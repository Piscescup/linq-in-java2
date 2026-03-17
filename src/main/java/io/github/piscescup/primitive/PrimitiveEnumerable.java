package io.github.piscescup.primitive;


import io.github.piscescup.Enumerable;

/**
 * Shared contract for primitive-specialized enumerable types.
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface PrimitiveEnumerable<T, SUB_PE extends PrimitiveEnumerable<T, SUB_PE>> {
    /**
     * Boxes primitive values into their wrapper-object sequence form.
     *
     * @return an {@link Enumerable} containing boxed wrapper values
     */
    Enumerable<T> boxed();

    /**
     * Orders values in ascending natural order.
     *
     * @return a {@code SUB_PE} whose values are ordered in ascending order
     */
    SUB_PE order();

    /**
     * Orders values in descending natural order.
     *
     * @return a {@code SUB_PE} whose values are ordered in descending order
     */
    SUB_PE orderDescending();

}
