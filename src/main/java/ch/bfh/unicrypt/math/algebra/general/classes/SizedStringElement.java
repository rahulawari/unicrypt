/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.unicrypt.math.algebra.general.classes;

import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.general.abstracts.AbstractElement;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;

/**
 *
 * @author rolfhaenni
 */
public class SizedStringElement
       extends AbstractElement<SizedStringSet, SizedStringElement> {

  private final String string;

  protected SizedStringElement(final SizedStringSet set, final String string) {
    super(set);
    this.string = string;
  }

  public String getString() {
    return this.string;
  }

  public int getLength() {
    return this.getString().length();
  }

  @Override
  protected BigInteger standardGetValue() {
    return new BigInteger(this.getString().getBytes());
  }

  @Override
  protected boolean standardIsEqual(Element element) {
    return this.getString().equals(((StringElement) element).getString());
  }

  @Override
  public String standardToStringContent() {
    return "\"" + this.getString() + "\"";
  }

}
