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

package com.github.rosnxt.firmware.devices;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.RegulatedMotor;

import com.github.rosnxt.firmware.Device;
import com.github.rosnxt.firmware.Header;

import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Device for the LEGO NXT motor
 * 
 * @author Federico Ferri
 *
 */
public class Motor extends Device {
	RegulatedMotor motor;
	
	public Motor(byte port) {
		super(DEV_MOTOR, port, new PollingMachine[1]);
		this.motor = getMotorPort(port);
		pollingMachines[0] = new PollingMachine() {
			@Override
			public void poll(DataOutputStream outputStream) throws IOException {
				header(DATA_MOTOR_TACHO, Integer.SIZE / Byte.SIZE).writeToStream(outputStream);
				outputStream.writeInt(motor.getTachoCount());
				outputStream.flush();
			}
		};
	}
	
	@Override
	public void executeCommand(Header header, DataInputStream inputStream) throws IOException {
		int bytesConsumed = 0;
		byte dir; int angle;
		switch(header.type) {
		case CMD_MOTOR_ROTATE:
			dir = inputStream.readByte();
			System.out.println("rotate " + dir);
			bytesConsumed += 1;
			if(dir > 0) motor.forward();
			else if(dir < 0) motor.backward();
			else motor.flt(true);
			break;
		case CMD_MOTOR_ROTATE_BY:
			angle = inputStream.readInt();
			System.out.println("rotateBy " + angle);
			bytesConsumed += Integer.SIZE / Byte.SIZE;
			motor.rotate(angle, true);
			break;
		case CMD_MOTOR_ROTATE_TO:
			angle = inputStream.readInt();
			System.out.println("rotateTo " + angle);
			bytesConsumed += Integer.SIZE / Byte.SIZE;
			motor.rotateTo(angle, true);
			break;
		case CMD_MOTOR_FLT:
			System.out.println("flt");
			motor.flt(true);
			break;
		case CMD_MOTOR_STOP:
			System.out.println("stop");
			motor.stop(true);
			break;
		case CMD_MOTOR_SET_SPEED:
			System.out.println("setSpeed " + "#");
			motor.setSpeed(Math.abs(inputStream.readInt()));
			bytesConsumed += Integer.SIZE / Byte.SIZE;
			break;
		case CMD_MOTOR_SET_ACCEL:
			System.out.println("setAccel " + "#");
			motor.setAcceleration(Math.abs(inputStream.readInt()));
			bytesConsumed += Integer.SIZE / Byte.SIZE;
			break;
		case CMD_MOTOR_SET_STALL_THRESHOLD:
			System.out.println("setStallTh " + "# #");
			motor.setStallThreshold(inputStream.readInt(), inputStream.readInt());
			bytesConsumed += 2 * (Integer.SIZE / Byte.SIZE);
			break;
		}
		super.executeCommand(header, inputStream, bytesConsumed);
	}
}
