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
package ch.bfh.unicrypt.crypto.schemes.hash.classes;

import ch.bfh.unicrypt.crypto.schemes.hash.abstracts.AbstractHashScheme;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.general.classes.FiniteByteArrayElement;
import ch.bfh.unicrypt.math.algebra.general.classes.FiniteByteArraySet;
import ch.bfh.unicrypt.math.function.classes.HashFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;
import ch.bfh.unicrypt.helper.hash.HashMethod;

public class StandardHashScheme
			 extends AbstractHashScheme<ByteArrayMonoid, ByteArrayElement, FiniteByteArraySet, FiniteByteArrayElement> {

	private final HashMethod hashMethod;

	protected StandardHashScheme(HashMethod hashMethod) {
		this.hashMethod = hashMethod;
	}

	public HashMethod getHashMethod() {
		return this.hashMethod;
	}

	@Override
	protected Function abstractGetHashFunction() {
		return HashFunction.getInstance(ByteArrayMonoid.getInstance(), this.getHashMethod());
	}

	public static StandardHashScheme getInstance(HashMethod hashMethod) {
		if (hashMethod == null) {
			throw new IllegalArgumentException();
		}
		return new StandardHashScheme(hashMethod);
	}

}
