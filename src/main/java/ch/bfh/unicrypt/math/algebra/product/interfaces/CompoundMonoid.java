/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.unicrypt.math.algebra.product.interfaces;

import ch.bfh.unicrypt.math.algebra.general.interfaces.Monoid;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;

/**
 *
 * @author rolfhaenni
 */
public interface CompoundMonoid<S extends Monoid> extends Monoid, CompoundSemiGroup<S> {
}