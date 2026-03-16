package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code int} values.
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface IntEnumerable
    extends BaseEnumerable<Integer, IntEnumerable>, PrimitiveEnumerable<Integer, IntEnumerable> {
    /**
     * Filters elements by an {@code int}-specific predicate.
     *
     * @param predicate the condition used to test each {@code int} value
     * @return an {@code IntEnumerable} containing matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    IntEnumerable whereByInt(IntPredicate predicate);

    /**
     * Maps each value to another {@code int} value.
     *
     * @param selector the mapping function applied to each value
     * @return an {@code IntEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    IntEnumerable select(IntUnaryOperator selector);

    /**
     * Maps each value to an object value.
     *
     * @param selector the mapping function applied to each value
     * @param <R> the target element type
     * @return an {@link Enumerable} containing mapped object values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    <R> Enumerable<R> selectToObj(IntFunction<? extends R> selector);

    /**
     * Maps each value to a {@code long}.
     *
     * @param selector the mapping function applied to each value
     * @return a {@link LongEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    LongEnumerable mapToLong(IntToLongFunction selector);

    /**
     * Maps each value to a {@code double}.
     *
     * @param selector the mapping function applied to each value
     * @return a {@link DoubleEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    DoubleEnumerable mapToDouble(IntToDoubleFunction selector);

    /**
     * Skips the first {@code count} values.
     *
     * @param count the number of values to skip from the start of the sequence
     * @return an {@code IntEnumerable} representing the remaining values
     */
    IntEnumerable skip(long count);

    /**
     * Takes the first {@code count} values.
     *
     * @param count the number of values to keep from the start of the sequence
     * @return an {@code IntEnumerable} containing the kept values
     */
    IntEnumerable take(long count);

    /**
     * Returns the first value in the sequence.
     *
     * @return {@code int}, the first value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    int first();

    /**
     * Returns the first value wrapped in an {@link OptionalInt}.
     *
     * @return an {@link OptionalInt} describing the first value, or empty if the sequence is empty
     */
    OptionalInt firstOptional();

    /**
     * Returns the only value in the sequence.
     *
     * @return {@code int}, the single value
     * @throws java.util.NoSuchElementException if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    int single();

    /**
     * Returns the only value wrapped in an {@link OptionalInt}.
     *
     * @return an {@link OptionalInt} describing the single value, or empty if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    OptionalInt singleOptional();

    /**
     * Concatenates this sequence with another {@code int} sequence.
     *
     * @param other the sequence appended after this sequence
     * @return an {@code IntEnumerable} containing values from both sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    IntEnumerable concat(IntEnumerable other);

    /**
     * Appends a single value to the end of the sequence.
     *
     * @param value the value appended after the current sequence
     * @return an {@code IntEnumerable} ending with {@code value}
     */
    IntEnumerable append(int value);

    /**
     * Prepends a single value to the beginning of the sequence.
     *
     * @param value the value inserted before the current sequence
     * @return an {@code IntEnumerable} starting with {@code value}
     */
    IntEnumerable prepend(int value);

    /**
     * Removes duplicate values from the sequence.
     *
     * @return an {@code IntEnumerable} containing distinct values
     */
    IntEnumerable distinct();

    /**
     * Determines whether any value matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if at least one value matches
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean anyByInt(IntPredicate predicate);

    /**
     * Determines whether all values match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if all values match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean allByInt(IntPredicate predicate);

    /**
     * Counts values that match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code long}, the number of matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    long countByInt(IntPredicate predicate);

    /**
     * Determines whether the sequence contains the specified value.
     *
     * @param value the value to search for
     * @return {@code boolean}, {@code true} if the value is present
     */
    boolean contains(int value);

    /**
     * Aggregates values into a single result.
     *
     * @param seed the initial accumulator value
     * @param aggregator the function used to combine the accumulator with each value
     * @param <A> the accumulator and result type
     * @return {@code A}, the aggregated result
     * @throws NullPointerException if {@code aggregator} is {@code null}
     */
    <A> A aggregate(A seed, BiFunction<? super A, ? super Integer, ? extends A> aggregator);

    /**
     * Returns the first value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code int}, the first matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     */
    int firstByInt(IntPredicate predicate);

    /**
     * Returns the first matching value wrapped in an {@link OptionalInt}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalInt} describing the first matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    OptionalInt firstOptionalByInt(IntPredicate predicate);

    /**
     * Returns the only value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code int}, the single matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     * @throws IllegalStateException if more than one matching value exists
     */
    int singleByInt(IntPredicate predicate);

    /**
     * Returns the only matching value wrapped in an {@link OptionalInt}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalInt} describing the single matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws IllegalStateException if more than one matching value exists
     */
    OptionalInt singleOptionalByInt(IntPredicate predicate);

    /**
     * Calculates the sum of all values.
     *
     * @return {@code int}, the sum of all values
     */
    int sum();

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
     * @return {@code int}, the minimum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    int min();

    /**
     * Returns the minimum value wrapped in an {@link OptionalInt}.
     *
     * @return an {@link OptionalInt} describing the minimum value, or empty if the sequence is empty
     */
    OptionalInt minOptional();

    /**
     * Returns the maximum value in the sequence.
     *
     * @return {@code int}, the maximum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    int max();

    /**
     * Returns the maximum value wrapped in an {@link OptionalInt}.
     *
     * @return an {@link OptionalInt} describing the maximum value, or empty if the sequence is empty
     */
    OptionalInt maxOptional();

    /**
     * Materializes the sequence into a primitive array.
     *
     * @return an {@code int[]} containing all values in iteration order
     */
    int[] toIntArray();

}
