/*
 * UniCrypt
 *
 *  UniCrypt(tm) : Cryptographical framework allowing the implementation of cryptographic protocols e.g. e-voting
 *  Copyright (C) 2014 Bern University of Applied Sciences (BFH), Research Institute for
 *  Security in the Information Society (RISIS), E-Voting Group (EVG)
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniCrypt may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH), Research Institute for
 *   Security in the Information Society (RISIS), E-Voting Group (EVG)
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: unicrypt@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.unicrypt.crypto.keygenerator.classes;

import ch.bfh.unicrypt.crypto.keygenerator.abstracts.AbstractKeyPairGenerator;
import ch.bfh.unicrypt.helper.converter.classes.biginteger.BigIntegerToBigInteger;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.StringToByteArray;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrimePair;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.ZStarMod;
import ch.bfh.unicrypt.math.function.classes.CompositeFunction;
import ch.bfh.unicrypt.math.function.classes.ConvertFunction;
import ch.bfh.unicrypt.math.function.classes.InvertFunction;
import ch.bfh.unicrypt.math.function.classes.RandomFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

/**
 *
 * @author rolfhaenni
 */
public class RSAKeyGenerator
	   extends AbstractKeyPairGenerator<ZMod, ZModElement, ZMod, ZModElement> {

	private final ZModPrimePair zModPrimePair;
	private final ZStarMod zStarMod;

	protected RSAKeyGenerator(ZModPrimePair zModPrimePair, StringToByteArray stringConverter) {
		super(zModPrimePair.getZModOrder(), stringConverter);
		this.zModPrimePair = zModPrimePair;
		this.zStarMod = ZStarMod.getInstance(zModPrimePair.getZStarModOrder().getOrder());
	}

	public ZModPrimePair getZModPrimes() {
		return this.zModPrimePair;
	}

	@Override
	protected Function defaultGetPrivateKeyGenerationFunction() {
		return CompositeFunction.getInstance(
			   RandomFunction.getInstance(this.zStarMod),
			   ConvertFunction.getInstance(this.zStarMod, this.zModPrimePair, BigIntegerToBigInteger.getInstance()));
	}

	@Override
	protected Function abstractGetPublicKeyGenerationFunction() {
		return CompositeFunction.getInstance(
			   ConvertFunction.getInstance(this.zModPrimePair, this.zStarMod, BigIntegerToBigInteger.getInstance()),
			   InvertFunction.getInstance(this.zStarMod),
			   ConvertFunction.getInstance(this.zStarMod, this.zModPrimePair, BigIntegerToBigInteger.getInstance()));
	}

	public static RSAKeyGenerator getInstance(ZModPrimePair zModPrimePair) {
		return RSAKeyGenerator.getInstance(zModPrimePair, StringToByteArray.getInstance());
	}

	public static RSAKeyGenerator getInstance(ZModPrimePair zModPrimePair, StringToByteArray stringConverter) {
		if (zModPrimePair == null || stringConverter == null) {
			throw new IllegalArgumentException();
		}
		return new RSAKeyGenerator(zModPrimePair, stringConverter);
	}

}