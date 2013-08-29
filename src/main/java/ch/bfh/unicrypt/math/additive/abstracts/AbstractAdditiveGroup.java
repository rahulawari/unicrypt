package ch.bfh.unicrypt.math.additive.abstracts;

import ch.bfh.unicrypt.math.additive.interfaces.AdditiveElement;
import java.math.BigInteger;

import ch.bfh.unicrypt.math.general.interfaces.Element;
import ch.bfh.unicrypt.math.general.abstracts.AbstractGroup;
import ch.bfh.unicrypt.math.additive.interfaces.AdditiveGroup;

public abstract class AbstractAdditiveGroup<E extends AdditiveElement> extends AbstractGroup<E> implements AdditiveGroup {

  @Override
  public final E add(final Element element1, final Element element2) {
    return this.apply(element1, element2);
  }

  @Override
  public final E add(final Element... elements) {
    return this.apply(elements);
  }

  @Override
  public final E times(final Element element, final BigInteger amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final E times(final Element element, final Element amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final E times(final Element element, final int amount) {
    return this.selfApply(element, amount);
  }

  @Override
  public final E timesTwo(Element element) {
    return this.selfApply(element);
  }

  @Override
  public final E sumOfProducts(Element[] elements, BigInteger[] amounts) {
    return this.multiSelfApply(elements, amounts);
  }

  @Override
  public final E subtract(final Element element1, final Element element2) {
    return this.applyInverse(element1, element2);
  }

}
