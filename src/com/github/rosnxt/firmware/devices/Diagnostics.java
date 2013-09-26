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

import lejos.nxt.Battery;
import lejos.nxt.Button;

import com.github.rosnxt.firmware.Data;
import com.github.rosnxt.firmware.Device;

import static com.github.rosnxt.firmware.ProtocolConstants.*;

/**
 * Device for reading diagnostic information from the LEGO NXT brick
 * 
 * @author Federico Ferri
 *
 */
public class Diagnostics extends Device {
	public Diagnostics() {
		super(PORT_MISC, TYPE_DIAGNOSTICS);
	}

	@Override
	protected int getNumSlots() {
		return 3;
	}
	
	@Override
	public Data getData0() {
		return new Data(new int[]{
			Battery.getVoltageMilliVolt()
		});
	}
	
	@Override
	public Data getData1() {
		return new Data(new int[]{
			(int)System.getRuntime().freeMemory(),
		});
	}
	
	@Override
	public Data getData2() {
		return new Data(new int[]{
			Button.ENTER.isDown()  ? 1 : 0,
			Button.ESCAPE.isDown() ? 1 : 0,
			Button.LEFT.isDown()   ? 1 : 0,
			Button.RIGHT.isDown()  ? 1 : 0
		});
	}
}
