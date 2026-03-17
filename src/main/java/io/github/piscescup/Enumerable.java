package io.github.piscescup;

import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.exfunction.BinFunction;
import io.github.piscescup.primitive.DoubleEnumerable;
import io.github.piscescup.primitive.IntEnumerable;
import io.github.piscescup.primitive.LongEnumerable;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.util.validation.NullCheck;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.*;

/**
 * Default reference-type enumerable.
 *
 * @param <T> the element type
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface Enumerable<T> extends BaseEnumerable<T, Enumerable<T>> {
    /**
     * Skips the first {@code count} elements.
     *
     * @param count the number of elements to skip
     * @return an {@code Enumerable<T>} representing the remaining elements
     */
    @Override
    Enumerable<T> skip(long count);

    /**
     * Takes the first {@code count} elements.
     *
     * @param count the number of elements to keep from the start of the sequence
     * @return an {@code Enumerable<T>} containing the kept elements
     */
    @Override
    Enumerable<T> take(long count);

    /**
     * Filters elements by the supplied predicate.
     *
     * @param predicate the condition used to decide whether an element is kept
     * @return an {@code Enumerable<T>} containing elements that satisfy the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    Enumerable<T> where(Predicate<? super T> predicate);

    /**
     * Takes elements from the start of the sequence while the predicate remains {@code true}.
     *
     * @param predicate the condition used to decide whether iteration should continue
     * @return an {@code Enumerable<T>} containing the longest matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    Enumerable<T> takeWhile(Predicate<? super T> predicate);

    /**
     * Skips elements from the start of the sequence while the predicate remains {@code true}.
     *
     * @param predicate the condition used to decide whether elements should be skipped
     * @return an {@code Enumerable<T>} containing the remaining suffix after skipping the matching prefix
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    Enumerable<T> skipWhile(Predicate<? super T> predicate);

    /**
     * Concatenates this sequence with another sequence of compatible elements.
     *
     * @param other the sequence appended after this sequence
     * @return an {@code Enumerable<T>} containing all elements from both sequences in order
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Enumerable<T> concat(Enumerable<? extends T> other);

    /**
     * Produces the set union of this sequence and another sequence by using the default equality
     * comparer.
     *
     * @param other the sequence whose elements are united with this sequence
     * @return an {@code Enumerable<T>} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Enumerable<T> union(Enumerable<? extends T> other);

    /**
     * Produces the set union of this sequence and another sequence by using the supplied equalator.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param equalator the equality comparer used to compare values
     * @return an {@code Enumerable<T>} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    Enumerable<T> union(
        Enumerable<? extends T> other,
        Equalator<? super T> equalator
    );

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return an {@code Enumerable<T>} containing the set union of the two sequences
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> Enumerable<T> unionBy(
        Enumerable<? extends T> other,
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Produces the set union of this sequence and another sequence according to a specified key
     * selector function and equalator.
     *
     * @param other the sequence whose elements are united with this sequence
     * @param keySelector the function used to extract the comparison key from each element
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @return an {@code Enumerable<T>} containing the set union of the two sequences
     * @throws NullPointerException if {@code other}, {@code keySelector}, or {@code equalator} is {@code null}
     */
    <K> Enumerable<T> unionBy(
        Enumerable<? extends T> other,
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Produces the set intersection of this sequence and another sequence by using the default
     * equality comparer to compare values.
     *
     * @param other the sequence whose distinct elements are used to intersect with this sequence
     * @return an {@code Enumerable<T>} containing the set intersection of the two sequences
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Enumerable<T> intersect(Enumerable<? extends T> other);

    /**
     * Produces the set intersection of this sequence and another sequence by using the supplied
     * equalator to compare values.
     *
     * @param other the sequence whose distinct elements are used to intersect with this sequence
     * @param equalator the equality comparer used to compare values
     * @return an {@code Enumerable<T>} containing the set intersection of the two sequences
     * @throws NullPointerException if {@code other} or {@code equalator} is {@code null}
     */
    Enumerable<T> intersect(
        Enumerable<? extends T> other,
        Equalator<? super T> equalator
    );

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function.
     *
     * @param other the sequence of keys used to intersect with this sequence
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return an {@code Enumerable<T>} containing elements whose selected keys exist in {@code other}
     * @throws NullPointerException if {@code other} or {@code keySelector} is {@code null}
     */
    <K> Enumerable<T> intersectBy(
        Enumerable<? extends K> other,
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Produces the set intersection of this sequence and a sequence of keys according to a
     * specified key selector function and equalator.
     *
     * @param other the sequence of keys used to intersect with this sequence
     * @param keySelector the function used to extract the comparison key from each element
     * @param equalator the equality comparer used to compare keys
     * @param <K> the key type
     * @return an {@code Enumerable<T>} containing elements whose selected keys exist in {@code other}
     * @throws NullPointerException if {@code other}, {@code keySelector}, or {@code equalator} is {@code null}
     */
    <K> Enumerable<T> intersectBy(
        Enumerable<? extends K> other,
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys. The
     * default equality comparer is used to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from matching outer and inner elements
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from matching pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <TInner, K, R> Enumerable<R> join(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys by
     * using the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from matching outer and inner elements
     * @param equalator the equality comparer used to compare keys
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from matching pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <TInner, K, R> Enumerable<R> join(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys and
     * includes all outer elements. The default equality comparer is used to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from outer elements and matching inner elements
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from left join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <TInner, K, R> Enumerable<R> leftJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys and
     * includes all outer elements by using the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from outer elements and matching inner elements
     * @param equalator the equality comparer used to compare keys
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from left join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <TInner, K, R> Enumerable<R> leftJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys and
     * includes all inner elements. The default equality comparer is used to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from outer elements and matching inner elements
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from right join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, or {@code resultSelector} is {@code null}
     */
    <TInner, K, R> Enumerable<R> rightJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    );

    /**
     * Correlates the elements of this sequence with another sequence based on matching keys and
     * includes all inner elements by using the supplied equalator to compare keys.
     *
     * @param inner the sequence to join to this sequence
     * @param outerKeySelector the function used to extract the join key from outer elements
     * @param innerKeySelector the function used to extract the join key from inner elements
     * @param resultSelector the function used to create a result element from outer elements and matching inner elements
     * @param equalator the equality comparer used to compare keys
     * @param <TInner> the inner element type
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing result elements created from right join pairs
     * @throws NullPointerException if {@code inner}, {@code outerKeySelector}, {@code innerKeySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <TInner, K, R> Enumerable<R> rightJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Applies a specified function to the corresponding elements of this sequence and another
     * sequence, producing a sequence of the results.
     *
     * @param second the second sequence
     * @param resultSelector the function used to project each pair of corresponding elements
     * @param <TSecond> the second element type
     * @param <R> the result type
     * @return an {@code Enumerable<R>} containing projected zip results
     * @throws NullPointerException if {@code second} or {@code resultSelector} is {@code null}
     */
    <TSecond, R> Enumerable<R> zip(
        Enumerable<? extends TSecond> second,
        BinFunction<? super T, ? super TSecond, ? extends R> resultSelector
    );

    /**
     * Produces a sequence of entries with elements from this sequence and another sequence.
     *
     * @param second the second sequence
     * @param <TSecond> the second element type
     * @return an {@code Enumerable} of {@link BinEntry} values containing corresponding elements
     * @throws NullPointerException if {@code second} is {@code null}
     */
    <TSecond> Enumerable<BinEntry<T, TSecond>> zip(Enumerable<? extends TSecond> second);

    /**
     * Produces a sequence of entries with elements from this sequence and two additional sequences.
     *
     * @param second the second sequence
     * @param third the third sequence
     * @param <TSecond> the second element type
     * @param <TThird> the third element type
     * @return an {@code Enumerable} of {@link TriEntry} values containing corresponding elements
     * @throws NullPointerException if {@code second} or {@code third} is {@code null}
     */
    <TSecond, TThird> Enumerable<TriEntry<T, TSecond, TThird>> zip(
        Enumerable<? extends TSecond> second,
        Enumerable<? extends TThird> third
    );

    /**
     * Appends a single element to the end of the sequence.
     *
     * @param element the element appended after the current sequence
     * @return an {@code Enumerable<T>} ending with {@code element}
     */
    Enumerable<T> append(T element);

    /**
     * Prepends a single element to the beginning of the sequence.
     *
     * @param element the element inserted before the current sequence
     * @return an {@code Enumerable<T>} starting with {@code element}
     */
    Enumerable<T> prepend(T element);

    /**
     * Projects each element into a new form.
     *
     * @param selector the mapping function applied to each element
     * @param <R> the target element type
     * @return an {@code Enumerable<R>} containing mapped elements
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    <R> Enumerable<R> select(Function<? super T, ? extends R> selector);

    /**
     * Projects each element to a nested sequence and flattens the results.
     *
     * @param selector the mapping function that returns a sequence for each element
     * @param <R> the target element type
     * @return an {@code Enumerable<R>} containing the flattened projected elements
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    <R> Enumerable<R> selectMany(
        Function<? super T, ? extends Enumerable<? extends R>> selector
    );

    /**
     * Groups elements according to a specified key selector function.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param <K> the key type
     * @return an {@code Enumerable} containing grouped elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K> Enumerable<Groupable<K, T>> groupBy(Function<? super T, ? extends K> keySelector);

    /**
     * Groups elements according to a specified key selector function and compares keys by using
     * the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @return an {@code Enumerable} containing grouped elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} or {@code equalator} is {@code null}
     */
    <K> Enumerable<Groupable<K, T>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups elements according to a specified key selector function and projects the elements
     * for each group.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param elementSelector the function used to project grouped elements
     * @param <K> the key type
     * @param <E> the projected element type
     * @return an {@code Enumerable} containing grouped projected elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector} or {@code elementSelector} is {@code null}
     */
    <K, E> Enumerable<Groupable<K, E>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector
    );

    /**
     * Groups elements according to a key selector function, projects the elements for each group,
     * and compares keys by using the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param elementSelector the function used to project grouped elements
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <E> the projected element type
     * @return an {@code Enumerable} containing grouped projected elements keyed by {@code K}
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, or {@code equalator} is {@code null}
     */
    <K, E> Enumerable<Groupable<K, E>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups elements according to a specified key selector function and creates a result value
     * from each group and its key.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param resultSelector the function used to create a result value from each group and its key
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable} containing the projected group results
     * @throws NullPointerException if {@code keySelector} or {@code resultSelector} is {@code null}
     */
    <K, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super K, ? super Enumerable<T>, ? extends R> resultSelector
    );

    /**
     * Groups elements according to a specified key selector function, creates a result value from
     * each group and its key, and compares keys by using the supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param resultSelector the function used to create a result value from each group and its key
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <R> the result type
     * @return an {@code Enumerable} containing the projected group results
     * @throws NullPointerException if {@code keySelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super K, ? super Enumerable<T>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Groups elements according to a specified key selector function, projects the elements of each
     * group, and creates a result value from each group and its key.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param elementSelector the function used to project grouped elements
     * @param resultSelector the function used to create a result value from each group and its key
     * @param <K> the key type
     * @param <E> the projected element type
     * @param <R> the result type
     * @return an {@code Enumerable} containing the projected group results
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, or {@code resultSelector} is {@code null}
     */
    <K, E, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        BinFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    );

    /**
     * Groups elements according to a specified key selector function, projects the elements of each
     * group, creates a result value from each group and its key, and compares keys by using the
     * supplied equalator.
     *
     * @param keySelector the function used to extract the grouping key from each element
     * @param elementSelector the function used to project grouped elements
     * @param resultSelector the function used to create a result value from each group and its key
     * @param equalator the equality comparer used to compare grouping keys
     * @param <K> the key type
     * @param <E> the projected element type
     * @param <R> the result type
     * @return an {@code Enumerable} containing the projected group results
     * @throws NullPointerException if {@code keySelector}, {@code elementSelector}, {@code resultSelector}, or {@code equalator} is {@code null}
     */
    <K, E, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        BinFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    );

    /**
     * Orders elements by a comparable key in ascending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param <K> the key type
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K extends Comparable<? super K>> OrderedEnumerable<T> orderBy(
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Orders elements by a key using the supplied comparator.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    <K> OrderedEnumerable<T> orderBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    );

    /**
     * Orders elements by an {@code int} key in ascending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByInt(ToIntFunction<? super T> keySelector);

    /**
     * Orders elements by a {@code long} key in ascending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByLong(ToLongFunction<? super T> keySelector);

    /**
     * Orders elements by a {@code double} key in ascending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByDouble(ToDoubleFunction<? super T> keySelector);

    /**
     * Orders elements by a comparable key in descending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param <K> the key type
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    <K extends Comparable<? super K>> OrderedEnumerable<T> orderByDescending(
        Function<? super T, ? extends K> keySelector
    );

    /**
     * Orders elements by a key in descending order using the supplied comparator.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    <K> OrderedEnumerable<T> orderByDescending(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    );

    /**
     * Orders elements by an {@code int} key in descending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByIntDescending(ToIntFunction<? super T> keySelector);

    /**
     * Orders elements by a {@code long} key in descending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByLongDescending(ToLongFunction<? super T> keySelector);

    /**
     * Orders elements by a {@code double} key in descending order.
     *
     * @param keySelector the function used to extract the sort key from each element
     * @return an {@link OrderedEnumerable} whose elements are ordered by the selected key
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    OrderedEnumerable<T> orderByDoubleDescending(ToDoubleFunction<? super T> keySelector);

    /**
     * Maps each element to an {@code int} value.
     *
     * @param selector the mapping function used to produce primitive {@code int} values
     * @return an {@link IntEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    IntEnumerable mapToInt(ToIntFunction<? super T> selector);

    /**
     * Maps each element to a {@code long} value.
     *
     * @param selector the mapping function used to produce primitive {@code long} values
     * @return a {@link LongEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    LongEnumerable mapToLong(ToLongFunction<? super T> selector);

    /**
     * Maps each element to a {@code double} value.
     *
     * @param selector the mapping function used to produce primitive {@code double} values
     * @return a {@link DoubleEnumerable} containing mapped values
     * @throws NullPointerException if {@code selector} is {@code null}
     */
    DoubleEnumerable mapToDouble(ToDoubleFunction<? super T> selector);

    /**
     * Materializes the sequence into a {@link List}.
     *
     * @return a {@link List} containing all elements in iteration order
     */
    List<T> toList();

    /**
     * Splits the sequence into consecutive chunks of the specified size.
     *
     * @param size the number of elements in each chunk
     * @return an {@code Enumerable} whose elements are chunked subsequences
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<Enumerable<T>> chunk(int size);

    /**
     * Splits the sequence into consecutive chunks materialized as lists.
     *
     * @param size the number of elements in each chunk
     * @return an {@code Enumerable} whose elements are chunked {@link List} instances
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    Enumerable<List<T>> chunkAsList(int size);

    /**
     * Materializes the sequence into an {@link Object} array.
     *
     * @return an {@code Object[]} containing all elements in iteration order
     */
    Object[] toArray();

    /**
     * Materializes the sequence into a typed array.
     *
     * @param generator the array factory used to create the destination array
     * @return a {@code T[]} containing all elements in iteration order
     * @throws NullPointerException if {@code generator} is {@code null}
     */
    T[] toArray(IntFunction<T[]> generator);

    /**
     * Casts the elements of this sequence to the specified target type.
     *
     * @param clazz the target element type token
     * @param <R> the target element type
     * @return an {@code Enumerable<R>} containing the cast elements
     * @throws NullPointerException if {@code clazz} is {@code null}
     * @throws ClassCastException if an element cannot be cast to {@code R}
     */
    <R> Enumerable<R> castTo(Class<R> clazz);

    /**
     * Determines whether any element matches the supplied predicate.
     *
     * @param predicate the condition used to test elements
     * @return {@code boolean}, {@code true} if at least one element matches
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean any(Predicate<? super T> predicate);

    /**
     * Determines whether all elements match the supplied predicate.
     *
     * @param predicate the condition used to test elements
     * @return {@code boolean}, {@code true} if every element matches
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    boolean all(Predicate<? super T> predicate);

    

    /**
     * Counts the number of elements that match the supplied predicate.
     *
     * @param predicate the condition used to test elements
     * @return {@code long}, the number of matching elements
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    long countBy(Predicate<? super T> predicate);

    /**
     * Determines whether the sequence contains the specified value.
     *
     * @param value the value to search for
     * @return {@code boolean}, {@code true} if the value is present
     */
    boolean contains(T value);

    /**
     * Returns the first element in the sequence.
     *
     * @return {@code T}, the first element
     * @throws java.util.NoSuchElementException if the sequence is empty
     */
    T first();

    /**
     * Returns the first element that matches the supplied predicate.
     *
     * @param predicate the condition used to test elements
     * @return {@code T}, the first matching element
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching element exists
     */
    T first(Predicate<? super T> predicate);

    /**
     * Returns the first element wrapped in an {@link Optional}.
     *
     * @return an {@link Optional} describing the first element, or empty if the sequence is empty
     */
    Optional<T> firstOptional();

    /**
     * Returns the first matching element wrapped in an {@link Optional}.
     *
     * @param predicate the condition used to test elements
     * @return an {@link Optional} describing the first matching element, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    Optional<T> firstOptional(Predicate<? super T> predicate);

    /**
     * Returns the only element in the sequence.
     *
     * @return {@code T}, the single element in the sequence
     * @throws java.util.NoSuchElementException if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one element
     */
    T single();

    /**
     * Returns the only element that matches the supplied predicate.
     *
     * @param predicate the condition used to test elements
     * @return {@code T}, the single matching element
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws java.util.NoSuchElementException if no matching element exists
     * @throws IllegalStateException if more than one matching element exists
     */
    T single(Predicate<? super T> predicate);

    /**
     * Returns the only element wrapped in an {@link Optional}.
     *
     * @return an {@link Optional} describing the single element, or empty if the sequence is empty
     * @throws IllegalStateException if the sequence contains more than one element
     */
    Optional<T> singleOptional();

    /**
     * Returns the only matching element wrapped in an {@link Optional}.
     *
     * @param predicate the condition used to test elements
     * @return an {@link Optional} describing the single matching element, or empty if none match
     * @throws NullPointerException if {@code predicate} is {@code null}
     * @throws IllegalStateException if more than one matching element exists
     */
    Optional<T> singleOptional(Predicate<? super T> predicate);

    /**
     * Aggregates the sequence into a single result value.
     *
     * @param seed the initial accumulator value
     * @param aggregator the function used to combine the accumulator with each element
     * @param <A> the accumulator and result type
     * @return {@code A}, the final aggregated result
     * @throws NullPointerException if {@code aggregator} is {@code null}
     */
    <A> A aggregate(A seed, BinFunction<? super A, ? super T, ? extends A> aggregator);

    /**
     * Returns the element with the minimum comparable key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> T minBy(Function<? super T, ? extends K> keySelector) {
        return minBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the element with the minimum key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> T minBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        return minOptionalBy(keySelector, comparator)
            .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the minimum {@code int} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T minByInt(ToIntFunction<? super T> keySelector) {
        return minOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the minimum {@code long} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T minByLong(ToLongFunction<? super T> keySelector) {
        return minOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the minimum {@code double} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T minByDouble(ToDoubleFunction<? super T> keySelector) {
        return minOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the minimum comparable key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return an {@link Optional} describing the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> Optional<T> minOptionalBy(
        Function<? super T, ? extends K> keySelector
    ) {
        return minOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the element with the minimum key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link Optional} describing the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> Optional<T> minOptionalBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T minElement = enumerator.current();
            K minKey = keySelector.apply(minElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, minKey) < 0) {
                    minElement = current;
                    minKey = currentKey;
                }
            }
            return Optional.of(minElement);
        }
    }

    /**
     * Returns the element with the minimum {@code int} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> minOptionalByInt(ToIntFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T minElement = enumerator.current();
            int minKey = keySelector.applyAsInt(minElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey < minKey) {
                    minElement = current;
                    minKey = currentKey;
                }
            }
            return Optional.of(minElement);
        }
    }

    /**
     * Returns the element with the minimum {@code long} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> minOptionalByLong(ToLongFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T minElement = enumerator.current();
            long minKey = keySelector.applyAsLong(minElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey < minKey) {
                    minElement = current;
                    minKey = currentKey;
                }
            }
            return Optional.of(minElement);
        }
    }

    /**
     * Returns the element with the minimum {@code double} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is minimal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> minOptionalByDouble(ToDoubleFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T minElement = enumerator.current();
            double minKey = keySelector.applyAsDouble(minElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, minKey) < 0) {
                    minElement = current;
                    minKey = currentKey;
                }
            }
            return Optional.of(minElement);
        }
    }

    /**
     * Returns the element with the maximum comparable key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> T maxBy(Function<? super T, ? extends K> keySelector) {
        return maxBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the element with the maximum key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default <K> T maxBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        return maxOptionalBy(keySelector, comparator)
            .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the maximum {@code int} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T maxByInt(ToIntFunction<? super T> keySelector) {
        return maxOptionalByInt(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the maximum {@code long} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T maxByLong(ToLongFunction<? super T> keySelector) {
        return maxOptionalByLong(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the maximum {@code double} key produced by the selector.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws NoSuchElementException if the sequence is empty
     */
    default T maxByDouble(ToDoubleFunction<? super T> keySelector) {
        return maxOptionalByDouble(keySelector).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the element with the maximum comparable key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param <K> the key type
     * @return an {@link Optional} describing the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     * @throws ClassCastException if the selected keys are not mutually comparable
     */
    default <K extends Comparable<? super K>> Optional<T> maxOptionalBy(
        Function<? super T, ? extends K> keySelector
    ) {
        return maxOptionalBy(keySelector, Comparator.naturalOrder());
    }

    /**
     * Returns the element with the maximum key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @param comparator the comparator used to compare extracted keys
     * @param <K> the key type
     * @return an {@link Optional} describing the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} or {@code comparator} is {@code null}
     */
    default <K> Optional<T> maxOptionalBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T maxElement = enumerator.current();
            K maxKey = keySelector.apply(maxElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                K currentKey = keySelector.apply(current);
                if (comparator.compare(currentKey, maxKey) > 0) {
                    maxElement = current;
                    maxKey = currentKey;
                }
            }
            return Optional.of(maxElement);
        }
    }

    /**
     * Returns the element with the maximum {@code int} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> maxOptionalByInt(ToIntFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T maxElement = enumerator.current();
            int maxKey = keySelector.applyAsInt(maxElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                int currentKey = keySelector.applyAsInt(current);
                if (currentKey > maxKey) {
                    maxElement = current;
                    maxKey = currentKey;
                }
            }
            return Optional.of(maxElement);
        }
    }

    /**
     * Returns the element with the maximum {@code long} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> maxOptionalByLong(ToLongFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T maxElement = enumerator.current();
            long maxKey = keySelector.applyAsLong(maxElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                long currentKey = keySelector.applyAsLong(current);
                if (currentKey > maxKey) {
                    maxElement = current;
                    maxKey = currentKey;
                }
            }
            return Optional.of(maxElement);
        }
    }

    /**
     * Returns the element with the maximum {@code double} key produced by the selector if present.
     *
     * @param keySelector the function used to extract the comparison key from each element
     * @return an {@link Optional} describing the element whose selected key is maximal
     * @throws NullPointerException if {@code keySelector} is {@code null}
     */
    default Optional<T> maxOptionalByDouble(ToDoubleFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);

        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }

            T maxElement = enumerator.current();
            double maxKey = keySelector.applyAsDouble(maxElement);
            while (enumerator.moveNext()) {
                T current = enumerator.current();
                double currentKey = keySelector.applyAsDouble(current);
                if (Double.compare(currentKey, maxKey) > 0) {
                    maxElement = current;
                    maxKey = currentKey;
                }
            }
            return Optional.of(maxElement);
        }
    }

    /**
     * Returns the minimum element according to its natural ordering.
     *
     * @return the minimum element
     * @throws ClassCastException if the elements are not mutually comparable
     */
    T min();

    /**
     * Returns the minimum element according to its natural ordering if present.
     *
     * @return an {@link Optional} containing the minimum element
     * @throws ClassCastException if the elements are not mutually comparable
     */
    Optional<T> minOptional();

    /**
     * Returns the maximum element according to its natural ordering.
     *
     * @return the maximum element
     * @throws ClassCastException if the elements are not mutually comparable
     */
    T max();

    /**
     * Returns the maximum element according to its natural ordering if present.
     *
     * @return an {@link Optional} containing the maximum element
     * @throws ClassCastException if the elements are not mutually comparable
     */
    Optional<T> maxOptional();
}
