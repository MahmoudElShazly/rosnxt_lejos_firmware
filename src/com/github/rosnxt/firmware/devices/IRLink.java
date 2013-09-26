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

import com.github.rosnxt.firmware.Data;
import com.github.rosnxt.firmware.Device;
import com.github.rosnxt.firmware.drivers.IRLinkExt;

import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Device for the HiTechnic IRLink sensor
 * 
 * @author Federico Ferri
 *
 */
public class IRLink extends Device {
	private IRLinkExt sensor;

	public IRLink(byte port) {
		super(port, TYPE_IRLINK);
		sensor = new IRLinkExt(sensor(port));
	}

	@Override
	public void write(Data data) {
		int v[] = data.intValues;
		if(v.length == 4 && v[0] == IRLINK_COMBO_DIRECT) {
			sensor.sendPFComboDirect(v[1], v[2], v[3]);
		} else if(v.length == 4 && v[0] == IRLINK_COMBO_PWM) {
			sensor.sendPFComboPmw(v[1], v[2], v[3]);
		} else if(v.length == 3 && v[0] == IRLINK_EXTENDED) {
			sensor.sendPFExtended(v[1], v[2]);
		} else if(v.length == 4 && v[0] == IRLINK_SINGLE_CST) {
			sensor.sendPFSingleModeCST(v[1], v[2], v[3]);
		} else if(v.length == 4 && v[0] == IRLINK_SINGLE_PWM) {
			sensor.sendPFSingleModePWM(v[1], v[2], v[3]);
		}
	}
	
	@Override
	protected int getNumSlots() {
		return 0;
	}
}
