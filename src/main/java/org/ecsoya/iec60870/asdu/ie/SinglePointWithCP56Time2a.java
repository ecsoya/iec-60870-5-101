/*******************************************************************************
 * Copyright (C) 2019 Ecsoya (jin.liu@soyatec.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.ecsoya.iec60870.asdu.ie;

import org.ecsoya.iec60870.CP56Time2a;
import org.ecsoya.iec60870.Frame;
import org.ecsoya.iec60870.asdu.ASDUParsingException;
import org.ecsoya.iec60870.asdu.ApplicationLayerParameters;
import org.ecsoya.iec60870.asdu.TypeID;
import org.ecsoya.iec60870.asdu.ie.value.QualityDescriptor;

/**
 * Single point with CP56Time2a timestamp (M_SP_TB_1)
 */
public class SinglePointWithCP56Time2a extends SinglePointInformation {
	private CP56Time2a timestamp;

	public SinglePointWithCP56Time2a(ApplicationLayerParameters parameters, byte[] msg, int startIndex,
			boolean isSequence) throws ASDUParsingException {
		super(parameters, msg, startIndex, isSequence);
		if (!isSequence) {
			startIndex += parameters.getSizeOfIOA(); // skip IOA
		}

		if ((msg.length - startIndex) < getEncodedSize()) {
			throw new ASDUParsingException("Message too small");
		}

		startIndex += 1; // skip SIQ

		/* parse CP56Time2a (time stamp) */
		timestamp = new CP56Time2a(msg, startIndex);
	}

	public SinglePointWithCP56Time2a(int objectAddress, boolean value, QualityDescriptor quality,
			CP56Time2a timestamp) {
		super(objectAddress, value, quality);
		this.timestamp = timestamp;
	}

	@Override
	public void encode(Frame frame, ApplicationLayerParameters parameters, boolean isSequence) {
		super.encode(frame, parameters, isSequence);

		frame.appendBytes(timestamp.getEncodedValue());
	}

	@Override
	public int getEncodedSize() {
		return 8;
	}

	@Override
	public boolean getSupportsSequence() {
		return false;
	}

	public final CP56Time2a getTimestamp() {
		return this.timestamp;
	}

	@Override
	public TypeID getType() {
		return TypeID.M_SP_TB_1;
	}
}
