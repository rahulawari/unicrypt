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
package ch.bfh.unicrypt.math.algebra.dualistic;

import ch.bfh.unicrypt.helper.Polynomial;
import ch.bfh.unicrypt.helper.numerical.ResidueClass;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.PolynomialElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.PolynomialField;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.PolynomialRing;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrime;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import java.math.BigInteger;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author philipp
 */
public class PolynomialFieldTest {

	private static final BigInteger zero = BigInteger.ZERO;
	private static final BigInteger one = BigInteger.ONE;

	private static final ZModPrime zmod2 = ZModPrime.getInstance(2);
	private static final ZModPrime zmod3 = ZModPrime.getInstance(3);
	private static final ZModPrime zmod5 = ZModPrime.getInstance(5);
	private static final ZModPrime zmod7 = ZModPrime.getInstance(7);

	private static final PolynomialRing<ResidueClass> ring2 = PolynomialRing.getInstance(zmod2);
	private static final PolynomialElement<ResidueClass> irrPoly = ring2.getElement(one, one, zero, zero, one);
	private static final PolynomialField<ResidueClass> field2_4 = PolynomialField.getInstance(zmod2, irrPoly);

	private static final PolynomialField<ResidueClass> field5_3 = PolynomialField.<ResidueClass>getInstance(zmod5, 3);
	private static final PolynomialField<ResidueClass> field7_4 = PolynomialField.<ResidueClass>getInstance(zmod7, 4);
	private static final PolynomialField<ResidueClass> field3_10 = PolynomialField.<ResidueClass>getInstance(zmod3, 10);

	@Test
	public void testGetElement() {
		// 1 + x^2 + x^5 + x^6 => 1 + x + x^2 + x^3
		PolynomialElement<ResidueClass> p = field2_4.getElement(one, zero, one, zero, zero, one, one);

		assertEquals(zmod2.getOneElement(), p.getValue().getCoefficient(0));
		assertEquals(zmod2.getOneElement(), p.getValue().getCoefficient(1));
		assertEquals(zmod2.getOneElement(), p.getValue().getCoefficient(2));
		assertEquals(zmod2.getOneElement(), p.getValue().getCoefficient(3));
		assertEquals(zmod2.getZeroElement(), p.getValue().getCoefficient(4));

		// 1 + 4x^2 + x^3 + x^5 + 6x^6 + x^7
		p = field7_4.getElement(one, zero, BigInteger.valueOf(4), one, zero, one, BigInteger.valueOf(6), one);
		assertTrue(4 > p.getValue().getDegree());

		HashMap map = new HashMap();
		map.put(4, zmod3.getElement(14));
		map.put(9, zmod3.getElement(13));
		map.put(12, zmod3.getElement(19));
		p = field3_10.getElement(Polynomial.<DualisticElement<ResidueClass>>getInstance(map, zmod3.getZeroElement(), zmod3.getOneElement()));
		assertTrue(10 > p.getValue().getDegree());
	}

	@Test
	public void testMultiply() {
		// p1 = 1 + x^2 + x^3
		PolynomialElement<ResidueClass> p1 = field2_4.getElement(one, zero, one, one);
		// p2 = 1 + x^3
		PolynomialElement<ResidueClass> p2 = field2_4.getElement(one, zero, zero, one);
		// p1 * p2 = 1 + x^2 + x^5 + x^6 => 1 + x + x^2 + x^3
		PolynomialElement<ResidueClass> p3 = p1.multiply(p2);
		assertEquals(zmod2.getOneElement(), p3.getValue().getCoefficient(0));
		assertEquals(zmod2.getOneElement(), p3.getValue().getCoefficient(1));
		assertEquals(zmod2.getOneElement(), p3.getValue().getCoefficient(2));
		assertEquals(zmod2.getOneElement(), p3.getValue().getCoefficient(3));
		assertEquals(zmod2.getZeroElement(), p3.getValue().getCoefficient(4));

		// p1 = 1 + x + x^3
		p1 = field2_4.getElement(one, one, zero, one);
		// p2 = 1 + x^2
		p2 = field2_4.getElement(one, zero, one);
		// p1 * p2 = 1 + x^2 + x^5 => 1
		p3 = p1.multiply(p2);
		assertTrue(p3.isEquivalent(field2_4.getOneElement()));
		assertTrue(p3.getSet().isField());
	}

	@Test
	public void testOneOver() {
		// p1 = 1 + x + x^3
		PolynomialElement<ResidueClass> p1 = field2_4.getElement(one, one, zero, one);
		PolynomialElement<ResidueClass> p2 = p1.oneOver();

		assertEquals(zmod2.getOneElement(), p2.getValue().getCoefficient(0));
		assertEquals(zmod2.getZeroElement(), p2.getValue().getCoefficient(1));
		assertEquals(zmod2.getOneElement(), p2.getValue().getCoefficient(2));
		assertEquals(zmod2.getZeroElement(), p2.getValue().getCoefficient(3));
		assertTrue(p2.getSet().isField());

		PolynomialElement<ResidueClass> p3 = p1.multiply(p2);
		assertTrue(p3.isEquivalent(field2_4.getOneElement()));

		p1 = field5_3.getElement(one, zero, BigInteger.valueOf(2));
		p2 = p1.oneOver();
		p3 = p1.multiply(p2);
		assertTrue(p3.isEquivalent(field5_3.getOneElement()));

		p1 = field7_4.getElement(one, BigInteger.valueOf(4), BigInteger.valueOf(5), BigInteger.valueOf(3));
		p2 = p1.oneOver();
		p3 = p1.multiply(p2);
		assertTrue(p3.isEquivalent(field7_4.getOneElement()));
	}

	@Test
	public void testDivide() {
		// p1 = 1 + x^2 + x^3
		PolynomialElement<ResidueClass> p1 = field2_4.getElement(one, zero, one, one);
		// p2 = 1 + x + x^2 + x^3
		PolynomialElement<ResidueClass> p2 = field2_4.getElement(one, one, one, one);
		// p2 / p1 = 1 + x^3
		PolynomialElement<ResidueClass> p3 = p2.divide(p1);
		assertEquals(field2_4.getElement(one, zero, zero, one), p3);
		assertTrue(p3.getSet().isField());
	}

}