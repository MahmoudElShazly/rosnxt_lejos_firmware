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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chunk/command header used in sensor protocol and in command protocol
 * 
 * The header is of fixed length (4 bytes). After reading the header, we can
 * read or skip its payload.
 * An incomplete/malformed header can cause the program to hang due to blocking
 * in a read() with no available data on the stream.
 * 
 * @author Federico Ferri
 *
 */
public class Header {
	public final byte device;
	public final byte port;
	public final byte type;
	public final byte length;
	
	public final static int BYTESIZE = 4;

	public Header(byte device, byte port, byte type, byte length) {
		this.device = device;
		this.port = port;
		this.type = type;
		this.length = length;
	}
	
	public static Header readFromStream(DataInputStream stream) throws IOException {
		return new Header(stream.readByte(), stream.readByte(), stream.readByte(), stream.readByte());
	}
	
	public static Header readFromStreamAsync(DataInputStream stream) throws IOException {
		return stream.available() >= 4 ? readFromStream(stream) : null;
	}
	
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeByte(device);
		stream.writeByte(port);
		stream.writeByte(type);
		stream.writeByte(length);
	}
}
