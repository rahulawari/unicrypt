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
package ch.bfh.unicrypt.math.algebra.dualistic.abstracts;

import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.Ring;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;

/**
 * This abstract class provides a basis implementation for objects of type {@link Ring}.
 * <p>
 * @param <E> Generic type of the elements of this ring
 * @param <V> Generic type of values stored in the elements of this ring
 * @author rolfhaenni
 */
public abstract class AbstractRing<E extends DualisticElement<V>, V extends Object>
	   extends AbstractSemiRing<E, V>
	   implements Ring<V> {

	public AbstractRing(Class<? extends Object> valueClass) {
		super(valueClass);
	}

	@Override
	public final E invert(final Element element) {
		if (!this.contains(element)) {
			throw new IllegalArgumentException();
		}
		return this.abstractInvert((E) element);
	}

	@Override
	public final E applyInverse(Element element1, Element element2) {
		return this.apply(element1, this.invert(element2));
	}

	@Override
	public final E subtract(final Element element1, final Element element2) {
		return this.applyInverse(element1, element2);
	}

	@Override
	public final E negate(final Element element) {
		return this.invert(element);
	}

	@Override
	protected E defaultSelfApply(E element, BigInteger amount) {
		boolean negAmount = (amount.signum() < 0);
		amount = amount.abs();
		if (this.isFinite() && this.hasKnownOrder()) {
			amount = amount.mod(this.getOrder());
		}
		if (amount.signum() == 0) {
			return this.getIdentityElement();
		}
		E result = this.defaultSelfApplyAlgorithm(element, amount);
		if (negAmount) {
			return this.invert(result);
		}
		return result;
	}

	//
	// The following protected abstract method must be implemented in every direct sub-class.
	//
	protected abstract E abstractInvert(E element);

}
