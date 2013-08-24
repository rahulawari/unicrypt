package ch.bfh.unicrypt.math.group.abstracts;

import java.math.BigInteger;

import ch.bfh.unicrypt.math.element.classes.AtomicElement;
import ch.bfh.unicrypt.math.element.interfaces.Element;
import ch.bfh.unicrypt.math.group.interfaces.AdditiveSemiGroup;

public abstract class AbstractAdditiveSemiGroup extends AbstractAtomicSemiGroup implements AdditiveSemiGroup {

  private static final long serialVersionUID = 1L;

@Override
  public final AtomicElement add(final Element element1, final Element element2) {
    return this.apply(element1, element2);
  }

  @Override
  public final AtomicElement add(final Element... elements) {
    return this.apply(elements);
  }

  @Override
  public final AtomicElement times(final Element element, final BigInteger amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final AtomicElement times(final Element element, final Element amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final AtomicElement times(final Element element, final int amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final AtomicElement timesTwo(Element element) {
    return this.selfApply(element);
  }

  @Override
  public final AtomicElement sumOfProducts(Element[] elements, BigInteger[] amounts) {
    return this.multiSelfApply(elements, amounts);
  }

}