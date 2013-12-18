package ch.bfh.unicrypt.crypto.schemes.commitment.classes;

import ch.bfh.unicrypt.crypto.random.classes.PseudoRandomReferenceString;
import ch.bfh.unicrypt.crypto.random.interfaces.RandomReferenceString;
import ch.bfh.unicrypt.crypto.schemes.commitment.abstracts.AbstractRandomizedCommitmentScheme;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrime;
import ch.bfh.unicrypt.math.algebra.general.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarMod;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.function.classes.ApplyFunction;
import ch.bfh.unicrypt.math.function.classes.CompositeFunction;
import ch.bfh.unicrypt.math.function.classes.GeneratorFunction;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

public class PedersenCommitmentScheme<CS extends CyclicGroup, CE extends Element>
			 extends AbstractRandomizedCommitmentScheme<ZMod, ZModElement, CS, CE, ZModPrime> {

	private final CS cyclicGroup;
	private final CE randomizationGenerator;
	private final CE messgeGenerator;

	protected PedersenCommitmentScheme(CS cyclicGroup, CE randomizationGenerator, CE messageGenerator) {
		this.cyclicGroup = cyclicGroup;
		this.randomizationGenerator = randomizationGenerator;
		this.messgeGenerator = messageGenerator;
	}

	public final CS getCyclicGroup() {
		return this.cyclicGroup;
	}

	public final CE getRandomizationGenerator() {
		return this.randomizationGenerator;
	}

	public final CE getMessageGenerator() {
		return this.messgeGenerator;
	}

	@Override
	protected Function abstractGetCommitmentFunction() {
		return CompositeFunction.getInstance(
					 ProductFunction.getInstance(GeneratorFunction.getInstance(this.getMessageGenerator()),
																			 GeneratorFunction.getInstance(this.getRandomizationGenerator())),
					 ApplyFunction.getInstance(this.getCyclicGroup()));
	}

	public static <CS extends CyclicGroup, CE extends Element> PedersenCommitmentScheme<CS, CE> getInstance(CS cyclicGroup) {
		return PedersenCommitmentScheme.<CS, CE>getInstance(cyclicGroup, (RandomReferenceString) null);
	}

	public static <CS extends CyclicGroup, CE extends Element> PedersenCommitmentScheme<CS, CE> getInstance(CS cyclicGroup, RandomReferenceString randomReferenceString) {
		return PedersenCommitmentScheme.<CS, CE>getInternalInstance(cyclicGroup, randomReferenceString);
	}

	public static PedersenCommitmentScheme<GStarMod, GStarModElement> getInstance(GStarMod gStarMod) {
		return PedersenCommitmentScheme.<GStarMod, GStarModElement>getInstance(gStarMod, (RandomReferenceString) null);
	}

	public static PedersenCommitmentScheme<GStarMod, GStarModElement> getInstance(GStarMod gStarMod, RandomReferenceString randomReferenceString) {
		return PedersenCommitmentScheme.<GStarMod, GStarModElement>getInternalInstance(gStarMod, randomReferenceString);
	}

	public static PedersenCommitmentScheme<GStarMod, GStarModElement> getInstance(GStarModElement firstGenerator, GStarModElement secondGenerator) {
		if (!firstGenerator.getSet().isEquivalent(secondGenerator.getSet())) {
			throw new IllegalArgumentException();
		}
		return new PedersenCommitmentScheme<GStarMod, GStarModElement>(firstGenerator.getSet(), firstGenerator, secondGenerator);
	}

	private static <CS extends CyclicGroup, CE extends Element> PedersenCommitmentScheme<CS, CE> getInternalInstance(CS cyclicGroup, RandomReferenceString randomReferenceString) {
		if (randomReferenceString == null) {
			randomReferenceString = PseudoRandomReferenceString.getInstance();
		} else {
			randomReferenceString.reset();
		}
		Element[] generators = cyclicGroup.getIndependentGenerators(2, randomReferenceString);
		CE randomizationGenerator = (CE) generators[0];
		CE messageGenerator = (CE) generators[1];
		return new PedersenCommitmentScheme<CS, CE>(cyclicGroup, randomizationGenerator, messageGenerator);
	}

}
