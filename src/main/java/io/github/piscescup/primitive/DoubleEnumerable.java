package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;
import io.github.piscescup.Enumerator;
import io.github.piscescup.Groupable;
import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.BinFunction;
import io.github.piscescup.interfaces.exfunction.primitive.DoubleBinFunction;
import io.github.piscescup.util.validation.NullCheck;

import java.util.Comparator;
import java.util.NoSuchElementException;
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
     * Groups values according to a specified key selector function.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param <K> the key type
     * @return an {@link Enumerable} containing grouped values keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K> Enumerable<Groupable<K, Double>> groupBy(DoubleFunction<? extends K> keySelector);

    /**
     * Groups values according to a specified key selector function and compares keys by using
     * the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @return an {@link Enumerable} containing grouped values keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} or {@code equalator} is {@code null}
     */
    <K> Enumerable<Groupable<K, Double>> groupBy(
        DoubleFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups values according to a specified key selector function and projects the elements for
     * each group.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param elementSelector the function used to project grouped values
     * @param <K> the key type
     * @param <E> the projected element type
     * @return an {@link Enumerable} containing grouped projected elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} or {@code elementSelector} is {@code null}
     */
    <K, E> Enumerable<Groupable<K, E>> groupBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector
    );

    /**
     * Groups values according to a specified key selector function, projects the elements for each
     * group, and compares keys by using the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param elementSelector the function used to project grouped values
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <E> the projected element type
     * @return an {@link Enumerable} containing grouped projected elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, or {@code equalator} is {@code null}
     */
    <K, E> Enumerable<Groupable<K, E>> groupBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups values according to a specified key selector function and creates a result value from
     * each group and its key.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param resultSelector the function used to create a result value from each group and its key
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing projected group results
     * @throws NullPointerException if {@code keySelector} or {@code resultSelector} is {@code null}
     */
    <K, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        BiFunction<? super K, ? super DoubleEnumerable, ? extends R> resultSelector
    );

    /**
     * Groups values according to a specified key selector function, creates a result value from
     * each group and its key, and compares keys by using the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param resultSelector the function used to create a result value from each group and its key
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing projected group results
     * @throws NullPointerException if {@code keySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        BiFunction<? super K, ? super DoubleEnumerable, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups values according to a specified key selector function, projects the elements of each
     * group, and creates a result value from each group and its key.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param elementSelector the function used to project grouped values
     * @param resultSelector the function used to create a result value from each group and its key
     * @param <K> the key type
     * @param <E> the projected element type
     * @param <R> the result type
     * @return an {@link Enumerable} containing projected group results
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, or {@code resultSelector} is {@code null}
     */
    <K, E, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    );

    /**
     * Groups values according to a specified key selector function, projects the elements of each
     * group, creates a result value from each group and its key, and compares keys by using the
     * supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param elementSelector the function used to project grouped values
     * @param resultSelector the function used to create a result value from each group and its key
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <E> the projected element type
     * @param <R> the result type
     * @return an {@link Enumerable} containing projected group results
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, E, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

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
     * Splits the sequence into consecutive chunks of the specified size.
     *
     * @param size the number of values in each chunk
     * @return an {@link Enumerable} whose elements are chunked {@link DoubleEnumerable} subsequences
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<DoubleEnumerable> chunk(int size);

    /**
     * Splits the sequence into consecutive chunks materialized as lists.
     *
     * @param size the number of values in each chunk
     * @return an {@link Enumerable} whose elements are chunked {@link java.util.List} instances
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<java.util.List<Double>> chunkAsList(int size);

    /**
     * Extracts the sequence into an instance of the specified target type.
     *
     * @param clazz the target type token
     * @param <R> the target type
     * @return an instance of {@code R} containing data extracted from this sequence
     * @throws NullPointerException if {@code clazz} is {@code null}
     */
    <R> R extractTo(Class<R> clazz);

    /**
     * Takes values from the start of the sequence while the predicate remains {@code true}.
     *
     * @param predicate the condition used to decide whether iteration should continue
     * @return a {@code DoubleEnumerable} containing the longest matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    DoubleEnumerable takeWhileByDouble(DoublePredicate predicate);

    /**
     * Skips values from the start of the sequence while the predicate remains {@code true}.
     *
     * @param predicate the condition used to decide whether values should be skipped
     * @return a {@code DoubleEnumerable} containing the remaining suffix after skipping the matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    DoubleEnumerable skipWhileByDouble(DoublePredicate predicate);

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
     * Produces the set intersection of this sequence and another sequence by using the default
     * equality comparer to compare values.
     */
    DoubleEnumerable intersect(DoubleEnumerable other);

    /**
     * Produces the set intersection of this sequence and another sequence by using the supplied
     * equalator to compare values.
     *
     * @param other the sequence whose distinct elements are used to intersect with this sequence
     * @param equalator the equality comparer used to compare values
     * @return a {@code DoubleEnumerable} containing the set intersection of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    DoubleEnumerable intersect(DoubleEnumerable other, Equalator<? super Double> equalator);

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function.
     *
     * @param other the sequence of keys used to intersect with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return a {@code DoubleEnumerable} containing values whose selected keys exist in {@code other}
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> DoubleEnumerable intersectBy(Enumerable<? extends K> other, DoubleFunction<? extends K> keySelector);

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function and equalator.
     */
    <K> DoubleEnumerable intersectBy(
        Enumerable<? extends K> other,
        DoubleFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Produces the set union of this sequence and another sequence by using the default equality
     * comparer.
     */
    DoubleEnumerable union(DoubleEnumerable other);

    /**
     * Produces the set union of this sequence and another sequence by using the supplied equalator.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param equalator the equality comparer used to compare values
     * @return a {@code DoubleEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    DoubleEnumerable union(DoubleEnumerable other, Equalator<? super Double> equalator);

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return a {@code DoubleEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> DoubleEnumerable unionBy(DoubleEnumerable other, DoubleFunction<? extends K> keySelector);

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function and equalator.
     */
    <K> DoubleEnumerable unionBy(
        DoubleEnumerable other,
        DoubleFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from matching values
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from matching pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <K, R> Enumerable<R> join(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys by using
     * the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from matching values
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from matching pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, R> Enumerable<R> join(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys and
     * includes all outer values.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from outer values and matching inner values
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from left join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <K, R> Enumerable<R> leftJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys and
     * includes all outer values by using the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from outer values and matching inner values
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from left join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, R> Enumerable<R> leftJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys and
     * includes all inner values.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from outer values and matching inner values
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from right join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <K, R> Enumerable<R> rightJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector
    );

    /**
     * Correlates the values of this sequence with another sequence based on matching keys and
     * includes all inner values by using the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from outer values and matching inner values
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from right join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, R> Enumerable<R> rightJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Applies a specified function to the corresponding values of this sequence and another
     * sequence, producing a sequence of results.
     *
     * @param second the second sequence
     * @param resultSelector the function used to project each pair of corresponding values
     * @param <R> the result type
     * @return an {@link Enumerable} containing projected zip results
     * @throws NullPointerException if {@code second} or {@code resultSelector} is {@code null}
     */
    <R> Enumerable<R> zip(DoubleEnumerable second, DoubleBinFunction<? extends R> resultSelector);

    /**
     * Produces a sequence of entries with values from this sequence and another sequence.
     *
     * @param second the second sequence
     * @return an {@link Enumerable} of {@link BinEntry} values containing corresponding values
     * @throws NullPointerException if {@code second} is {@code null}
     */
    Enumerable<BinEntry<Double, Double>> zip(DoubleEnumerable second);

    /**
     * Produces a sequence of entries with values from this sequence and two additional sequences.
     *
     * @param second the second sequence
     * @param third the third sequence
     * @return an {@link Enumerable} of {@link TriEntry} values containing corresponding values
     * @throws NullPointerException if {@code second} or {@code third} is {@code null}
     */
    Enumerable<TriEntry<Double, Double, Double>> zip(DoubleEnumerable second, DoubleEnumerable third);

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
     * Returns the value whose selected comparable key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return {@code double}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> double minBy(DoubleFunction<? extends K> keySelector) {
        return minBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return {@code double}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> double minBy(
        DoubleFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        return minOptionalBy(keySelector, comparator).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code int} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double minByInt(DoubleToIntFunction keySelector) {
        return minOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code long} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double minByLong(DoubleToLongFunction keySelector) {
        return minOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code double} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double minByDouble(DoubleUnaryOperator keySelector) {
        return minOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected comparable key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@link OptionalDouble} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> OptionalDouble minOptionalBy(
        DoubleFunction<? extends K> keySelector
    ) {
        return minOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OptionalDouble} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> OptionalDouble minOptionalBy(
        DoubleFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double minValue = enumerator.current();
            K minKey = keySelector.apply(minValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, minKey) < 0) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalDouble.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code int} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble minOptionalByInt(DoubleToIntFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double minValue = enumerator.current();
            int minKey = keySelector.applyAsInt(minValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey < minKey) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalDouble.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code long} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble minOptionalByLong(DoubleToLongFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double minValue = enumerator.current();
            long minKey = keySelector.applyAsLong(minValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey < minKey) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalDouble.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code double} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble minOptionalByDouble(DoubleUnaryOperator keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double minValue = enumerator.current();
            double minKey = keySelector.applyAsDouble(minValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, minKey) < 0) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalDouble.of(minValue);
        }
    }

    /**
     * Returns the value whose selected comparable key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return {@code double}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> double maxBy(DoubleFunction<? extends K> keySelector) {
        return maxBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return {@code double}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> double maxBy(
        DoubleFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        return maxOptionalBy(keySelector, comparator).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code int} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double maxByInt(DoubleToIntFunction keySelector) {
        return maxOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code long} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double maxByLong(DoubleToLongFunction keySelector) {
        return maxOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code double} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code double}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default double maxByDouble(DoubleUnaryOperator keySelector) {
        return maxOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected comparable key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@link OptionalDouble} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> OptionalDouble maxOptionalBy(
        DoubleFunction<? extends K> keySelector
    ) {
        return maxOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OptionalDouble} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> OptionalDouble maxOptionalBy(
        DoubleFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double maxValue = enumerator.current();
            K maxKey = keySelector.apply(maxValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, maxKey) > 0) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalDouble.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code int} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble maxOptionalByInt(DoubleToIntFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double maxValue = enumerator.current();
            int maxKey = keySelector.applyAsInt(maxValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey > maxKey) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalDouble.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code long} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble maxOptionalByLong(DoubleToLongFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double maxValue = enumerator.current();
            long maxKey = keySelector.applyAsLong(maxValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey > maxKey) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalDouble.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code double} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalDouble} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalDouble maxOptionalByDouble(DoubleUnaryOperator keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Double> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalDouble.empty();
            }

            double maxValue = enumerator.current();
            double maxKey = keySelector.applyAsDouble(maxValue);
            while (enumerator.moveNext()) {
                double current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, maxKey) > 0) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalDouble.of(maxValue);
        }
    }

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
