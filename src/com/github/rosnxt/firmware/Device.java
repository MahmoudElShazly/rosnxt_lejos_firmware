package com.github.rosnxt.firmware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.rosnxt.firmware.devices.*;

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
	
	protected static lejos.nxt.SensorPort getSensorPort(byte port) {
		switch(port) {
		case PORT_S1: return lejos.nxt.SensorPort.S1;
		case PORT_S2: return lejos.nxt.SensorPort.S2;
		case PORT_S3: return lejos.nxt.SensorPort.S3;
		case PORT_S4: return lejos.nxt.SensorPort.S4;
		default:      return null;
		}
	}
	
	protected static lejos.nxt.NXTRegulatedMotor getMotorPort(byte port) {
		switch(port) {
		case PORT_A: return lejos.nxt.Motor.A;
		case PORT_B: return lejos.nxt.Motor.B;
		case PORT_C: return lejos.nxt.Motor.C;
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
	
	public static Device factory(byte deviceType, byte port) {
		switch(deviceType) {
		case DEV_NULL: return null;
		case DEV_MOTOR: return new Motor(port);
		case DEV_TOUCH: return new Touch(port);
		case DEV_SOUND: return new Sound(port);
		case DEV_LIGHT: return new Light(port);
		case DEV_COLOR: return new Color(port);
		case DEV_ULTRASONIC: return new Ultrasonic(port);
		case DEV_TOUCHMUX: return new MuxTouch(port);
		case DEV_IRLINK: return new IRLink(port);
		case DEV_DIMU: return new DIMU(port);
		case DEV_DCOMPASS: return new DCompass(port);
		}
		return null;
	}
}