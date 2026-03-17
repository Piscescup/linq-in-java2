package io.github.piscescup;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Represents an {@link Enumerable} with an established primary ordering that can
 * be refined by additional ordering keys.
 *
 * @param <T> the element type
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface OrderedEnumerable<T> extends Enumerable<T> {
    /**
     * Applies an additional ascending ordering by a comparable key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param <K> the key type
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K extends Comparable<? super K>> OrderedEnumerable<T> thenOrderBy(
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Applies an additional ascending ordering by a key using the supplied comparator.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    <K> OrderedEnumerable<T> thenOrderBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    );

    /**
     * Applies an additional ascending ordering by an {@code int} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderByInt(
        ToIntFunction<? super T> keySelector
    );

    /**
     * Applies an additional ascending ordering by a {@code long} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderByLong(
        ToLongFunction<? super T> keySelector
    );

    /**
     * Applies an additional ascending ordering by a {@code double} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderByDouble(
        ToDoubleFunction<? super T> keySelector
    );

    /**
     * Applies an additional descending ordering by a comparable key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param <K> the key type
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K extends Comparable<? super K>> OrderedEnumerable<T> thenOrderByDescending(
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Applies an additional descending ordering by a key using the supplied comparator.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    <K> OrderedEnumerable<T> thenOrderByDescending(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    );

    /**
     * Applies an additional descending ordering by an {@code int} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderDescendingByInt(
        ToIntFunction<? super T> keySelector
    );

    /**
     * Applies an additional descending ordering by a {@code long} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderDescendingByLong(
        ToLongFunction<? super T> keySelector
    );

    /**
     * Applies an additional descending ordering by a {@code double} key.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@code OrderedEnumerable<T>} with the additional ordering applied
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> thenOrderDescendingByDouble(
        ToDoubleFunction<? super T> keySelector
    );
}
