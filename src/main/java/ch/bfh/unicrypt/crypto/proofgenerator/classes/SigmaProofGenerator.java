package ch.bfh.unicrypt.crypto.proofgenerator.classes;

import java.util.Random;

import ch.bfh.unicrypt.crypto.proofgenerator.abstracts.ProofGeneratorAbstract;
import ch.bfh.unicrypt.crypto.proofgenerator.interfaces.SigmaProofGenerator;
import ch.bfh.unicrypt.math.algebra.additive.classes.ZPlusMod;
import ch.bfh.unicrypt.math.algebra.additive.interfaces.AdditiveElement;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductGroup;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.function.classes.HashFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

public class SigmaProofGenerator extends ProofGeneratorAbstract implements SigmaProofGenerator {

  public static final HashFunction.HashAlgorithm DEFAULT_HASH_ALGORITHM = HashFunction.HashAlgorithm.SHA256;
  public static final ConcatenateFunction.ConcatParameter DEFAULT_CONCAT_ALGORITHM = ConcatParameter.Plain;
  public static final Mapper DEFAULT_MAPPER = new CharsetXRadixYMapperClass(CharsetXRadixYMapperClass.DEFAULT_CHARSET, CharsetXRadixYMapperClass.DEFAULT_RADIX);
  private final HashFunction hashFunction;
  private final ZPlusMod challengeSpace;
  private final ProductGroup proofSpace;
  private final ConcatParameter concatParameter;
  private final Mapper mapper;

  public SigmaProofGenerator(final Function function) {
    this(function, SigmaProofGenerator.DEFAULT_HASH_ALGORITHM, SigmaProofGenerator.DEFAULT_CONCAT_ALGORITHM, SigmaProofGenerator.DEFAULT_MAPPER);
  }

  public SigmaProofGenerator(final Function function,
          final HashFunction.HashAlgorithm hashAlgorithm,
          final ConcatenateFunction.ConcatParameter concatParameter,
          final Mapper mapper) {
    if ((function == null) || (hashAlgorithm == null) || (concatParameter == null) || (mapper == null)) {
      throw new IllegalArgumentException();
    }
    this.function = function;
    this.challengeSpace = this.getDomain().getMinOrderGroup();
    this.hashFunction = new HashFunction(hashAlgorithm, this.challengeSpace);
    this.concatParameter = concatParameter;
    this.mapper = mapper;
    this.proofSpace = new ProductGroup(this.getCommitmentSpace(), this.getResponseSpace());

  }

  @Override
  public Tuple generate(final Element secretInput, final Element publicInput, final Element otherInput, final Random random) {
    if ((secretInput == null) || !this.getResponseSpace().contains(secretInput) || (publicInput == null)
            || !this.getCommitmentSpace().contains(publicInput)) {
      throw new IllegalArgumentException();
    }
    final Element randomElement = this.createRandomElement(random);
    final Element commitment = this.createCommitment(randomElement);
    final AdditiveElement challenge = this.createChallenge(commitment, publicInput, otherInput);
    final Element response = this.createResponse(randomElement, challenge, secretInput);
    return this.createProof(commitment, response);
  }

  public Element createRandomElement(final Random random) {
    return this.getResponseSpace().getRandomElement(random);
  }

  public Element createCommitment(final Element randomElement) {
    return this.getProofFunction().apply(randomElement);
  }

  public AdditiveElement createChallenge(final Element commitment, final Element publicInput, final Element otherInput) {
    AdditiveElement hashInput;
    if (otherInput == null) {
      final ProductGroup pg = new ProductGroup(publicInput.getSet(), commitment.getSet());
      final ConcatenateFunction concatFunction = new ConcatenateFunction(pg, this.concatParameter, this.mapper);
      hashInput = concatFunction.apply(publicInput, commitment);
    } else {
      final ProductGroup pg = new ProductGroup(publicInput.getSet(), commitment.getSet(), otherInput.getSet());
      final ConcatenateFunction concatFunction = new ConcatenateFunction(pg, this.concatParameter, this.mapper);
      hashInput = concatFunction.apply(publicInput, commitment, otherInput);
    }
    return this.getHashFunction().apply(hashInput);
  }

  @SuppressWarnings("static-method")
  public Element createResponse(final Element randomElement, final AdditiveElement challenge, final Element secretInput) {
    return randomElement.apply(secretInput.selfApply(challenge));
  }

  public Tuple createProof(final Element commitment, final Element response) {
    return this.getProofSpace().getElement(commitment, response);
  }

  @Override
  public boolean verify(final Tuple proof, final Element publicInput, final Element otherInput) {
    if ((proof == null) || !this.getProofSpace().contains(proof) || (publicInput == null) || !this.getCommitmentSpace().contains(publicInput)) {
      throw new IllegalArgumentException();
    }
    AdditiveElement hashInput;
    if (otherInput == null) {
      final ProductGroup pg = new ProductGroup(publicInput.getSet(), this.getCommitment(proof).getGroup());
      final ConcatenateFunction concatFunction = new ConcatenateFunction(pg, this.concatParameter, this.mapper);
      hashInput = concatFunction.apply(publicInput, this.getCommitment(proof));
    } else {
      final ProductGroup pg = new ProductGroup(publicInput.getSet(), this.getCommitment(proof).getGroup(), otherInput.getSet());
      final ConcatenateFunction concatFunction = new ConcatenateFunction(pg, this.concatParameter, this.mapper);
      hashInput = concatFunction.apply(publicInput, this.getCommitment(proof), otherInput);
    }
    final AdditiveElement challenge = this.getHashFunction().apply(hashInput);
    final Element left = this.getProofFunction().apply(this.getResponse(proof));
    final Element right = this.getCommitment(proof).apply(publicInput.selfApply(challenge));
    return left.isEqual(right);
  }

  public Group getCommitmentSpace() {
    return this.getCoDomain();
  }

  public Group getResponseSpace() {
    return this.getDomain();
  }

  public Element getCommitment(final Tuple element) {
    if (element == null) {
      throw new IllegalArgumentException();
    }
    return element.getAt(0);
  }

  public Element getResponse(final Tuple element) {
    if (element == null) {
      throw new IllegalArgumentException();
    }
    return element.getAt(1);
  }

  public HashFunction getHashFunction() {
    return this.hashFunction;
  }

}
