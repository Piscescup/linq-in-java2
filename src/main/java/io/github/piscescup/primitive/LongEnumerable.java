package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code long} values.
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface LongEnumerable
    extends BaseEnumerable<Long, LongEnumerable>, PrimitiveEnumerable<Long, LongEnumerable> {
    /**
     * Filters elements by a {@code long}-specific predicate.
     *
     * @param predicate the condition used to test each {@code long} value
     * @return a {@code LongEnumerable} containing matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    LongEnumerable whereByLong(LongPredicate predicate);

    /**
     * Maps each value to another {@code long} value.
     *
     * @param selector the mapping function applied to each value
     * @return a {@code LongEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    LongEnumerable select(LongUnaryOperator selector);

    /**
     * Maps each value to an object value.
     *
     * @param selector the mapping function applied to each value
     * @param <R> the target element type
     * @return an {@link Enumerable} containing mapped object values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    <R> Enumerable<R> selectToObj(LongFunction<? extends R> selector);

    /**
     * Maps each value to an {@code int}.
     *
     * @param selector the mapping function applied to each value
     * @return an {@link IntEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    IntEnumerable mapToInt(LongToIntFunction selector);

    /**
     * Maps each value to a {@code double}.
     *
     * @param selector the mapping function applied to each value
     * @return a {@link DoubleEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    DoubleEnumerable mapToDouble(LongToDoubleFunction selector);

    /**
     * Skips the first {@code count} values.
     *
     * @param count the number of values to skip from the start of the sequence
     * @return a {@code LongEnumerable} representing the remaining values
     */
    LongEnumerable skip(long count);

    /**
     * Takes the first {@code count} values.
     *
     * @param count the number of values to keep from the start of the sequence
     * @return a {@code LongEnumerable} containing the kept values
     */
    LongEnumerable take(long count);

    /**
     * Returns the first value in the sequence.
     *
     * @return {@code long}, the first value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    long first();

    /**
     * Returns the first value wrapped in an {@link OptionalLong}.
     *
     * @return an {@link OptionalLong} describing the first value, or empty if the sequence is empty
     */
    OptionalLong firstOptional();

    /**
     * Returns the only value in the sequence.
     *
     * @return {@code long}, the single value
     * @throws java.util.NoSuchElementException if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    long single();

    /**
     * Returns the only value wrapped in an {@link OptionalLong}.
     *
     * @return an {@link OptionalLong} describing the single value, or empty if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    OptionalLong singleOptional();

    /**
     * Concatenates this sequence with another {@code long} sequence.
     *
     * @param other the sequence appended after this sequence
     * @return a {@code LongEnumerable} containing values from both sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    LongEnumerable concat(LongEnumerable other);

    /**
     * Appends a single value to the end of the sequence.
     *
     * @param value the value appended after the current sequence
     * @return a {@code LongEnumerable} ending with {@code value}
     */
    LongEnumerable append(long value);

    /**
     * Prepends a single value to the beginning of the sequence.
     *
     * @param value the value inserted before the current sequence
     * @return a {@code LongEnumerable} starting with {@code value}
     */
    LongEnumerable prepend(long value);

    /**
     * Removes duplicate values from the sequence.
     *
     * @return a {@code LongEnumerable} containing distinct values
     */
    LongEnumerable distinct();

    /**
     * Determines whether any value matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if at least one value matches
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean anyByLong(LongPredicate predicate);

    /**
     * Determines whether all values match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if all values match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean allByLong(LongPredicate predicate);

    /**
     * Counts values that match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code long}, the number of matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    long countByLong(LongPredicate predicate);

    /**
     * Determines whether the sequence contains the specified value.
     *
     * @param value the value to search for
     * @return {@code boolean}, {@code true} if the value is present
     */
    boolean contains(long value);

    /**
     * Aggregates values into a single result.
     *
     * @param seed the initial accumulator value
     * @param aggregator the function used to combine the accumulator with each value
     * @param <A> the accumulator and result type
     * @return {@code A}, the aggregated result
     * @throws NullPointerException if {@code aggregator} is {@code null}
     */
    <A> A aggregate(A seed, BiFunction<? super A, ? super Long, ? extends A> aggregator);

    /**
     * Returns the first value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code long}, the first matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     */
    long firstByLong(LongPredicate predicate);

    /**
     * Returns the first matching value wrapped in an {@link OptionalLong}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalLong} describing the first matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    OptionalLong firstOptionalByLong(LongPredicate predicate);

    /**
     * Returns the only value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code long}, the single matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     * @throws IllegalStateException if more than one matching value exists
     */
    long singleByLong(LongPredicate predicate);

    /**
     * Returns the only matching value wrapped in an {@link OptionalLong}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalLong} describing the single matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws IllegalStateException if more than one matching value exists
     */
    OptionalLong singleOptionalByLong(LongPredicate predicate);

    /**
     * Calculates the sum of all values.
     *
     * @return {@code long}, the sum of all values
     */
    long sum();

    /**
     * Calculates the arithmetic mean of all values.
     *
     * @return {@code double}, the average of all values
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    double average();

    /**
     * Calculates the arithmetic mean of all values if present.
     *
     * @return an {@link OptionalDouble} describing the average value, or empty if the sequence is empty
     */
    OptionalDouble averageOptional();

    /**
     * Returns the minimum value in the sequence.
     *
     * @return {@code long}, the minimum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    long min();

    /**
     * Returns the minimum value wrapped in an {@link OptionalLong}.
     *
     * @return an {@link OptionalLong} describing the minimum value, or empty if the sequence is empty
     */
    OptionalLong minOptional();

    /**
     * Returns the maximum value in the sequence.
     *
     * @return {@code long}, the maximum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    long max();

    /**
     * Returns the maximum value wrapped in an {@link OptionalLong}.
     *
     * @return an {@link OptionalLong} describing the maximum value, or empty if the sequence is empty
     */
    OptionalLong maxOptional();

    /**
     * Materializes the sequence into a primitive array.
     *
     * @return a {@code long[]} containing all values in iteration order
     */
    long[] toLongArray();

}
