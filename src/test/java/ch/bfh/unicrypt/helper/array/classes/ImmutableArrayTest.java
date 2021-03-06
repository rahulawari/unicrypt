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
package ch.bfh.unicrypt.helper.array.classes;

import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import java.util.ArrayList;
import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Rolf Haenni <rolf.haenni@bfh.ch>
 */
public class ImmutableArrayTest {

	private static DenseArray<String> a0 = DenseArray.getInstance();
	private static DenseArray<String> a1 = DenseArray.getInstance("s1", "s2", "s3");
	private static DenseArray<String> a2 = DenseArray.getInstance("s1", "s1", "s1");
	private static DenseArray<String> a3 = DenseArray.getInstance("s1", 3);
	private static DenseArray<String> a4;
	private static DenseArray<String> a5;

	public ImmutableArrayTest() {
		ArrayList list1 = new ArrayList();
		list1.add("s1");
		list1.add("s2");
		list1.add("s3");
		a4 = DenseArray.getInstance(list1);

		ArrayList list2 = new ArrayList();
		list2.add("s1");
		list2.add("s1");
		list2.add("s1");
		a5 = DenseArray.getInstance(list2);
	}

	@Test
	public void testGetLength() {
		assertEquals(0, a0.getLength());
		assertEquals(3, a1.getLength());
		assertEquals(3, a2.getLength());
		assertEquals(3, a3.getLength());
		assertEquals(3, a4.getLength());
		assertEquals(3, a5.getLength());
	}

	@Test
	public void testGetAt() {
		assertEquals("s1", a1.getAt(0));
		assertEquals("s2", a1.getAt(1));
		assertEquals("s3", a1.getAt(2));
		assertEquals("s1", a2.getAt(0));
		assertEquals("s1", a2.getAt(1));
		assertEquals("s1", a2.getAt(2));
		assertEquals("s1", a3.getAt(0));
		assertEquals("s1", a3.getAt(1));
		assertEquals("s1", a3.getAt(2));
		assertEquals("s1", a4.getAt(0));
		assertEquals("s2", a4.getAt(1));
		assertEquals("s3", a4.getAt(2));
		assertEquals("s1", a5.getAt(0));
		assertEquals("s1", a5.getAt(2));
	}

	@Test
	public void testRemoveAt() {
		assertEquals(a0, a1.removeAt(2).removeAt(0).removeAt(0));
		assertEquals(a0, a2.removeAt(1).removeAt(1).removeAt(0));
		assertEquals(a0, a3.removeAt(0).removeAt(1).removeAt(0));
		assertEquals(a1.removeAt(1).removeAt(1), a2.removeAt(2).removeAt(0));
		assertEquals(a1.removeAt(1).removeAt(1), a3.removeAt(2).removeAt(0));
	}

	@Test
	public void testInsertAt() {
		assertEquals(a1, a0.insertAt(0, "s3").insertAt(0, "s2").insertAt(0, "s1"));
		assertEquals(a1, a0.insertAt(0, "s2").insertAt(1, "s3").insertAt(0, "s1"));
		assertEquals(a1, a0.insertAt(0, "s1").insertAt(1, "s3").insertAt(1, "s2"));
	}

	@Test
	public void testReplaceAt() {
		assertEquals(a2, a1.replaceAt(1, "s1").replaceAt(2, "s1"));
	}

	@Test
	public void testAdd() {
		assertEquals(a1, a0.add("s1").add("s2").add("s3"));
		assertEquals(a2, a0.add("s1").add("s1").add("s1"));
		assertEquals(a3, a0.add("s1").add("s1").add("s1"));
	}

	@Test
	public void testIterator() {
		Iterator<String> it2 = a2.iterator();
		Iterator<String> it3 = a3.iterator();
		while (it2.hasNext()) {
			assertEquals(it2.next(), it3.next());
		}
	}

	@Test
	public void testEquals() {
		assertTrue(a0.equals(a0));
		assertFalse(a0.equals(a1));
		assertFalse(a0.equals(a2));
		assertFalse(a0.equals(a3));
		assertTrue(a1.equals(a1));
		assertFalse(a1.equals(a2));
		assertFalse(a1.equals(a3));
		assertTrue(a2.equals(a2));
		assertTrue(a2.equals(a3));
		assertTrue(a3.equals(a3));
		assertTrue(a1.equals(a4));
		assertTrue(a2.equals(a5));
		assertTrue(a3.equals(a5));

	}

	@Test
	public void testAppend() {
		assertEquals(a0, a0.append(a0));
		assertEquals(a1, a0.append(a1));
		assertEquals(a1, a1.append(a0));
		assertEquals(a2, a0.append(a2));
		assertEquals(a2, a2.append(a0));
		assertEquals(a3, a0.append(a3));
		assertEquals(a3, a3.append(a0));
		assertEquals(a3, a3.append(a0));
		assertEquals(6, a1.append(a1).getLength());
		assertEquals(6, a1.append(a2).getLength());
		assertEquals(a2.append(a2), a3.append(a3));
		assertEquals(a2.append(a3), a3.append(a2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetInstanceByCollectionException() {
		ArrayList list1 = new ArrayList();
		list1.add("s1");
		list1.add(null);
		list1.add("s3");
		DenseArray a = DenseArray.getInstance(list1);
	}

}
