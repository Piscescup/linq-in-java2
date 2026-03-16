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
 * <p>This is the object-sequence counterpart of Java's {@code Stream<T>}.
 *
 * @param <T> the element type
 *
 * @author REN YuanTong
 * @since 1.1.0
 */
public interface Enumerable<T> extends BaseEnumerable<T, Enumerable<T>> {
    Enumerable<T> where(Predicate<? super T> predicate);

    Enumerable<T> concat(InternalEnumerable<? extends T> other);

    Enumerable<T> append(T element);

    Enumerable<T> prepend(T element);

    <R> Enumerable<R> select(Function<? super T, ? extends R> selector);

    <R> Enumerable<R> selectMany(
        Function<? super T, ? extends Enumerable<? extends R>> selector
    );

    IntEnumerable mapToInt(ToIntFunction<? super T> selector);

    LongEnumerable mapToLong(ToLongFunction<? super T> selector);

    DoubleEnumerable mapToDouble(ToDoubleFunction<? super T> selector);

    List<T> toList();

    Object[] toArray();

    T[] toArray(IntFunction<T[]> generator);

    boolean any(Predicate<? super T> predicate);

    boolean all(Predicate<? super T> predicate);

    long count(Predicate<? super T> predicate);

    boolean contains(T value);

    T first();

    T first(Predicate<? super T> predicate);

    Optional<T> firstOptional();

    Optional<T> firstOptional(Predicate<? super T> predicate);

    T single();

    T single(Predicate<? super T> predicate);

    Optional<T> singleOptional();

    Optional<T> singleOptional(Predicate<? super T> predicate);

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
