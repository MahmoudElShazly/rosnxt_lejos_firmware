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
		System.out.println("waiting " + (bluetooth ? "bt" : "usb") + "...");
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
		
		System.out.println("h " + h.device + " " + h.port + " " + h.type + " " + h.length);
		
		if(h.device == DEV_SYSTEM) {
			byte t = -1; int z = -1;
			int bytesConsumed = 0;
			switch(h.type) {
			case CMD_SYSTEM_SET_DEVICE_TYPE:
				t = inputStream.readByte();
				System.out.println("setDevType " + t);
				bytesConsumed += 1;
				setDeviceType(h.port, t);
				break;
			case CMD_SYSTEM_SET_POLL_PERIOD:
				t = inputStream.readByte();
				z = inputStream.readInt();
				System.out.println("setPollT " + t + " " + z);
				bytesConsumed += 1 + Integer.SIZE/Byte.SIZE;
				setPollPeriod(h.port, t, z);
				break;
			default:
				System.out.println("bad command");
				break;
			}
			for(int i = bytesConsumed; i < h.length; i++)
				inputStream.readByte();
		} else {
			boolean handled = false;
			
			for(int port = 0; port < device.length; port++) {
				if(device[port] == null) continue;
				
				if(device[port].matchHeader(h)) {
					// TODO: automatically ensure that no payload remains
					//       unread after command execution (protocol compliance)
					device[port].executeCommand(h, inputStream);
					handled = true;
					break;
				}
			}
			
			if(!handled) {
				System.out.println("unhandled");
				for(int i = 0; i < h.length; i++)
					inputStream.readByte();
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
	
	public void setDeviceType(byte port, byte deviceType) {
		device[port] = Device.factory(deviceType, port);
	}
	
	public void setPollPeriod(byte port, byte subport, int period) {
		if(device[port] != null)
			device[port].pollingMachines[subport].setPollPeriod(period);
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
