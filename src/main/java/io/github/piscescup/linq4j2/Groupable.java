package io.github.piscescup.linq4j2;

import java.util.List;
import java.util.Map;

/**
 * Represents a grouping of elements that share a common key.
 *
 * <p>
 * This interface models the result of a grouping operation, similar to
 * LINQ's {@code IGrouping<K, E>}. Each {@code Groupable} consists of:
 * <ul>
 *     <li>a key of type {@code K}</li>
 *     <li>a collection of elements of type {@code E} associated with that key</li>
 * </ul>
 *
 * <p>
 * This interface extends {@link Map.Entry}, where:
 * <ul>
 *     <li>{@link #groupingKey()} corresponds to the grouping key</li>
 *     <li>{@link #getValue()} corresponds to the grouped elements</li>
 * </ul>
 *
 * <p>
 * Unlike a typical {@link Map.Entry}, this interface provides a more domain-specific
 * abstraction for grouped data, exposing {@link #elements()} for clarity.
 *
 * <p>
 * Implementations may or may not support mutation via {@link #setValue(List)}.
 *
 * <h3>Example</h3>
 * <pre>{@code
 * Enumerable<Groupable<String, Integer>> groups =
 *     numbers.groupBy(n -> n % 2 == 0 ? "even" : "odd");
 *
 * for (Groupable<String, Integer> group : groups) {
 *     System.out.println(group.getKey() + ": " + group.getElements());
 * }
 * }</pre>
 *
 * @param <K> the type of the grouping key
 * @param <E> the type of elements in each group
 *
 * @author REN YuanTong
 * @since 1.1.0
 */
public interface Groupable<K, E> extends Map.Entry<K, List<E>> {

    /**
     * Returns the key of this grouping.
     *
     * @return the grouping key
     */
    K groupingKey();

    /**
     * Returns the elements associated with the grouping key.
     *
     * @return the grouped elements
     */
    List<E> elements();

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key corresponding to this entry
     * @throws IllegalStateException implementations may, but are not
     *                               required to, throw this exception if the entry has been
     *                               removed from the backing map.
     */
    @Override
    default K getKey() {
        return groupingKey();
    }

    /**
     * Returns the value corresponding to this entry.  If the mapping
     * has been removed from the backing map (by the iterator's
     * {@code remove} operation), the results of this call are undefined.
     *
     * @return the value corresponding to this entry
     * @throws IllegalStateException implementations may, but are not
     *                               required to, throw this exception if the entry has been
     *                               removed from the backing map.
     */
    @Override
    default List<E> getValue() {
        return elements();
    }

    /**
     * Replaces the value corresponding to this entry with the specified
     * value (optional operation).  (Writes through to the map.)  The
     * behavior of this call is undefined if the mapping has already been
     * removed from the map (by the iterator's {@code remove} operation).
     *
     * @param value new value to be stored in this entry
     * @return old value corresponding to the entry
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by the backing map
     * @throws ClassCastException            if the class of the specified value
     *                                       prevents it from being stored in the backing map
     * @throws NullPointerException          if the backing map does not permit
     *                                       null values, and the specified value is null
     * @throws IllegalArgumentException      if some property of this value
     *                                       prevents it from being stored in the backing map
     * @throws IllegalStateException         implementations may, but are not
     *                                       required to, throw this exception if the entry has been
     *                                       removed from the backing map.
     */
    @Override
    List<E> setValue(List<E> value);

    /**
     * Compares the specified object with this entry for equality.
     * Returns {@code true} if the given object is also a map entry and
     * the two entries represent the same mapping.  More formally, two
     * entries {@code e1} and {@code e2} represent the same mapping
     * if<pre>
     *     (e1.getKey()==null ?
     *      e2.getKey()==null : e1.getKey().equals(e2.getKey()))  &amp;&amp;
     *     (e1.getValue()==null ?
     *      e2.getValue()==null : e1.getValue().equals(e2.getValue()))
     * </pre>
     * This ensures that the {@code equals} method works properly across
     * different implementations of the {@code Map.Entry} interface.
     *
     * @param o object to be compared for equality with this map entry
     * @return {@code true} if the specified object is equal to this map
     * entry
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the hash code value for this map entry.  The hash code
     * of a map entry {@code e} is defined to be: <pre>
     *     (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
     *     (e.getValue()==null ? 0 : e.getValue().hashCode())
     * </pre>
     * This ensures that {@code e1.equals(e2)} implies that
     * {@code e1.hashCode()==e2.hashCode()} for any two Entries
     * {@code e1} and {@code e2}, as required by the general
     * contract of {@code Object.hashCode}.
     *
     * @return the hash code value for this map entry
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
    int hashCode();
}
