package ch.bfh.unicrypt.crypto.proofgenerator.abstracts;

import java.util.List;
import java.util.Random;

import ch.bfh.unicrypt.crypto.proofgenerator.interfaces.ProductDomainProofGenerator;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductGroup;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;

public abstract class ProductDomainProofGeneratorAbstract extends ProofGeneratorAbstract implements ProductDomainProofGenerator {

  @Override
  public Tuple generate(final List<Element> secretInputs, final Element publicInput) {
    return this.generate(this.getDomain().getElement(secretInputs), publicInput);
  }

  @Override
  public Tuple generate(final List<Element> secretInputs, final Element publicInput, final Element otherInput) {
    return this.generate(this.getDomain().getElement(secretInputs), publicInput, otherInput);
  }

  @Override
  public Tuple generate(final List<Element> secretInputs, final Element publicInput, final Random random) {
    return this.generate(this.getDomain().getElement(secretInputs), publicInput, random);
  }

  @Override
  public Tuple generate(final List<Element> secretInputs, final Element publicInput, final Element otherInput, final Random random) {
    return this.generate(this.getDomain().getElement(secretInputs), publicInput, otherInput, random);
  }

  @Override
  public ProductGroup getDomain() {
    return (ProductGroup) super.getDomain();
  }

}