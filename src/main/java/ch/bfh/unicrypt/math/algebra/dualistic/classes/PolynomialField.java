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
package ch.bfh.unicrypt.math.algebra.dualistic.classes;

import ch.bfh.unicrypt.helper.Polynomial;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.FiniteField;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.PrimeField;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Triple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.interfaces.MultiplicativeGroup;
import ch.bfh.unicrypt.random.classes.HybridRandomByteSequence;
import ch.bfh.unicrypt.random.interfaces.RandomByteSequence;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author rolfhaenni
 * @param <V>
 */
public class PolynomialField<V> extends PolynomialRing<V> implements
		FiniteField<Polynomial<DualisticElement<V>>> {

	private final PolynomialElement<V> irreduciblePolynomial;

	protected PolynomialField(PrimeField primeField,
			PolynomialElement<V> irreduciblePolynomial) {
		super(primeField);
		this.irreduciblePolynomial = irreduciblePolynomial;
	}

	public PrimeField<V> getPrimeField() {
		return (PrimeField<V>) super.getRing();
	}

	public PolynomialElement<V> getIrreduciblePolynomial() {
		return this.irreduciblePolynomial;
	}

	public int getDegree() {
		return this.irreduciblePolynomial.getValue().getDegree();
	}

	/**
	 * 
	 * @param s
	 *            String containing 0/1 for each coefficient of binary
	 *            polynomial with rightmost bit as constant term.
	 * @return
	 */
	public PolynomialElement<V> getElementFromBitString(String s) {
		if (!this.getPrimeField().equals(ZModTwo.getInstance())) {
			throw new UnsupportedOperationException(
					"Only binary polynomials supported");
		}

		BigInteger bitString = new BigInteger(s, 2);

		// Read bits and create a BigInteger ArrayList
		ArrayList<BigInteger> arrayBigInteger = new ArrayList<BigInteger>();
		for (Character c : bitString.toString(2).toCharArray()) {
			arrayBigInteger.add(0, new BigInteger(c.toString()));

		}

		// Convert ArrayList BigInteger array and get element
		BigInteger[] coeffs = {};
		coeffs = arrayBigInteger.toArray(coeffs);

		return this.getElement(coeffs);
	}

	//
	// The following protected methods override the default implementation from
	// various super-classes
	//
	@Override
	protected BigInteger abstractGetOrder() {
		// p^m
		return this.getCharacteristic().pow(this.getDegree());
	}

	@Override
	protected PolynomialElement<V> abstractGetElement(Polynomial value) {
		PolynomialElement<V> e = super.abstractGetElement(value);
		return new PolynomialElement<V>(this, this.mod(e).getValue());
	}

	@Override
	public BigInteger getCharacteristic() {
		return this.getPrimeField().getOrder();
	}

	@Override
	public MultiplicativeGroup<Polynomial<DualisticElement<V>>> getMultiplicativeGroup() {
		// TODO Create muliplicative.classes.FStar (Definition 2.228, Fact
		// 2.229/2.230)
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	protected PolynomialElement<V> abstractApply(PolynomialElement<V> element1,
			PolynomialElement<V> element2) {
		PolynomialElement<V> poly = super.abstractApply(element1, element2);
		return this.mod(this.getElement(poly.getValue()));
	}

	@Override
	protected PolynomialElement<V> abstractMultiply(
			PolynomialElement<V> element1, PolynomialElement<V> element2) {
		PolynomialElement<V> poly = super.abstractMultiply(element1, element2);
		return this.mod(this.getElement(poly.getValue()));
	}

	@Override
	public PolynomialElement<V> divide(Element element1, Element element2) {
		return this.multiply(element1, this.oneOver(element2));
	}

	/**
	 * oneOver.
	 * <p>
	 * Compute using extended Euclidean algorithm for polynomial (Algorithm
	 * 2.226)
	 * <p>
	 * 
	 * @param element
	 * @return
	 */
	@Override
	public PolynomialElement<V> oneOver(Element element) {

		if (!this.contains(element)) {
			throw new IllegalArgumentException();
		}

		if (element.isEquivalent(this.getZeroElement())) {
			throw new UnsupportedOperationException();
		}

		Triple euclid = this.extendedEuclidean((PolynomialElement<V>) element,
				this.irreduciblePolynomial);
		return this.getElement(((PolynomialElement<V>) euclid.getSecond())
				.getValue());

	}

	/**
	 * Mod. g(x) mod irreduciblePolynomial = h(x)
	 * <p>
	 * Z_p must be a field.
	 * <p>
	 * 
	 * @param g
	 *            g(x) in Z_p[x]
	 * @return h(x) in Z_p[x]
	 */
	private PolynomialElement<V> mod(PolynomialElement<V> g) {
		if (g.getValue().getDegree() < this.getDegree()) {
			return g;
		}
		Pair longDiv = this.longDivision(g, this.irreduciblePolynomial);
		return (PolynomialElement<V>) longDiv.getSecond();
	}
	
	public PolynomialElement<V> sqrtMod(PolynomialElement<V> element){
		BigInteger i=new BigInteger("2").pow(this.getDegree()-1);
		return element.power(i);
	}

	//
	// STATIC FACTORY METHODS
	//
	public static <V> PolynomialField getInstance(PrimeField primeField,
			int degree) {
		return getInstance(primeField, degree,
				HybridRandomByteSequence.getInstance());
	}

	public static <V> PolynomialField getInstance(PrimeField primeField,
			int degree, RandomByteSequence randomByteSequence) {
		if (primeField == null || degree < 1) {
			throw new IllegalArgumentException();
		}
		PolynomialRing<V> ring = PolynomialRing.getInstance(primeField);
		PolynomialElement<V> irreduciblePolynomial = ring
				.findIrreduciblePolynomial(degree, randomByteSequence);
		return new PolynomialField(primeField, irreduciblePolynomial);
	}

	public static <V> PolynomialField getInstance(PrimeField primeField,
			PolynomialElement<V> irreduciblePolynomial) {
		if (primeField == null
				|| irreduciblePolynomial == null
				|| !irreduciblePolynomial.getSet().getSemiRing()
						.isEquivalent(primeField)
				|| !irreduciblePolynomial.isIrreducible()) {
			throw new IllegalArgumentException();
		}
		return new PolynomialField(primeField, irreduciblePolynomial);
	}

}
