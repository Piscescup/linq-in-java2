package io.github.piscescup.linq4j2;

import java.util.List;

/**
 * A read-only group.
 *
 * @param <K> the type of the grouping key
 * @param <E> the type of elements in the group
 *
 * @author REN YuanTong
 * @since 1.0.0
 */
public final class ReadOnlyGroup<K, E> extends AbstractGroup<K, E> {

    public ReadOnlyGroup(K key, List<E> value) {
        super(key, value);
    }

    /**
     * Unsupported Operation!
     * <p>
     *     Always throw UnsupportedOperationException.
     * </p>
     */
    @Override
    public List<E> setValue(List<E> value) {
        throw new UnsupportedOperationException();
    }
}
