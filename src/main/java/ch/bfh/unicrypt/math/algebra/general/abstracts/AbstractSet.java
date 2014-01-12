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
package ch.bfh.unicrypt.math.algebra.general.abstracts;

import ch.bfh.unicrypt.crypto.random.classes.PseudoRandomGenerator;
import ch.bfh.unicrypt.crypto.random.interfaces.RandomGenerator;
import ch.bfh.unicrypt.math.algebra.additive.interfaces.AdditiveSemiGroup;
import ch.bfh.unicrypt.math.algebra.concatenative.interfaces.ConcatenativeSemiGroup;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.Field;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.Ring;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.SemiRing;
import ch.bfh.unicrypt.math.algebra.general.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Monoid;
import ch.bfh.unicrypt.math.algebra.general.interfaces.SemiGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.ZStarMod;
import ch.bfh.unicrypt.math.algebra.multiplicative.interfaces.MultiplicativeSemiGroup;
import ch.bfh.unicrypt.math.helper.Compound;
import ch.bfh.unicrypt.math.helper.UniCrypt;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This abstract class provides a basis implementation for atomic sets.
 * <p>
 * @param <E>
 * @see AbstractElement
 * <p>
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public abstract class AbstractSet<E extends Element>
			 extends UniCrypt
			 implements Set, Iterable<E> {

	private BigInteger order, lowerBound, upperBound, minimum;

	@Override
	public final boolean isSemiGroup() {
		return this instanceof SemiGroup;
	}

	@Override
	public final boolean isMonoid() {
		return this instanceof Monoid;
	}

	@Override
	public final boolean isGroup() {
		return this instanceof Group;
	}

	@Override
	public final boolean isSemiRing() {
		return this instanceof SemiRing;
	}

	@Override
	public final boolean isRing() {
		return this instanceof Ring;
	}

	@Override
	public final boolean isField() {
		return this instanceof Field;
	}

	@Override
	public final boolean isCyclic() {
		return this instanceof CyclicGroup;
	}

	@Override
	public boolean isAdditive() {
		return this instanceof AdditiveSemiGroup;
	}

	@Override
	public boolean isMultiplicative() {
		return this instanceof MultiplicativeSemiGroup;
	}

	@Override
	public boolean isConcatenative() {
		return this instanceof ConcatenativeSemiGroup;
	}

	@Override
	public final boolean isProduct() {
		return this instanceof Compound;
	}

	@Override
	public final boolean isFinite() {
		return !this.getOrder().equals(Set.INFINITE_ORDER);
	}

	@Override
	public final boolean hasKnownOrder() {
		return !this.getOrder().equals(Set.UNKNOWN_ORDER);
	}

	@Override
	public final BigInteger getOrder() {
		if (this.order == null) {
			this.order = this.abstractGetOrder();
		}
		return this.order;
	}

	@Override
	public final BigInteger getOrderLowerBound() {
		if (this.lowerBound == null) {
			if (this.hasKnownOrder()) {
				this.lowerBound = this.getOrder();
			} else {
				this.lowerBound = this.standardGetOrderLowerBound();
			}
		}
		return this.lowerBound;
	}

	@Override
	public final BigInteger getOrderUpperBound() {
		if (this.upperBound == null) {
			if (this.hasKnownOrder()) {
				this.upperBound = this.getOrder();
			} else {
				this.upperBound = this.standardGetOrderUpperBound();
			}
		}
		return this.upperBound;
	}

	@Override
	public final BigInteger getMinimalOrder() {
		if (this.minimum == null) {
			this.minimum = this.standardGetMinimalOrder();
		}
		return this.minimum;
	}

	@Override
	public final boolean isEmpty() {
		return this.getOrder().equals(BigInteger.ZERO);
	}

	@Override
	public final boolean isSingleton() {
		return this.getOrder().equals(BigInteger.ONE);
	}

	@Override
	public final ZMod getZModOrder() {
		if (!(this.isFinite() && this.hasKnownOrder())) {
			throw new UnsupportedOperationException();
		}
		return this.standardGetZModOrder();
	}

	@Override
	public final ZStarMod getZStarModOrder() {
		if (!(this.isFinite() && this.hasKnownOrder())) {
			throw new UnsupportedOperationException();
		}
		return this.standardGetZStarModOrder();
	}

	@Override
	public final boolean contains(final int value) {
		return this.contains(BigInteger.valueOf(value));
	}

	@Override
	public final boolean contains(final BigInteger value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		return this.abstractContains(value);
	}

	@Override
	public final boolean contains(final Element element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		return this.standardContains(element);
	}

	@Override
	public final E getElement(final int value) {
		return this.getElement(BigInteger.valueOf(value));
	}

	@Override
	public final E getElement(BigInteger value) {
		if (value == null || !this.contains(value)) {
			throw new IllegalArgumentException();
		}
		return this.abstractGetElement(value);
	}

	@Override
	public final E getElement(final Element element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		if (this.contains(element)) {
			return (E) element;
		}
		return this.getElement(element.getValue());
	}

	@Override
	public final E getRandomElement() {
		return this.getRandomElement(null);
	}

	@Override
	public final E getRandomElement(RandomGenerator randomGenerator) {
		if (randomGenerator == null) {
			randomGenerator = PseudoRandomGenerator.DEFAULT;
		}
		return this.abstractGetRandomElement(randomGenerator);
	}

	@Override
	public final boolean areEqual(final Element element1, final Element element2) {
		if (!this.contains(element1) || !this.contains(element2)) {
			throw new IllegalArgumentException();
		}
		return element1.isEquivalent(element2);
	}

	@Override
	public final boolean isCompatible(Set set) {
		if (set == null) {
			throw new IllegalArgumentException();
		}
		return standardIsCompatible(set);
	}

	@Override
	public final boolean isEquivalent(final Set set) {
		if (set == null) {
			throw new IllegalArgumentException();
		}
		if (this == set) {
			return true;
		}
		if (!this.isCompatible(set)) {
			return false;
		}
		return this.standardIsEquivalent(set);
	}

	@Override
	public final Iterator<E> iterator() {
		return this.standardIterator();
	}

	//
	// The following protected methods are standard implementations for sets.
	// They may need to be changed in certain sub-classes.
	//
	protected ZMod standardGetZModOrder() {
		return ZMod.getInstance(this.getOrder());
	}

	protected ZStarMod standardGetZStarModOrder() {
		return ZStarMod.getInstance(this.getOrder());
	}

	protected BigInteger standardGetOrderLowerBound() {
		return BigInteger.ZERO;
	}

	protected BigInteger standardGetOrderUpperBound() {
		return Set.INFINITE_ORDER;
	}

	protected BigInteger standardGetMinimalOrder() {
		return this.getOrderLowerBound();
	}

	protected boolean standardIsCompatible(Set set) {
		return this.getClass() == set.getClass();
	}

	protected boolean standardIsEquivalent(Set set) {
		return true;
	}

	protected boolean standardContains(final Element element) {
		return this.isEquivalent(element.getSet());
	}

	protected Iterator<E> standardIterator() {
		final AbstractSet<E> set = this;
		return new Iterator<E>() {

			BigInteger counter = BigInteger.ZERO;
			BigInteger currentValue = BigInteger.ZERO;

			@Override
			public boolean hasNext() {
				if (set.hasKnownOrder()) {
					if (set.isFinite()) {
						return counter.compareTo(set.getOrder()) < 0;
					}
					return true;
				}
				return false; // the standard iterator does not work for groups of unknonw order
			}

			@Override
			public E next() {
				if (this.hasNext()) {
					while (!set.contains(currentValue)) {
						this.currentValue = this.currentValue.add(BigInteger.ONE);
					}
					E element = set.abstractGetElement(currentValue);
					this.counter = this.counter.add(BigInteger.ONE);
					this.currentValue = this.currentValue.add(BigInteger.ONE);
					return element;
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	//
	// The following protected abstract method must be implemented in every direct
	// sub-class.
	//
	protected abstract BigInteger abstractGetOrder();

	protected abstract E abstractGetElement(BigInteger value);

	protected abstract E abstractGetRandomElement(RandomGenerator randomGenerator);

	protected abstract boolean abstractContains(BigInteger value);

}
