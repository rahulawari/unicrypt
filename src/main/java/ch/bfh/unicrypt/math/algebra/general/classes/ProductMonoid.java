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

import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Monoid;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.helper.CompoundIterator;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author rolfhaenni
 */
public class ProductMonoid
			 extends ProductSemiGroup
			 implements Monoid {

	private Tuple identityElement;

	protected ProductMonoid(final Monoid[] monoids) {
		super(monoids);
	}

	protected ProductMonoid(final Monoid monoid, final int arity) {
		super(monoid, arity);
	}

	@Override
	public Monoid getFirst() {
		return (Monoid) super.getFirst();
	}

	@Override
	public Monoid getAt(int index) {
		return (Monoid) super.getAt(index);
	}

	@Override
	public Monoid getAt(int... indices) {
		return (Monoid) super.getAt(indices);
	}

	@Override
	public Monoid[] getAll() {
		return (Monoid[]) super.getAll();
	}

	@Override
	public ProductMonoid removeAt(final int index) {
		return (ProductMonoid) super.removeAt(index);
	}

	@Override
	protected ProductMonoid abstractRemoveAt(Set set, int arity) {
		return ProductMonoid.getInstance((Monoid) set, arity);
	}

	@Override
	protected ProductMonoid abstractRemoveAt(Set[] sets) {
		return ProductMonoid.getInstance((Monoid[]) sets);
	}

	@Override
	public Iterable<? extends Monoid> makeIterable() {
		final ProductSet productMonoid = this;
		return new Iterable<Monoid>() {
			@Override
			public Iterator<Monoid> iterator() {
				return new CompoundIterator<Monoid>(productMonoid);
			}
		};
	}

	@Override
	public Tuple getIdentityElement() {
		if (this.identityElement == null) {
			final Element[] identityElements = new Element[this.getArity()];
			for (int i = 0; i < identityElements.length; i++) {
				identityElements[i] = this.getAt(i).getIdentityElement();
			}
			this.identityElement = this.standardGetElement(identityElements);
		}
		return this.identityElement;
	}

	@Override
	public boolean isIdentityElement(Element element) {
		return this.areEqual(element, this.getIdentityElement());
	}

	@Override
	public Tuple standardApply(final Element[] elements) {
		if (elements.length == 0) {
			return this.getIdentityElement();
		}
		return super.standardApply(elements);
	}

	@Override
	protected Tuple standardMultiSelfApply(final Element[] elements, BigInteger[] amounts) {
		if (elements.length == 0) {
			return this.getIdentityElement();
		}
		return super.standardMultiSelfApply(elements, amounts);
	}

	/**
	 * This is a static factory method to construct a composed monoid without calling respective constructors. The input
	 * monids are given as an array.
	 * <p/>
	 * @param monoids The array of input monoids
	 * @return The corresponding product monoids
	 * @throws IllegalArgumentException if {@literal monids} is null or contains null
	 */
	public static ProductMonoid getInstance(final Monoid... monoids) {
		if (monoids == null) {
			throw new IllegalArgumentException();
		}
		boolean isGroup = true;
		if (monoids.length > 0) {
			boolean uniform = true;
			Monoid first = monoids[0];
			for (final Monoid monoid : monoids) {
				if (monoid == null) {
					throw new IllegalArgumentException();
				}
				if (!monoid.isEquivalent(first)) {
					uniform = false;
				}
				isGroup = isGroup && monoid.isGroup();
			}
			if (uniform) {
				return ProductMonoid.getInstance(first, monoids.length);
			}
		}
		if (isGroup) {
			Group[] groups = Arrays.copyOf(monoids, monoids.length, Group[].class);
			return ProductGroup.getInstance(groups);
		}
		return new ProductMonoid(monoids);
	}

	public static ProductMonoid getInstance(final Monoid monoid, int arity) {
		if ((monoid == null) || (arity < 0)) {
			throw new IllegalArgumentException();
		}
		if (monoid.isGroup()) {
			return ProductGroup.getInstance((Group) monoid, arity);
		}
		if (arity == 0) {
			return new ProductMonoid(new Monoid[]{});
		}
		return new ProductMonoid(monoid, arity);
	}

}
