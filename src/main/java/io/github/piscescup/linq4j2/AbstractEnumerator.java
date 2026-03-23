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
    private T current;
    private boolean hasCurrent;
    private boolean ready;
    private boolean finished;

    /**
     * Computes the next element.
     *
     * @return {@code true} when an element was prepared, {@code false} when the sequence is exhausted
     */
    protected abstract boolean computeNext();

    protected final boolean yieldValue(T element) {
        current = element;
        hasCurrent = true;
        ready = true;
        return true;
    }

    protected final boolean end() {
        current = null;
        hasCurrent = false;
        ready = false;
        finished = true;
        return false;
    }

    @Override
    public boolean moveNext() {
        if (finished) {
            return false;
        }
        if (ready) {
            ready = false;
            return true;
        }
        if (!computeNext()) {
            return false;
        }
        ready = false;
        return true;
    }

    @Override
    public T current() {
        if (!hasCurrent) {
            throw new IllegalStateException("Enumerator is not positioned on an element.");
        }
        return current;
    }

    @Override
    public boolean hasNext() {
        if (finished) {
            return false;
        }
        return ready || computeNext();
    }

    @Override
    public T next() {
        if (!moveNext()) {
            throw new NoSuchElementException();
        }
        return current();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        NullCheck.requireNonNull(action);
        while (moveNext()) {
            action.accept(current());
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
