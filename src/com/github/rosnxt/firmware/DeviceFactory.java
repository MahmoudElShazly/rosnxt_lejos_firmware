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

import static com.github.rosnxt.firmware.ProtocolConstants.*;

import com.github.rosnxt.firmware.devices.Color;
import com.github.rosnxt.firmware.devices.DCompass;
import com.github.rosnxt.firmware.devices.DIMU;
import com.github.rosnxt.firmware.devices.Diagnostics;
import com.github.rosnxt.firmware.devices.IRLink;
import com.github.rosnxt.firmware.devices.Light;
import com.github.rosnxt.firmware.devices.Motor;
import com.github.rosnxt.firmware.devices.MuxTouch;
import com.github.rosnxt.firmware.devices.Sound;
import com.github.rosnxt.firmware.devices.Touch;
import com.github.rosnxt.firmware.devices.Ultrasonic;

/**
 * 
 * @author Federico Ferri
 *
 */
public class DeviceFactory {
	public static Device createSensor(byte type, byte port, DataSink dataSink) {
		Device r = null;
		switch(type) {
		case TYPE_COLOR:       r = new Color(port); break;
		case TYPE_COMPASS:     r = new DCompass(port); break;
		case TYPE_DIAGNOSTICS: r = new Diagnostics(); break;
		case TYPE_IMU:         r = new DIMU(port); break;
		case TYPE_IRLINK:      r = new IRLink(port); break;
		case TYPE_LIGHT:       r = new Light(port); break;
		case TYPE_MOTOR:       r = new Motor(port); break;
		case TYPE_MUXTOUCH:    r = new MuxTouch(port); break;
		case TYPE_SOUND:       r = new Sound(port); break;
		case TYPE_TOUCH:       r = new Touch(port); break;
		case TYPE_ULTRASONIC:  r = new Ultrasonic(port); break;
		}
		if(r != null)
			r.setDataSink(dataSink);
		return r;
	}
}
