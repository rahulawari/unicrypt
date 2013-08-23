package ch.bfh.unicrypt.math.group.classes;

import java.math.BigInteger;
import java.util.Random;

import ch.bfh.unicrypt.math.element.Element;
import ch.bfh.unicrypt.math.group.abstracts.AbstractAdditiveCyclicGroup;
import ch.bfh.unicrypt.math.group.abstracts.AbstractMultiplicativeMonoid;
import ch.bfh.unicrypt.math.group.interfaces.Group;
import ch.bfh.unicrypt.math.group.interfaces.Monoid;
import ch.bfh.unicrypt.math.group.interfaces.Set;
import ch.bfh.unicrypt.math.utility.RandomUtil;

/**
/**
 * This class implements the multiplicative monoid of non-negative integers with infinite order.
 * Its identity element is 0.
 *
 * @see "Handbook of Applied Cryptography, Example 2.164"
 * @see <a href="http://en.wikipedia.org/wiki/Integer">http://en.wikipedia.org/wiki/Integer</a>
 *
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public class NTimes extends AbstractMultiplicativeMonoid {

  private static final long serialVersionUID = 1L;
  private static final int RANDOM_ELEMENT_BIT_LENGTH = 1000;

  /**
   * This is the private constructor of this class. It is called by the static factory methods
   * of the static nested class Factory.
   */
  private NTimes() {
  }

  //
  // The following protected methods implement the abstract methods from
  // various super-classes
  //

  @Override
  protected Element abstractGetRandomElement(final Random random) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean abstractContains(final BigInteger value) {
    return value.signum() >= 0;
  }

  @Override
  protected BigInteger abstractGetOrder() {
    return Group.INFINITE_ORDER;
  }

  @Override
  protected Element abstractGetIdentityElement() {
    return this.standardGetElement(BigInteger.ONE);
  }

  @Override
  protected Element abstractApply(final Element element1, final Element element2) {
    return this.standardGetElement(element1.getValue().multiply(element2.getValue()));
  }

  @Override
  protected boolean abstractEquals(Set set) {
    return true;
  }

  //
  // STATIC FACTORY METHODS
  //

  private static NTimes instance;

  /**
   * Returns the singleton object of this class.
   * @return The singleton object of this class
   */
  public static NTimes getInstance() {
    if (NTimes.instance == null) {
      NTimes.instance = new NTimes();
    }
    return NTimes.instance;
  }

}