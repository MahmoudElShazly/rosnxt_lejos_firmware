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

import java.io.DataOutputStream;
import java.io.IOException;

import com.github.rosnxt.firmware.Device;
import com.github.rosnxt.firmware.drivers.HMC5883L;

import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Device for the Dexter Industries dCompass device
 * 
 * @author Federico Ferri
 *
 */
public class DCompass extends Device {
	private HMC5883L sensor;
	
	public DCompass(byte port) {
		super(DEV_DCOMPASS, port, new PollingMachine[1]);
		sensor = new HMC5883L(getSensorPort(port));
		pollingMachines[0] = new PollingMachine() {
			@Override
			public void poll(DataOutputStream outputStream) throws IOException {
				header(DATA_DCOMPASS_HEADING, Float.SIZE / Byte.SIZE).writeToStream(outputStream);
				outputStream.writeFloat(sensor.getDegreesCartesian());
				outputStream.flush();
			}
		};
	}
}
