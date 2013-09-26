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

import lejos.robotics.RegulatedMotor;

import com.github.rosnxt.firmware.Data;
import com.github.rosnxt.firmware.Device;

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
		super(port, TYPE_MOTOR);
		this.motor = motor(port);
	}
	
	@Override
	public void write(Data data) {
		int v[] = data.intValues;
		if(v.length >= 2 && (v[0] == MOTOR_ROTATE_BY || v[0] == MOTOR_ROTATE_TO)) {
			if(v.length >= 3) motor.setSpeed(v[2]);
			if(v.length >= 4) motor.setAcceleration(v[3]);
			if(v[0] == MOTOR_ROTATE_BY) motor.rotate(v[1], true);
			else motor.rotateTo(v[1], true);
		} else if(v.length == 1 && v[0] == MOTOR_FLOAT) {
			motor.flt(true);
		} else if(v.length == 1 && v[0] == MOTOR_STOP) {
			motor.stop(true);
		} else if(v.length == 2 && v[0] == MOTOR_SET_SPEED) {
			motor.setSpeed(Math.abs(v[1]));
		} else if(v.length == 2 && v[0] == MOTOR_SET_ACCEL) {
			motor.setAcceleration(Math.abs(v[1]));
		} else if(v.length == 3 && v[0] == MOTOR_SET_STALL_TRESHOLD) {
			motor.setStallThreshold(v[1], v[2]);
		} else if(v.length == 1 && v[0] == MOTOR_FORWARD) {
			motor.forward();
		} else if(v.length == 1 && v[0] == MOTOR_BACKWARD) {
			motor.backward();
		}
	}
	
	@Override
	protected int getNumSlots() {
		return 1;
	}
	
	@Override
	public Data getData0() {
		return new Data(new int[]{
			motor.isMoving() ? 1 : 0,
			motor.isStalled() ? 1 : 0,
			motor.getTachoCount(),
			0,//motor.getSpeed(),
			0,//motor.getRotationSpeed(),
			0//motor.getLimitAngle()
		});
	}
}
