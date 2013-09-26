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

import lejos.nxt.SensorPort;
import lejos.robotics.RegulatedMotor;
import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Abstract model of a device (sensor, motor or anything else).
 * 
 * A device produces data (typically, a sensor), which is periodically
 * polled (by the SensorThread).
 * 
 * A device also responds to commands (e.g.: a motor).
 * 
 * @author Federico Ferri
 *
 */
public abstract class Device {
	private final byte port;
	private final byte type;
	private DataSink dataSink;
	private final SensorThread sensorThread[];
	
	public Device(byte port, byte type) {
		this.port = port;
		this.type = type;
		this.sensorThread = new SensorThread[getNumSlots()];
	}
	
	public void setDataSink(DataSink dataSink) {
		this.dataSink = dataSink;
	}
	
	public void write(Data data) {
	}
	
	protected abstract int getNumSlots();
	
	public synchronized void startPoll(final byte slot, final int period) {
		if(slot < 0 || slot >= sensorThread.length) return;
		if(sensorThread[slot] != null) return;
		sensorThread[slot] = new SensorThread(slot, period);
		sensorThread[slot].start();
	}
	
	public void stopAllAsync() {
		for(int i = 0; i < sensorThread.length; i++)
			stopPollAsync(i);
	}
	
	public void stopPollAsync(final int slot) {
		sensorThread[slot] = null;
	}
	
	public synchronized void stopPoll(final int slot) {
		if(sensorThread[slot] == null) return;
		try {
			Thread t = sensorThread[slot];
			stopPollAsync(slot);
			t.join();
		} catch (InterruptedException e) {
		}
	}
	
	public Data getData(byte numSlot) {
		switch(numSlot) {
		case 0: return getData0();
		case 1: return getData1();
		case 2: return getData2();
		case 3: return getData3();
		case 4: return getData4();
		case 5: return getData5();
		case 6: return getData6();
		case 7: return getData7();
		case 8: return getData8();
		case 9: return getData9();
		default: return null;
		}
	}
	
	public Data getData0() {
		return null;
	}
	
	public Data getData1() {
		return null;
	}
	
	public Data getData2() {
		return null;
	}
	
	public Data getData3() {
		return null;
	}
	
	public Data getData4() {
		return null;
	}
	
	public Data getData5() {
		return null;
	}
	
	public Data getData6() {
		return null;
	}
	
	public Data getData7() {
		return null;
	}
	
	public Data getData8() {
		return null;
	}
	
	public Data getData9() {
		return null;
	}
	
	private class SensorThread extends Thread {
		private final byte numSlot;
		private final long pollPeriod;
		private long lastReadTime;
		
		public SensorThread(byte numSlot, long pollPeriod) {
			this.numSlot = numSlot;
			this.pollPeriod = pollPeriod;
			lastReadTime = 0;
		}
		
		@Override
		public void run() {
			while(sensorThread != null) {
				if(lastReadTime + pollPeriod < System.currentTimeMillis()) {
					Data data = getData(numSlot);
					data.port = port;
					data.slot = numSlot;
					data.type = type;
					dataSink.push(data);
					lastReadTime = System.currentTimeMillis();
				}
				Thread.yield();
			}
		}
	}
	
	protected SensorPort sensor(byte port) {
		switch(port) {
		case PORT_1: return SensorPort.S1;
		case PORT_2: return SensorPort.S2;
		case PORT_3: return SensorPort.S3;
		case PORT_4: return SensorPort.S4;
		}
		return null;
	}
	
	protected RegulatedMotor motor(byte port) {
		switch(port) {
		case PORT_A: return lejos.nxt.Motor.A;
		case PORT_B: return lejos.nxt.Motor.B;
		case PORT_C: return lejos.nxt.Motor.C;
		}
		return null;
	}
}
