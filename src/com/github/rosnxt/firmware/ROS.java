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
import java.util.Queue;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;

/**
 * Main class for the ROS-NXT leJOS firmware
 * 
 * @author Federico Ferri
 *
 */
public class ROS {
	boolean run = true;

	Device device[] = new Device[8];
	
	DataInputStream inputStream;
	DataOutputStream outputStream;

	public void main() throws IOException {
		mainLoop:
		while(true) {
			boolean bt = false;
			
			LCD.clear();
			LCD.drawString(" <  BT", 0, 1);
			LCD.drawString("    USB  >", 0, 2);
			switch(Button.waitForAnyPress()) {
			case Button.ID_LEFT: bt = true; break;
			case Button.ID_RIGHT: bt = false; break;
			default: continue mainLoop;
			}
			LCD.clear();
			
			System.out.println("waiting " + (bt ? "bluetooth" : "usb"));
			NXTConnection conn = (bt ? Bluetooth.waitForConnection() : USB.waitForConnection());
			
			System.out.println("connected");
			
			DataInputStream inputStream = conn.openDataInputStream();
			DataOutputStream outputStream = conn.openDataOutputStream();
			
			run = true;
			
			while(run) {
				processCommands();
				pollDevices();
			}
			
			inputStream.close();
			outputStream.close();
			conn.close();
		}
	}
	
	void processCommands() throws IOException {
		Header h = Header.readFromStreamAsync(inputStream);
		
		if(h == null)
			return;
		
		for(int port = 0; port < device.length; port++) {
			if(device[port].matchHeader(h)) {
				// TODO: automatically ensure that no payload remains
				//       unread after command execution (protocol compliance)
				device[port].executeCommand(h, inputStream);
				break;
			}
		}
	}
	
	void pollDevices() throws IOException {
		for(int i = 0; i < device.length; i++) {
			device[i].poll(outputStream);
		}
	}

	public static void main(String[] args) throws Exception {new ROS().main();}
}
