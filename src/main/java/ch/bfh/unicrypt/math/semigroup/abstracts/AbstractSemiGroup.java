package ch.bfh.unicrypt.math.semigroup.abstracts;

import java.math.BigInteger;

import ch.bfh.unicrypt.math.element.abstracts.AbstractElement;
import ch.bfh.unicrypt.math.element.interfaces.Element;
import ch.bfh.unicrypt.math.set.abstracts.AbstractSet;
import ch.bfh.unicrypt.math.semigroup.interfaces.SemiGroup;

/**
 * This abstract class provides a basis implementation for objects of type
 * {@link SemiGroup}.
 *
 * @see AbstractElement
 *
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public abstract class AbstractSemiGroup<T extends Element> extends AbstractSet<T> implements SemiGroup {

  @Override
  public final T apply(final Element element1, final Element element2) {
    if (!this.contains(element1) || !this.contains(element2)) {
      throw new IllegalArgumentException();
    }
    return abstractApply(element1, element2);
  }

  @Override
  public final T apply(final Element... elements) {
    if (elements == null || elements.length == 0) {
      throw new IllegalArgumentException();
    }
    T result = null;
    for (Element element : elements) {
      if (result == null) {
        result = (T) element;
      } else {
        result = this.apply(result, element);
      }
    }
    return result;
  }

  @Override
  public final T selfApply(final Element element, final BigInteger amount) {
    if (!this.contains(element) || (amount == null)) {
      throw new IllegalArgumentException();
    }
    return standardSelfApply(element, amount);
  }

  @Override
  public final T selfApply(final Element element, final Element amount) {
    if (amount == null) {
      throw new IllegalArgumentException();
    }
    return this.selfApply(element, amount.getValue());
  }

  @Override
  public final T selfApply(final Element element, final int amount) {
    return this.selfApply(element, BigInteger.valueOf(amount));
  }

  @Override
  public final T selfApply(final Element element) {
    return this.apply(element, element);
  }

  @Override
  public final T multiSelfApply(final Element[] elements, final BigInteger[] amounts) {
    if ((elements == null) || (amounts == null) || (elements.length != amounts.length) || (elements.length == 0)) {
      throw new IllegalArgumentException();
    }
    Element[] results = new Element[elements.length];
    for (int i = 0; i < elements.length; i++) {
      results[i] = this.selfApply(elements[i], amounts[i]);
    }
    return this.apply(results);
  }

  //
  // The following protected methods are standard implementations for sets.
  // They may need to be changed in certain sub-classes.
  //

  protected T standardSelfApply(Element element, BigInteger amount) {
    if (amount.signum() <= 0) {
      throw new IllegalArgumentException();
    }
    T result = null;
    T atomicElement = (T) element;
    for (int i = amount.bitLength()-1; i >= 0; i--) {
      if (result == null) {
        result = atomicElement;
      } else {
        result = this.selfApply(result);
      }
      if (amount.testBit(i)) {
        result = this.apply(result, atomicElement);
      }
    }
    return result;
  }

  //
  // The following protected abstract method must be implemented in every direct sub-class.
  //

  protected abstract T abstractApply(Element element1, Element element2);

}

// THIS IS OLD CODE FOR AN OPTIMZED multiSelfApply ALGORITHM (works only for commutative operators)
//      bitLength = Math.max(bitLength, amounts[i].bitLength());
//    }
//    int bitLength = 0;
//    for (int i = 0; i < elements.length; i++) {
//      if ((elements[i] == null) || (amounts[i] == null) || (amounts[i].equals(BigInteger.ZERO))) {
//        throw new IllegalArgumentException();
//      }
//      bitLength = Math.max(bitLength, amounts[i].bitLength());
//    }
//    Element result = this.getIdentityElement();
//    for (int i = bitLength - 1; i >= 0; i--) {
//      result = result.selfApply();
//      for (int j = 0; j < amounts.length; j++) {
//        if (amounts[j].testBit(i)) {
//          result = result.apply(elements[j]);
//        }
//      }
//    }
//    return result;