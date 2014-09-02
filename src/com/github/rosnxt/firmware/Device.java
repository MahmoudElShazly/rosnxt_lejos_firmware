package com.github.rosnxt.firmware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import static com.github.rosnxt.firmware.ProtocolConstants.*;

public abstract class Device {
	protected final byte device;
	protected final byte port;
	protected final PollingMachine pollingMachines[];
	
	public Device(byte device, byte port) {
		this(device, port, new PollingMachine[]{});
	}
	
	public Device(byte device, byte port, PollingMachine[] pollingMachines) {
		this.device = device;
		this.port = port;
		this.pollingMachines = pollingMachines;
	}

	public void poll(DataOutputStream outputStream) throws IOException {
		for(PollingMachine pollingMachine : pollingMachines) {
			pollingMachine.pollIfNeeded(outputStream);
		}
	}

	public abstract class PollingMachine {
		protected long lastPollTime = 0;
		protected int pollPeriod = 0;

		public void setPollPeriod(int pollPeriod) {
			this.pollPeriod = pollPeriod;
		}
		
		public void pollIfNeeded(DataOutputStream outputStream) throws IOException {
			long now = System.currentTimeMillis();
			if(pollPeriod > 0 && now - lastPollTime > pollPeriod)
				poll(outputStream);
		}

		public abstract void poll(DataOutputStream outputStream) throws IOException;
	}
	
	public Header header(byte type, byte length) {
		return new Header(device, port, type, length);
	}
	
	public Header header(byte type, int length) {
		return new Header(device, port, type, (byte)length);
	}
	
	protected static SensorPort getSensorPort(byte port) {
		switch(port) {
		case PORT_S1: return SensorPort.S1;
		case PORT_S2: return SensorPort.S2;
		case PORT_S3: return SensorPort.S3;
		case PORT_S4: return SensorPort.S4;
		default:      return null;
		}
	}
	
	protected static NXTRegulatedMotor getMotorPort(byte port) {
		switch(port) {
		case PORT_A: return Motor.A;
		case PORT_B: return Motor.B;
		case PORT_C: return Motor.C;
		default:     return null;
		}
	}

	public boolean matchHeader(Header header) {
		return port == header.port && device == header.device;
	}
	
	public void executeCommand(Header header, DataInputStream inputStream) throws IOException {
		// default method for unimplemented commands:
		// read and discard payload
		executeCommand(header, inputStream, 0);
	}
	
	public void executeCommand(Header header, DataInputStream inputStream, int numBytesAlreadyConsumed) throws IOException {
		for(int i = numBytesAlreadyConsumed; i < header.length; i++)
			inputStream.readByte();
	}
}