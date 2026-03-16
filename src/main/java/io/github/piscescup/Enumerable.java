package io.github.piscescup;

import io.github.piscescup.primitive.DoubleEnumerable;
import io.github.piscescup.primitive.IntEnumerable;
import io.github.piscescup.primitive.LongEnumerable;

import java.util.List;
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
     * Filters elements by the supplied predicate.
     *
     * @param predicate the condition used to decide whether an element is kept
     * @return an {@code Enumerable<T>} containing elements that satisfy the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    Enumerable<T> where(Predicate<? super T> predicate);

    /**
     * Concatenates this sequence with another sequence of compatible elements.
     *
     * @param other the sequence appended after this sequence
     * @return an {@code Enumerable<T>} containing all elements from both sequences in order
     * @throws NullPointerException if {@code other} is {@code null}
     */
    Enumerable<T> concat(InternalEnumerable<? extends T> other);

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
    long count(Predicate<? super T> predicate);

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
    <A> A aggregate(A seed, BiFunction<? super A, ? super T, ? extends A> aggregator);

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
