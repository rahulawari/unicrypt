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
package ch.bfh.unicrypt.crypto.proofgenerator;

import ch.bfh.unicrypt.crypto.proofgenerator.challengegenerator.classes.StandardNonInteractiveSigmaChallengeGenerator;
import ch.bfh.unicrypt.crypto.proofgenerator.challengegenerator.interfaces.SigmaChallengeGenerator;
import ch.bfh.unicrypt.crypto.proofgenerator.classes.PreimageEqualityProofGenerator;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.classes.BooleanElement;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductSemiGroup;
import ch.bfh.unicrypt.math.algebra.general.classes.Triple;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarMod;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModSafePrime;
import ch.bfh.unicrypt.math.function.classes.GeneratorFunction;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;
import ch.bfh.unicrypt.math.helper.Alphabet;
import java.math.BigInteger;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PreimageEqualityProofGeneratorTest {

	final static int P1 = 167;
	final static String P2 = "88059184022561109274134540595138392753102891002065208740257707896840303297223";
	final private GStarMod G_q1;
	final private GStarMod G_q2;
	final private StringElement proverId;

	public PreimageEqualityProofGeneratorTest() {
		this.G_q1 = GStarModSafePrime.getInstance(P1);
		this.G_q2 = GStarModSafePrime.getInstance(new BigInteger(P2, 10));
		this.proverId = StringMonoid.getInstance(Alphabet.BASE64).getElement("Prover1");
	}

	@Test
	public void testPreimageEqualityProof() {

		// Proof generator
		Function f1 = GeneratorFunction.getInstance(this.G_q1.getElement(4));
		Function f2 = GeneratorFunction.getInstance(this.G_q1.getElement(2));
		ProductFunction f = ProductFunction.getInstance(f1, f2);

		SigmaChallengeGenerator scg = StandardNonInteractiveSigmaChallengeGenerator.getInstance(
			   f.getCoDomain(), (ProductSemiGroup) f.getCoDomain(), ZMod.getInstance(f.getDomain().getMinimalOrder()), this.proverId);

		PreimageEqualityProofGenerator pg = PreimageEqualityProofGenerator.getInstance(scg, f1, f2);
		assertTrue(pg.getProofFunctions().length == 2 && pg.getProofFunctions()[0].isEquivalent(f1));

		// Valid proof
		Element privateInput = f1.getDomain().getElement(3);
		Element publicInput = Tuple.getInstance(
			   f1.getCoDomain().getElement(64),
			   f2.getCoDomain().getElement(8));


		Triple proof = pg.generate(privateInput, publicInput);

		BooleanElement v = pg.verify(proof, publicInput);
		assertTrue(v.getBoolean());

		// Invalid proof -> preimages are not equal
		privateInput = f1.getDomain().getElement(3);
		publicInput = Tuple.getInstance(
			   f1.getCoDomain().getElement(64), // Preimage = 3
			   f2.getCoDomain().getElement(16));    // Preimage = 4
		proof = pg.generate(privateInput, publicInput);
		v = pg.verify(proof, publicInput);
		assertTrue(!v.getBoolean());

	}

	@Test
	public void testPreimageEqualityProof_Invalid() {

		// Proof generator
		Function f1 = GeneratorFunction.getInstance(this.G_q2.getElement(4));
		Function f2 = GeneratorFunction.getInstance(this.G_q2.getElement(2));
		ProductFunction f = ProductFunction.getInstance(f1, f2);

		SigmaChallengeGenerator scg = StandardNonInteractiveSigmaChallengeGenerator.getInstance(
			   f.getCoDomain(), (ProductSemiGroup) f.getCoDomain(), ZMod.getInstance(f.getDomain().getMinimalOrder()), this.proverId);

		PreimageEqualityProofGenerator pg = PreimageEqualityProofGenerator.getInstance(scg, f1, f2);
		assertTrue(pg.getProofFunctions().length == 2 && pg.getProofFunctions()[0].isEquivalent(f1));

		// Invalid proof -> preimages are not equal
		Element privateInput = f1.getDomain().getElement(3);
		Tuple publicInput = Tuple.getInstance(
			   f1.getCoDomain().getElement(64), // Preimage = 3
			   f2.getCoDomain().getElement(16));    // Preimage = 4
		Triple proof = pg.generate(privateInput, publicInput);
		BooleanElement v = pg.verify(proof, publicInput);
		assertTrue(!v.getBoolean());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testPreimageEqualityProof_Exception() {
		Function f1 = GeneratorFunction.getInstance(this.G_q1.getElement(4));
		Function f2 = GeneratorFunction.getInstance(this.G_q1.getElement(2));
		Function f3 = GeneratorFunction.getInstance(this.G_q2.getElement(4));
		ProductFunction f = ProductFunction.getInstance(f1, f2, f3);
		SigmaChallengeGenerator scg = StandardNonInteractiveSigmaChallengeGenerator.getInstance(f, this.proverId);
		PreimageEqualityProofGenerator pg = PreimageEqualityProofGenerator.getInstance(scg, f1, f2, f3);
	}

}
