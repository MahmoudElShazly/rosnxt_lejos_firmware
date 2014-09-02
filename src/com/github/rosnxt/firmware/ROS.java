/*
 * Copyright (c) 2013, Federico Ferri
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 *   Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 * 
 *   Neither the name of the {organization} nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.rosnxt.firmware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.rosnxt.firmware.devices.Color;
import com.github.rosnxt.firmware.devices.DCompass;
import com.github.rosnxt.firmware.devices.DIMU;
import com.github.rosnxt.firmware.devices.IRLink;
import com.github.rosnxt.firmware.devices.Light;
import com.github.rosnxt.firmware.devices.Motor;
import com.github.rosnxt.firmware.devices.MuxTouch;
import com.github.rosnxt.firmware.devices.Sound;
import com.github.rosnxt.firmware.devices.Touch;
import com.github.rosnxt.firmware.devices.Ultrasonic;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;
import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Main class for the ROS-NXT leJOS firmware
 * 
 * @author Federico Ferri
 *
 */
public class ROS {
	boolean run;

	Device device[] = new Device[8];
	
	NXTConnection connection;
	
	DataInputStream inputStream;
	DataOutputStream outputStream;

	public ROS(boolean bluetooth) {
		System.out.println("waiting " + (bluetooth ? "bluetooth" : "usb"));
		connection = (bluetooth ? Bluetooth.waitForConnection() : USB.waitForConnection());
		System.out.println("connected");

		run = true;
		
		inputStream = connection.openDataInputStream();
		outputStream = connection.openDataOutputStream();
	}
	
	public void run() throws IOException {
		try {
			while(run) {
				processCommands();
				pollDevices();
			}
		} catch(IOException ex) {
		} finally {
			inputStream.close();
			outputStream.close();
			connection.close();
		}
	}
	
	void processCommands() throws IOException {
		Header h = Header.readFromStreamAsync(inputStream);
		
		if(h == null)
			return;
		
		if(h.device == DEV_SYSTEM) {
			byte t = -1; int z = -1;
			switch(h.type) {
			case CMD_SYSTEM_SET_DEVICE_TYPE:
				t = inputStream.readByte();
				switch(t) {
				case DEV_NULL:
					if(isMotorPort(h.port) || isSensorPort(h.port))
						device[h.port] = null;
					break;
				case DEV_MOTOR:
					if(isMotorPort(h.port))
						device[h.port] = new Motor(h.port);
					break;
				case DEV_TOUCH:
					if(isSensorPort(h.port))
						device[h.port] = new Touch(h.port);
					break;
				case DEV_SOUND:
					if(isSensorPort(h.port))
						device[h.port] = new Sound(h.port);
					break;
				case DEV_LIGHT:
					if(isSensorPort(h.port))
						device[h.port] = new Light(h.port);
					break;
				case DEV_COLOR:
					if(isSensorPort(h.port))
						device[h.port] = new Color(h.port);
					break;
				case DEV_ULTRASONIC:
					if(isSensorPort(h.port))
						device[h.port] = new Ultrasonic(h.port);
					break;
				case DEV_TOUCHMUX:
					if(isSensorPort(h.port))
						device[h.port] = new MuxTouch(h.port);
					break;
				case DEV_IRLINK:
					if(isSensorPort(h.port))
						device[h.port] = new IRLink(h.port);
					break;
				case DEV_DIMU:
					if(isSensorPort(h.port))
						device[h.port] = new DIMU(h.port);
					break;
				case DEV_DCOMPASS:
					if(isSensorPort(h.port))
						device[h.port] = new DCompass(h.port);
					break;
				}
				break;
			case CMD_SYSTEM_SET_POLL_PERIOD:
				t = inputStream.readByte();
				z = inputStream.readInt();
				if(device[h.port] != null)
					device[h.port].pollingMachines[t].setPollPeriod(z);
				break;
			}
		} else {
			for(int port = 0; port < device.length; port++) {
				if(device[port] == null) continue;
				
				if(device[port].matchHeader(h)) {
					// TODO: automatically ensure that no payload remains
					//       unread after command execution (protocol compliance)
					device[port].executeCommand(h, inputStream);
					break;
				}
			}
		}
	}
	
	boolean isMotorPort(byte port) {
		return port == PORT_A || port == PORT_B || port == PORT_C;
	}
	
	boolean isSensorPort(byte port) {
		return port == PORT_S1 || port == PORT_S2 || port == PORT_S3 || port == PORT_S4;
	}
	
	void pollDevices() throws IOException {
		for(int i = 0; i < device.length; i++) {
			if(device[i] == null) continue;
			
			device[i].poll(outputStream);
		}
	}

	public static void main(String[] args) throws Exception {
		boolean bt = false;
		
		selectConnectionMethod:
		while(true) {
			LCD.clear();
			LCD.drawString(" <  BT", 0, 1);
			LCD.drawString("    USB  >", 0, 2);
			switch(Button.waitForAnyPress()) {
			case Button.ID_LEFT: bt = true; break selectConnectionMethod;
			case Button.ID_RIGHT: bt = false; break selectConnectionMethod;
			}
		}
		
		LCD.clear();
		
		ROS app = new ROS(bt);
		app.run();
	}
}
