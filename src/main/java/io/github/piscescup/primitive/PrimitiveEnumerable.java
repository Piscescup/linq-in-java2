package io.github.piscescup.primitive;


import io.github.piscescup.Enumerable;

/**
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface PrimitiveEnumerable<T, SUB_PE extends PrimitiveEnumerable<T, SUB_PE>> {
     Enumerable<T> boxed();
}
