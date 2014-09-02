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
import java.io.IOException;

import com.github.rosnxt.firmware.Device;
import com.github.rosnxt.firmware.Header;
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
		super(DEV_IRLINK, port, new PollingMachine[0]);
		sensor = new IRLinkExt(getSensorPort(port));
	}

	@Override
	public void executeCommand(Header header, DataInputStream inputStream) throws IOException {
		int bytesConsumed = 0, a, b, c;
		switch(header.type) {
		case CMD_IRLINK_SEND_EXTENDED:
			a = inputStream.readInt();
			b = inputStream.readInt();
			bytesConsumed += 2 * Integer.SIZE / Byte.SIZE;
			sensor.sendPFExtended(a, b);
			break;
		case CMD_IRLINK_SEND_SINGLE_CST:
			a = inputStream.readInt();
			b = inputStream.readInt();
			c = inputStream.readInt();
			bytesConsumed += 3 * Integer.SIZE / Byte.SIZE;
			sensor.sendPFSingleModeCST(a, b, c);
			break;
		case CMD_IRLINK_SEND_SINGLE_PWM:
			a = inputStream.readInt();
			b = inputStream.readInt();
			c = inputStream.readInt();
			bytesConsumed += 3 * Integer.SIZE / Byte.SIZE;
			sensor.sendPFSingleModePWM(a, b, c);
			break;
		case CMD_IRLINK_SEND_COMBO_DIRECT:
			a = inputStream.readInt();
			b = inputStream.readInt();
			c = inputStream.readInt();
			bytesConsumed += 3 * Integer.SIZE / Byte.SIZE;
			sensor.sendPFComboDirect(a, b, c);
			break;
		case CMD_IRLINK_SEND_COMBO_PWM:
			a = inputStream.readInt();
			b = inputStream.readInt();
			c = inputStream.readInt();
			bytesConsumed += 3 * Integer.SIZE / Byte.SIZE;
			sensor.sendPFComboPmw(a, b, c);
			break;
		}
		super.executeCommand(header, inputStream, bytesConsumed);
	}
}
