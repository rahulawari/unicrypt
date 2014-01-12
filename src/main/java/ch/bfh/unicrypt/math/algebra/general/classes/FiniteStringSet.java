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
package ch.bfh.unicrypt.math.algebra.general.classes;

import ch.bfh.unicrypt.crypto.random.interfaces.RandomGenerator;
import ch.bfh.unicrypt.math.algebra.general.abstracts.AbstractSet;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.helper.Alphabet;
import java.math.BigInteger;

/**
 *
 * @author rolfhaenni
 */
public class FiniteStringSet
			 extends AbstractSet<FiniteStringElement> {

	private final Alphabet alphabet;
	private final int minLength;
	private final int maxLength;

	protected FiniteStringSet(Alphabet alphabet, int minLength, int maxLength) {
		this.alphabet = alphabet;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public int getMinLength() {
		return this.minLength;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public boolean fixedLength() {
		return this.getMinLength() == this.getMaxLength();
	}

	public Alphabet getAlphabet() {
		return this.alphabet;
	}

	public final FiniteStringElement getElement(final String string) {
		if (string == null || string.length() < this.getMinLength() || string.length() > this.getMaxLength()) {
			throw new IllegalArgumentException();
		}
		return this.standardGetElement(string);
	}

	protected FiniteStringElement standardGetElement(String string) {
		return new FiniteStringElement(this, string);
	}

	@Override
	protected FiniteStringElement abstractGetElement(BigInteger value) {
		int minLength = this.getMinLength();
		BigInteger size = BigInteger.valueOf(this.getAlphabet().getSize());
		StringBuilder strBuilder = new StringBuilder(this.getMaxLength());
		while (!value.equals(BigInteger.ZERO) || strBuilder.length() < minLength) {
			if (strBuilder.length() >= minLength) {
				value = value.subtract(BigInteger.ONE);
			}
			strBuilder.append(this.getAlphabet().getCharacter(value.mod(size).intValue()));
			value = value.divide(size);
		}
		return this.standardGetElement(strBuilder.reverse().toString());
	}

	@Override
	protected BigInteger abstractGetOrder() {
		BigInteger size = BigInteger.valueOf(this.getAlphabet().getSize());
		BigInteger order = BigInteger.ONE;
		for (int i = 0; i < this.getMaxLength() - this.getMinLength(); i++) {
			order = order.multiply(size).add(BigInteger.ONE);
		}
		return order.multiply(size.pow(this.getMinLength()));
	}

	@Override
	protected FiniteStringElement abstractGetRandomElement(RandomGenerator randomGenerator) {
		return this.abstractGetElement(randomGenerator.nextBigInteger(this.getOrder().subtract(BigInteger.ONE)));
	}

	@Override
	protected boolean abstractContains(BigInteger value) {
		return (value.signum() >= 0) && (value.compareTo(this.getOrder()) < 0);
	}

	@Override
	public boolean standardIsEquivalent(final Set set) {
		final FiniteStringSet other = (FiniteStringSet) set;
		return this.getAlphabet() == other.getAlphabet() && this.getMinLength() == other.getMinLength() && this.getMaxLength() == other.getMaxLength();
	}

	@Override
	public String standardToStringContent() {
		return this.getAlphabet().toString() + "^{" + this.getMinLength() + "..." + this.getMaxLength() + "}";
	}

	//
	// STATIC FACTORY METHODS
	//
	public static FiniteStringSet getInstance(final Alphabet alphabet, final int maxLength) {
		return FiniteStringSet.getInstance(alphabet, 0, maxLength);
	}

	public static FiniteStringSet getInstance(final Alphabet alphabet, final int minLength, final int maxLength) {
		if (alphabet == null || minLength < 0 || maxLength < minLength) {
			throw new IllegalArgumentException();
		}
		return new FiniteStringSet(alphabet, minLength, maxLength);
	}

	public static FiniteStringSet getInstance(final Alphabet alphabet, final BigInteger minOrder) {
		return FiniteStringSet.getInstance(alphabet, minOrder, 0);
	}

	public static FiniteStringSet getInstance(final Alphabet alphabet, final BigInteger minOrder, int minLength) {
		if (alphabet == null || minOrder == null || minOrder.signum() < 0 || minLength < 0) {
			throw new IllegalArgumentException();
		}
		int maxLength = minLength;
		BigInteger size = BigInteger.valueOf(alphabet.getSize());
		BigInteger order1 = size.pow(minLength);
		BigInteger order2 = BigInteger.ONE;
		while (order1.multiply(order2).compareTo(minOrder) < 0) {
			order2 = order2.multiply(size).add(BigInteger.ONE);
			maxLength++;
		}
		return new FiniteStringSet(alphabet, minLength, maxLength);
	}

}
