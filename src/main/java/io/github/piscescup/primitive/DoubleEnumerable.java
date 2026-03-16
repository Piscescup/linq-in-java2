package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code double} values.
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface DoubleEnumerable
    extends BaseEnumerable<Double, DoubleEnumerable>, PrimitiveEnumerable<Double, DoubleEnumerable> {
    /**
     * Filters elements by a {@code double}-specific predicate.
     *
     * @param predicate the condition used to test each {@code double} value
     * @return a {@code DoubleEnumerable} containing matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    DoubleEnumerable whereByDouble(DoublePredicate predicate);

    /**
     * Maps each value to another {@code double} value.
     *
     * @param selector the mapping function applied to each value
     * @return a {@code DoubleEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    DoubleEnumerable select(DoubleUnaryOperator selector);

    /**
     * Maps each value to an object value.
     *
     * @param selector the mapping function applied to each value
     * @param <R> the target element type
     * @return an {@link Enumerable} containing mapped object values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    <R> Enumerable<R> selectToObj(DoubleFunction<? extends R> selector);

    /**
     * Maps each value to an {@code int}.
     *
     * @param selector the mapping function applied to each value
     * @return an {@link IntEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    IntEnumerable mapToInt(DoubleToIntFunction selector);

    /**
     * Maps each value to a {@code long}.
     *
     * @param selector the mapping function applied to each value
     * @return a {@link LongEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    LongEnumerable mapToLong(DoubleToLongFunction selector);

    /**
     * Skips the first {@code count} values.
     *
     * @param count the number of values to skip from the start of the sequence
     * @return a {@code DoubleEnumerable} representing the remaining values
     */
    DoubleEnumerable skip(long count);

    /**
     * Takes the first {@code count} values.
     *
     * @param count the number of values to keep from the start of the sequence
     * @return a {@code DoubleEnumerable} containing the kept values
     */
    DoubleEnumerable take(long count);

    /**
     * Returns the first value in the sequence.
     *
     * @return {@code double}, the first value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    double first();

    /**
     * Returns the first value wrapped in an {@link OptionalDouble}.
     *
     * @return an {@link OptionalDouble} describing the first value, or empty if the sequence is empty
     */
    OptionalDouble firstOptional();

    /**
     * Returns the only value in the sequence.
     *
     * @return {@code double}, the single value
     * @throws java.util.NoSuchElementException if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    double single();

    /**
     * Returns the only value wrapped in an {@link OptionalDouble}.
     *
     * @return an {@link OptionalDouble} describing the single value, or empty if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one value
     */
    OptionalDouble singleOptional();

    /**
     * Concatenates this sequence with another {@code double} sequence.
     *
     * @param other the sequence appended after this sequence
     * @return a {@code DoubleEnumerable} containing values from both sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    DoubleEnumerable concat(DoubleEnumerable other);

    /**
     * Appends a single value to the end of the sequence.
     *
     * @param value the value appended after the current sequence
     * @return a {@code DoubleEnumerable} ending with {@code value}
     */
    DoubleEnumerable append(double value);

    /**
     * Prepends a single value to the beginning of the sequence.
     *
     * @param value the value inserted before the current sequence
     * @return a {@code DoubleEnumerable} starting with {@code value}
     */
    DoubleEnumerable prepend(double value);

    /**
     * Removes duplicate values from the sequence.
     *
     * @return a {@code DoubleEnumerable} containing distinct values
     */
    DoubleEnumerable distinct();

    /**
     * Determines whether any value matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if at least one value matches
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean anyByDouble(DoublePredicate predicate);

    /**
     * Determines whether all values match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code boolean}, {@code true} if all values match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean allByDouble(DoublePredicate predicate);

    /**
     * Counts values that match the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code long}, the number of matching values
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    long countByDouble(DoublePredicate predicate);

    /**
     * Determines whether the sequence contains the specified value.
     *
     * @param value the value to search for
     * @return {@code boolean}, {@code true} if the value is present
     */
    boolean contains(double value);

    /**
     * Aggregates values into a single result.
     *
     * @param seed the initial accumulator value
     * @param aggregator the function used to combine the accumulator with each value
     * @param <A> the accumulator and result type
     * @return {@code A}, the aggregated result
     * @throws NullPointerException if {@code aggregator} is {@code null}
     */
    <A> A aggregate(A seed, BiFunction<? super A, ? super Double, ? extends A> aggregator);

    /**
     * Returns the first value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code double}, the first matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     */
    double firstByDouble(DoublePredicate predicate);

    /**
     * Returns the first matching value wrapped in an {@link OptionalDouble}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalDouble} describing the first matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    OptionalDouble firstOptionalByDouble(DoublePredicate predicate);

    /**
     * Returns the only value that matches the supplied predicate.
     *
     * @param predicate the condition used to test each value
     * @return {@code double}, the single matching value
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching value exists
     * @throws IllegalStateException if more than one matching value exists
     */
    double singleByDouble(DoublePredicate predicate);

    /**
     * Returns the only matching value wrapped in an {@link OptionalDouble}.
     *
     * @param predicate the condition used to test each value
     * @return an {@link OptionalDouble} describing the single matching value, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws IllegalStateException if more than one matching value exists
     */
    OptionalDouble singleOptionalByDouble(DoublePredicate predicate);

    /**
     * Calculates the sum of all values.
     *
     * @return {@code double}, the sum of all values
     */
    double sum();

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
     * @return {@code double}, the minimum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    double min();

    /**
     * Returns the minimum value wrapped in an {@link OptionalDouble}.
     *
     * @return an {@link OptionalDouble} describing the minimum value, or empty if the sequence is empty
     */
    OptionalDouble minOptional();

    /**
     * Returns the maximum value in the sequence.
     *
     * @return {@code double}, the maximum value
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    double max();

    /**
     * Returns the maximum value wrapped in an {@link OptionalDouble}.
     *
     * @return an {@link OptionalDouble} describing the maximum value, or empty if the sequence is empty
     */
    OptionalDouble maxOptional();

    /**
     * Materializes the sequence into a primitive array.
     *
     * @return a {@code double[]} containing all values in iteration order
     */
    double[] toDoubleArray();

}
