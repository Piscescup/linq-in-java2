package io.github.piscescup.linq4j2.primitive;

import io.github.piscescup.linq4j2.Enumerable;
import io.github.piscescup.linq4j2.BaseEnumerable;
import io.github.piscescup.linq4j2.Enumerator;
import io.github.piscescup.linq4j2.Groupable;
import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.primitive.IntBinFunction;
import io.github.piscescup.util.validation.NullCheck;

import java.util.Comparator;
import java.util.NoSuchElementException;
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
     * Groups values according to a specified key selector function.
     *
     * @param keySelector the function used to extract the grouping key from each value
     * @param <K> the key type
     * @return an {@link Enumerable} containing grouped values keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K> Enumerable<Groupable<K, Integer>> groupBy(IntFunction<? extends K> keySelector);

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
    <K> Enumerable<Groupable<K, Integer>> groupBy(
        IntFunction<? extends K> keySelector,
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
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector
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
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
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
        IntFunction<? extends K> keySelector,
        BiFunction<? super K, ? super IntEnumerable, ? extends R> resultSelector
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
        IntFunction<? extends K> keySelector,
        BiFunction<? super K, ? super IntEnumerable, ? extends R> resultSelector,
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
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
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
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

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
     * Splits the sequence into consecutive chunks of the specified size.
     *
     * @param size the number of values in each chunk
     * @return an {@link Enumerable} whose elements are chunked {@link IntEnumerable} subsequences
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<IntEnumerable> chunk(int size);

    /**
     * Splits the sequence into consecutive chunks materialized as lists.
     *
     * @param size the number of values in each chunk
     * @return an {@link Enumerable} whose elements are chunked {@link java.util.List} instances
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<java.util.List<Integer>> chunkAsList(int size);

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
     * @return an {@code IntEnumerable} containing the longest matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    IntEnumerable takeWhileByInt(IntPredicate predicate);

    /**
     * Skips values from the start of the sequence while the predicate remains {@code true}.
     *
     * @param predicate the condition used to decide whether values should be skipped
     * @return an {@code IntEnumerable} containing the remaining suffix after skipping the matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    IntEnumerable skipWhileByInt(IntPredicate predicate);

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
     * Produces the set intersection of this sequence and another sequence by using the default
     * equality comparer to compare values.
     *
     * @param other the sequence whose distinct elements are used to intersect with this sequence
     * @return an {@code IntEnumerable} containing the set intersection of the two sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    IntEnumerable intersect(IntEnumerable other);

    /**
     * Produces the set intersection of this sequence and another sequence by using the supplied
     * equalator to compare values.
     *
     * @param other the sequence whose distinct elements are used to intersect with this sequence
     * @param equalator the equality comparer used to compare values
     * @return an {@code IntEnumerable} containing the set intersection of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    IntEnumerable intersect(IntEnumerable other, Equalator<? super Integer> equalator);

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function.
     *
     * @param other the sequence of keys used to intersect with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@code IntEnumerable} containing values whose selected keys exist in {@code other}
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> IntEnumerable intersectBy(Enumerable<? extends K> other, IntFunction<? extends K> keySelector);

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function and equalator.
     *
     * @param other the sequence of keys used to intersect with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @return an {@code IntEnumerable} containing values whose selected keys exist in {@code other}
     * @throws NullPointerException if {@code other}, {@code keySelector}, or {@code equalator} is {@code null}
     */
    <K> IntEnumerable intersectBy(
        Enumerable<? extends K> other,
        IntFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Produces the set union of this sequence and another sequence by using the default equality
     * comparer.
     *
     * @param other the sequence whose elements are united with this sequence
     * @return an {@code IntEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    IntEnumerable union(IntEnumerable other);

    /**
     * Produces the set union of this sequence and another sequence by using the supplied equalator.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param equalator the equality comparer used to compare values
     * @return an {@code IntEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    IntEnumerable union(IntEnumerable other, Equalator<? super Integer> equalator);

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@code IntEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> IntEnumerable unionBy(IntEnumerable other, IntFunction<? extends K> keySelector);

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function and equalator.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param keySelector the function used to extract the comparison key from each value
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @return an {@code IntEnumerable} containing the set union of the two sequences
     * @throws NullPointerException if {@code other}, {@code keySelector}, or {@code equalator} is {@code null}
     */
    <K> IntEnumerable unionBy(
        IntEnumerable other,
        IntFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer values
     * @param innerKeySelector the function used to extract the join key from inner values
     * @param resultSelector the function used to create a result element from matching values
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@link Enumerable} containing result elements created from matching pairs
     */
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction< ? extends R> resultSelector
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction< ? extends R> resultSelector,
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction< ? extends R> resultSelector
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction< ? extends R> resultSelector,
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector
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
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector,
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
    <R> Enumerable<R> zip(IntEnumerable second, IntBinFunction< ? extends R> resultSelector);

    /**
     * Produces a sequence of entries with values from this sequence and another sequence.
     *
     * @param second the second sequence
     * @return an {@link Enumerable} of {@link BinEntry} values containing corresponding values
     * @throws NullPointerException if {@code second} is {@code null}
     */
    Enumerable<BinEntry<Integer, Integer>> zip(IntEnumerable second);

    /**
     * Produces a sequence of entries with values from this sequence and two additional sequences.
     *
     * @param second the second sequence
     * @param third the third sequence
     * @return an {@link Enumerable} of {@link TriEntry} values containing corresponding values
     * @throws NullPointerException if {@code second} or {@code third} is {@code null}
     */
    Enumerable<TriEntry<Integer, Integer, Integer>> zip(IntEnumerable second, IntEnumerable third);

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
     * Returns the value whose selected comparable key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return {@code int}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> int minBy(IntFunction<? extends K> keySelector) {
        return minBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return {@code int}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> int minBy(IntFunction<? extends K> keySelector, Comparator<? super K> comparator) {
        return minOptionalBy(keySelector, comparator).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code int} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int minByInt(IntUnaryOperator keySelector) {
        return minOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code long} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int minByLong(IntToLongFunction keySelector) {
        return minOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code double} key is minimal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int minByDouble(IntToDoubleFunction keySelector) {
        return minOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected comparable key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@link OptionalInt} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> OptionalInt minOptionalBy(
        IntFunction<? extends K> keySelector
    ) {
        return minOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OptionalInt} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> OptionalInt minOptionalBy(
        IntFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int minValue = enumerator.current();
            K minKey = keySelector.apply(minValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, minKey) < 0) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalInt.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code int} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt minOptionalByInt(IntUnaryOperator keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int minValue = enumerator.current();
            int minKey = keySelector.applyAsInt(minValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey < minKey) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalInt.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code long} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt minOptionalByLong(IntToLongFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int minValue = enumerator.current();
            long minKey = keySelector.applyAsLong(minValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey < minKey) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalInt.of(minValue);
        }
    }

    /**
     * Returns the value whose selected {@code double} key is minimal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt minOptionalByDouble(IntToDoubleFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int minValue = enumerator.current();
            double minKey = keySelector.applyAsDouble(minValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, minKey) < 0) {
                    minValue = current;
                    minKey = currentKey;
                }
            }
            return OptionalInt.of(minValue);
        }
    }

    /**
     * Returns the value whose selected comparable key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return {@code int}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> int maxBy(IntFunction<? extends K> keySelector) {
        return maxBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return {@code int}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> int maxBy(IntFunction<? extends K> keySelector, Comparator<? super K> comparator) {
        return maxOptionalBy(keySelector, comparator).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code int} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int maxByInt(IntUnaryOperator keySelector) {
        return maxOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code long} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int maxByLong(IntToLongFunction keySelector) {
        return maxOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected {@code double} key is maximal.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return {@code int}, the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default int maxByDouble(IntToDoubleFunction keySelector) {
        return maxOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the value whose selected comparable key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param <K> the key type
     * @return an {@link OptionalInt} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> OptionalInt maxOptionalBy(
        IntFunction<? extends K> keySelector
    ) {
        return maxOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the value whose selected key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OptionalInt} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> OptionalInt maxOptionalBy(
        IntFunction<? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int maxValue = enumerator.current();
            K maxKey = keySelector.apply(maxValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, maxKey) > 0) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalInt.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code int} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt maxOptionalByInt(IntUnaryOperator keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int maxValue = enumerator.current();
            int maxKey = keySelector.applyAsInt(maxValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey > maxKey) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalInt.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code long} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt maxOptionalByLong(IntToLongFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int maxValue = enumerator.current();
            long maxKey = keySelector.applyAsLong(maxValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey > maxKey) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalInt.of(maxValue);
        }
    }

    /**
     * Returns the value whose selected {@code double} key is maximal if present.
     *
     * @param keySelector the function used to extract the comparison key from each value
     * @return an {@link OptionalInt} describing the value whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default OptionalInt maxOptionalByDouble(IntToDoubleFunction keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<Integer> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return OptionalInt.empty();
            }

            int maxValue = enumerator.current();
            double maxKey = keySelector.applyAsDouble(maxValue);
            while (enumerator.moveNext()) {
                int current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, maxKey) > 0) {
                    maxValue = current;
                    maxKey = currentKey;
                }
            }
            return OptionalInt.of(maxValue);
        }
    }

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
