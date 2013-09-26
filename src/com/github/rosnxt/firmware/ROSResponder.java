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
public class ROSResponder {
	private static boolean blueTooth = false;
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private static boolean run = true;

	private static Queue<Data> readQueue = new Queue<Data>();
	
	private static Device device[] = new Device[9];
	
	private static DataSink dataSink = new DataSink() {
		@Override
		public void push(Data r) {
			synchronized(readQueue) {
				readQueue.push(r);
			}
		}
	};
	
	private static void flushReadQueue() throws IOException {
		final int MAX_CHUNK_SIZE = 63;
		synchronized(readQueue) {
			int wr = 0;
			while(!readQueue.empty()) {
				Data data = (Data)readQueue.pop();
				
				// try to combine several data chunks in the same packet, if possible
				int pktSize = 6 + 4 * (data.intValues.length + data.floatValues.length);
				if((wr + pktSize) > MAX_CHUNK_SIZE) {
					dos.flush();
					wr = 0;
				}
				
				dos.writeByte(HDR_DATA);
				dos.writeByte(data.port);
				dos.writeByte(data.slot);
				dos.writeByte(data.type);
				dos.writeByte((byte)data.intValues.length);
				for(int i = 0; i < data.intValues.length; i++)
					dos.writeInt(data.intValues[i]);
				dos.writeByte((byte)data.floatValues.length);
				for(int i = 0; i < data.floatValues.length; i++)
					dos.writeFloat(data.floatValues[i]);
				
				wr += pktSize;
			}
			dos.flush();
		}
	}
	
	private static void processOneCommand() throws IOException {
		byte cmd = dis.readByte(), port, slot;
		switch(cmd) {
		case CMD_SETUP_PORT:
			port = dis.readByte();
			byte type = dis.readByte();
			if(device[port] != null)
				device[port].stopAllAsync();
			device[port] = DeviceFactory.createSensor(type, port, dataSink);
			break;
		case CMD_START_POLLING:
			port = dis.readByte();
			slot = dis.readByte();
			int period = dis.readInt();
			if(device[port] != null) device[port].startPoll(slot, period);
			break;
		case CMD_STOP_POLLING:
			port = dis.readByte();
			slot = dis.readByte();
			if(device[port] != null)
				device[port].stopPollAsync(slot);
			break;
		case CMD_WRITE:
			port = dis.readByte();
			if(device[port] != null) {
				byte ni = dis.readByte();
				int iv[] = new int[ni];
				for(int i = 0; i < ni; i++)
					iv[i] = dis.readInt();
				byte nf = dis.readByte();
				float fv[] = new float[nf];
				for(int i = 0; i < nf; i++)
					fv[i] = dis.readFloat();
				device[port].write(new Data(iv, fv));
			}
			break;
		case CMD_SHUT_DOWN:
			run = false;
			break;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		while(true) {
			System.out.println("waiting " + (blueTooth ? "bluetooth" : "usb"));
			NXTConnection conn = ((blueTooth)
					? Bluetooth.waitForConnection()
			        : USB.waitForConnection());
			
			System.out.println("connected");
			
		    dis = conn.openDataInputStream();
			dos = conn.openDataOutputStream();
			
			Thread t = new Thread() {
				public void run() {
					while(run) try {flushReadQueue();} catch (IOException e) {}
				}
			};
			t.start();
			
			while(run) processOneCommand();

			// shutdown polling threads
			for(int i = 0; i < device.length; i++) {
				if(device[i] != null) {
					device[i].stopAllAsync();
					device[i] = null;
				}
			}
			
			// shutdown flush read queue thread
			t.join();
			
			readQueue.clear();
			
			dis.close();
			dos.close();
			conn.close();
		}
	}
}