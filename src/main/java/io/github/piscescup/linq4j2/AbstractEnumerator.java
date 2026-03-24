package io.github.piscescup.linq4j2;

import io.github.piscescup.util.validation.NullCheck;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Base {@link Enumerator} implementation with cached {@link #hasNext()} support.
 *
 * @param <T> the element type
 */
public abstract class AbstractEnumerator<T> implements Enumerator<T> {
    private static final byte NEEDS_COMPUTE = 0;
    private static final byte PREFETCHED = 1;
    private static final byte ON_CURRENT = 2;
    private static final byte FINISHED = 3;

    private T current;
    private byte state = NEEDS_COMPUTE;

    /**
     * Computes the next element.
     *
     * @return {@code true} when an element was prepared, {@code false} when the sequence is exhausted
     */
    protected abstract boolean computeNext();

    protected final boolean yieldValue(T element) {
        current = element;
        state = PREFETCHED;
        return true;
    }

    protected final boolean end() {
        current = null;
        state = FINISHED;
        return false;
    }

    protected final void resetState() {
        current = null;
        state = NEEDS_COMPUTE;
    }

    @Override
    public boolean moveNext() {
        if (state == FINISHED) {
            return false;
        }
        if (state == PREFETCHED) {
            state = ON_CURRENT;
            return true;
        }
        if (!computeNext()) {
            return false;
        }
        state = ON_CURRENT;
        return true;
    }

    @Override
    public T current() {
        if (state != ON_CURRENT) {
            throw new IllegalStateException("Enumerator is not positioned on an element.");
        }
        return current;
    }

    @Override
    public boolean hasNext() {
        if (state == FINISHED) {
            return false;
        }
        return state == PREFETCHED || computeNext();
    }

    @Override
    public T next() {
        if (!moveNext()) {
            throw new NoSuchElementException();
        }
        return current;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        NullCheck.requireNonNull(action);
        while (moveNext()) {
            action.accept(current);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }
}
