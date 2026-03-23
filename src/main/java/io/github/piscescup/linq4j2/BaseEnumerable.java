package io.github.piscescup.linq4j2;

import java.util.List;

/**
 * Represents the shared abstraction of all LINQ-style sequences.
 *
 * <p>This interface is intentionally close in spirit to Java's {@code BaseStream}:
 * it only contains operations that make sense for every sequence regardless of
 * whether the elements are reference values or primitive-specialized values.
 *
 * <p>The second generic parameter {@code SUB_BE} follows the
 * <em>Curiously Recurring Template Pattern (CRTP)</em>. It allows concrete
 * implementations to preserve their own type for operations that keep the same
 * element type, such as {@code where}, {@code skip}, and {@code concat}.
 *
 * @param <T> the type of elements produced by this sequence
 * @param <SUB_BE> the concrete subtype of {@code BaseEnumerable} used for
 *                 fluent method chaining
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public interface BaseEnumerable<T, SUB_BE extends BaseEnumerable<T, SUB_BE>>
    extends InternalEnumerable<T> {

    /**
     * Skips the first {@code count} elements.
     *
     * @param count the number of elements to skip
     * @return the sliced sequence
     */
    SUB_BE skip(long count);

    /**
     * Takes the first {@code count} elements.
     *
     * @param count the number of elements to take
     * @return the sliced sequence
     */
    SUB_BE take(long count);

    /**
     * Removes duplicate elements according to {@link Object#equals(Object)}.
     *
     * @return a sequence containing distinct elements
     */
    SUB_BE distinct();

    /**
     * Determines whether the sequence contains any element.
     *
     * @return {@code true} if the sequence is non-empty
     */
    boolean any();

    /**
     * Counts the number of elements in the sequence.
     *
     * @return the element count
     */
    long count();

    /**
     * Collects the elements of the sequence into a {@link List}.
     * @return a {@link List} contains the elements of the sequence
     */
    List<T> toList();

}
