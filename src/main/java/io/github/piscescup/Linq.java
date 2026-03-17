package io.github.piscescup;

import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.BinFunction;
import io.github.piscescup.primitive.DoubleEnumerable;
import io.github.piscescup.primitive.IntEnumerable;
import io.github.piscescup.primitive.LongEnumerable;
import io.github.piscescup.util.validation.NullCheck;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Default {@link Enumerable} implementation backed by repeated materialization.
 *
 * @param <T> the element type
 */
public class Linq<T> implements Enumerable<T> {
    private final Supplier<List<T>> materializer;

    public Linq(InternalEnumerable<? extends T> source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> materialize(source);
    }

    public Linq(Iterable<? extends T> source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> materialize(source);
    }

    private Linq(Supplier<List<T>> materializer) {
        this.materializer = materializer;
    }

    public static <T> Linq<T> from(InternalEnumerable<? extends T> source) {
        return new Linq<>(source);
    }

    public static <T> Linq<T> from(Iterable<? extends T> source) {
        return new Linq<>(source);
    }

    @SafeVarargs
    public static <T> Linq<T> of(T... elements) {
        NullCheck.requireNonNull(elements);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>(elements.length);
            for (T element : elements) {
                result.add(element);
            }
            return result;
        });
    }

    private static <T> Linq<T> fromList(List<? extends T> source) {
        return new Linq<>(() -> new ArrayList<>(source));
    }

    @Override
    public Enumerator<T> enumerator() {
        return new ListEnumerator<>(snapshot());
    }

    @Override
    public Linq<T> skip(long count) {
        return new Linq<>(() -> {
            List<T> source = snapshot();
            if (count <= 0) {
                return source;
            }
            if (count >= source.size()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(source.subList((int) count, source.size()));
        });
    }

    @Override
    public Linq<T> take(long count) {
        return new Linq<>(() -> {
            List<T> source = snapshot();
            if (count <= 0) {
                return new ArrayList<>();
            }
            if (count >= source.size()) {
                return source;
            }
            return new ArrayList<>(source.subList(0, (int) count));
        });
    }

    @Override
    public Linq<T> where(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            for (T element : snapshot()) {
                if (predicate.test(element)) {
                    result.add(element);
                }
            }
            return result;
        });
    }

    @Override
    public Linq<T> takeWhile(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            for (T element : snapshot()) {
                if (!predicate.test(element)) {
                    continue;
                }
                result.add(element);
            }
            return result;
        });
    }

    @Override
    public Linq<T> skipWhile(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> {
            List<T> source = snapshot();
            List<T> result = new ArrayList<>();
            for (T element : source) {
                if (predicate.test(element)) {
                    continue;
                }
                result.add(element);
            }
            return result;
        });
    }

    @Override
    public Linq<T> concat(Enumerable<? extends T> other) {
        NullCheck.requireNonNull(other);
        return new Linq<>(() -> {
            List<T> result = snapshot();
            result.addAll(materialize(other));
            return result;
        });
    }

    @Override
    public Linq<T> append(T element) {
        return new Linq<>(() -> {
            List<T> result = snapshot();
            result.add(element);
            return result;
        });
    }

    @Override
    public Linq<T> prepend(T element) {
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            result.add(element);
            result.addAll(snapshot());
            return result;
        });
    }

    @Override
    public <R> Linq<R> select(Function<? super T, ? extends R> selector) {
        NullCheck.requireNonNull(selector);
        return new Linq<>(() -> {
            List<R> result = new ArrayList<>();
            for (T element : snapshot()) {
                result.add(selector.apply(element));
            }
            return result;
        });
    }

    @Override
    public <R> Linq<R> selectMany(Function<? super T, ? extends Enumerable<? extends R>> selector) {
        NullCheck.requireNonNull(selector);
        return new Linq<>(() -> {
            List<R> result = new ArrayList<>();
            for (T element : snapshot()) {
                result.addAll(materialize(selector.apply(element)));
            }
            return result;
        });
    }

    @Override
    public <K> Enumerable<Groupable<K, T>> groupBy(Function<? super T, ? extends K> keySelector) {
        return groupBy(keySelector, Function.identity());
    }

    @Override
    public <K> Enumerable<Groupable<K, T>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        return groupBy(keySelector, Function.identity(), equalator);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector
    ) {
        return groupBy(keySelector, elementSelector, Linq::defaultEquals);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(equalator);
        return fromList(buildGroups(snapshot(), keySelector, elementSelector, equalator));
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super K, ? super Enumerable<T>, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, Function.identity(), resultSelector);
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super K, ? super Enumerable<T>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        return groupResultBy(keySelector, Function.identity(), resultSelector, equalator);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        BinFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, elementSelector, resultSelector, Linq::defaultEquals);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        BinFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);

        List<Grouping<K, E>> groups = buildGroups(snapshot(), keySelector, elementSelector, equalator);
        List<R> result = new ArrayList<>(groups.size());
        for (Grouping<K, E> group : groups) {
            result.add(resultSelector.apply(group.groupingKey(), fromList(group.elements())));
        }
        return fromList(result);
    }

    @Override
    public <K extends Comparable<? super K>> OrderedEnumerable<T> orderBy(
        Function<? super T, ? extends K> keySelector
    ) {
        NullCheck.requireNonNull(keySelector);
        return orderBy(keySelector, Comparator.naturalOrder());
    }

    @Override
    public <K> OrderedEnumerable<T> orderBy(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);
        return new OrderedLinq<>(this::snapshot, Comparator.comparing(keySelector, comparator));
    }

    @Override
    public OrderedEnumerable<T> orderByInt(ToIntFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, Comparator.comparingInt(keySelector));
    }

    @Override
    public OrderedEnumerable<T> orderByLong(ToLongFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, Comparator.comparingLong(keySelector));
    }

    @Override
    public OrderedEnumerable<T> orderByDouble(ToDoubleFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, Comparator.comparingDouble(keySelector));
    }

    @Override
    public <K extends Comparable<? super K>> OrderedEnumerable<T> orderByDescending(
        Function<? super T, ? extends K> keySelector
    ) {
        NullCheck.requireNonNull(keySelector);
        return orderByDescending(keySelector, Comparator.naturalOrder());
    }

    @Override
    public <K> OrderedEnumerable<T> orderByDescending(
        Function<? super T, ? extends K> keySelector,
        Comparator<? super K> comparator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(comparator);
        return new OrderedLinq<>(this::snapshot, (left, right) ->
            comparator.compare(keySelector.apply(right), keySelector.apply(left))
        );
    }

    @Override
    public OrderedEnumerable<T> orderByIntDescending(ToIntFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, (left, right) ->
            Integer.compare(keySelector.applyAsInt(right), keySelector.applyAsInt(left))
        );
    }

    @Override
    public OrderedEnumerable<T> orderByLongDescending(ToLongFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, (left, right) ->
            Long.compare(keySelector.applyAsLong(right), keySelector.applyAsLong(left))
        );
    }

    @Override
    public OrderedEnumerable<T> orderByDoubleDescending(ToDoubleFunction<? super T> keySelector) {
        NullCheck.requireNonNull(keySelector);
        return new OrderedLinq<>(this::snapshot, (left, right) ->
            Double.compare(keySelector.applyAsDouble(right), keySelector.applyAsDouble(left))
        );
    }

    @Override
    public IntEnumerable mapToInt(ToIntFunction<? super T> selector) {
        NullCheck.requireNonNull(selector);
        throw new UnsupportedOperationException("Primitive enumerable implementation has not been added yet.");
    }

    @Override
    public LongEnumerable mapToLong(ToLongFunction<? super T> selector) {
        NullCheck.requireNonNull(selector);
        throw new UnsupportedOperationException("Primitive enumerable implementation has not been added yet.");
    }

    @Override
    public DoubleEnumerable mapToDouble(ToDoubleFunction<? super T> selector) {
        NullCheck.requireNonNull(selector);
        throw new UnsupportedOperationException("Primitive enumerable implementation has not been added yet.");
    }

    @Override
    public Linq<T> union(Enumerable<? extends T> other) {
        return union(other, Linq::defaultEquals);
    }

    @Override
    public Linq<T> union(
        Enumerable<? extends T> other,
        Equalator<? super T> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            addDistinct(result, snapshot(), equalator);
            addDistinct(result, materialize(other), equalator);
            return result;
        });
    }

    @Override
    public <K> Linq<T> unionBy(
        Enumerable<? extends T> other,
        Function<? super T, ? extends K> keySelector
    ) {
        return unionBy(other, keySelector, Linq::defaultEquals);
    }

    @Override
    public <K> Linq<T> unionBy(
        Enumerable<? extends T> other,
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            List<K> seenKeys = new ArrayList<>();
            addDistinctBy(result, seenKeys, snapshot(), keySelector, equalator);
            addDistinctBy(result, seenKeys, materialize(other), keySelector, equalator);
            return result;
        });
    }

    @Override
    public Linq<T> intersect(Enumerable<? extends T> other) {
        return intersect(other, Linq::defaultEquals);
    }

    @Override
    public Linq<T> intersect(
        Enumerable<? extends T> other,
        Equalator<? super T> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<T> source = snapshot();
            List<? extends T> candidates = materialize(other);
            List<T> result = new ArrayList<>();
            for (T element : source) {
                if (containsBy(candidates, element, equalator) && !containsBy(result, element, equalator)) {
                    result.add(element);
                }
            }
            return result;
        });
    }

    @Override
    public <K> Linq<T> intersectBy(
        Enumerable<? extends K> other,
        Function<? super T, ? extends K> keySelector
    ) {
        return intersectBy(other, keySelector, Linq::defaultEquals);
    }

    @Override
    public <K> Linq<T> intersectBy(
        Enumerable<? extends K> other,
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            List<? extends K> keys = materialize(other);
            List<K> emittedKeys = new ArrayList<>();
            for (T element : snapshot()) {
                K key = keySelector.apply(element);
                if (containsBy(keys, key, equalator) && !containsBy(emittedKeys, key, equalator)) {
                    result.add(element);
                    emittedKeys.add(key);
                }
            }
            return result;
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> join(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return join(inner, outerKeySelector, innerKeySelector, resultSelector, Linq::defaultEquals);
    }

    @Override
    public <TInner, K, R> Enumerable<R> join(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<R> result = new ArrayList<>();
            List<TInner> innerItems = materialize(inner);
            for (T outerItem : snapshot()) {
                K outerKey = outerKeySelector.apply(outerItem);
                for (TInner innerItem : innerItems) {
                    if (equalator.equals(outerKey, innerKeySelector.apply(innerItem))) {
                        result.add(resultSelector.apply(outerItem, innerItem));
                    }
                }
            }
            return result;
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> leftJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector, Linq::defaultEquals);
    }

    @Override
    public <TInner, K, R> Enumerable<R> leftJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<R> result = new ArrayList<>();
            List<TInner> innerItems = materialize(inner);
            for (T outerItem : snapshot()) {
                K outerKey = outerKeySelector.apply(outerItem);
                boolean matched = false;
                for (TInner innerItem : innerItems) {
                    if (equalator.equals(outerKey, innerKeySelector.apply(innerItem))) {
                        matched = true;
                        result.add(resultSelector.apply(outerItem, innerItem));
                    }
                }
                if (!matched) {
                    result.add(resultSelector.apply(outerItem, null));
                }
            }
            return result;
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> rightJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector, Linq::defaultEquals);
    }

    @Override
    public <TInner, K, R> Enumerable<R> rightJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        return new Linq<>(() -> {
            List<R> result = new ArrayList<>();
            List<T> outerItems = snapshot();
            for (TInner innerItem : materialize(inner)) {
                K innerKey = innerKeySelector.apply(innerItem);
                boolean matched = false;
                for (T outerItem : outerItems) {
                    if (equalator.equals(outerKeySelector.apply(outerItem), innerKey)) {
                        matched = true;
                        result.add(resultSelector.apply(outerItem, innerItem));
                    }
                }
                if (!matched) {
                    result.add(resultSelector.apply(null, innerItem));
                }
            }
            return result;
        });
    }

    @Override
    public <TSecond, R> Enumerable<R> zip(
        Enumerable<? extends TSecond> second,
        BinFunction<? super T, ? super TSecond, ? extends R> resultSelector
    ) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(resultSelector);
        return new Linq<>(() -> {
            List<T> left = snapshot();
            List<TSecond> right = materialize(second);
            int size = Math.min(left.size(), right.size());
            List<R> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(resultSelector.apply(left.get(i), right.get(i)));
            }
            return result;
        });
    }

    @Override
    public <TSecond> Enumerable<BinEntry<T, TSecond>> zip(Enumerable<? extends TSecond> second) {
        return zip(second, BinEntry::new);
    }

    @Override
    public <TSecond, TThird> Enumerable<TriEntry<T, TSecond, TThird>> zip(
        Enumerable<? extends TSecond> second,
        Enumerable<? extends TThird> third
    ) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(third);
        return new Linq<>(() -> {
            List<T> firstItems = snapshot();
            List<TSecond> secondItems = materialize(second);
            List<TThird> thirdItems = materialize(third);
            int size = Math.min(firstItems.size(), Math.min(secondItems.size(), thirdItems.size()));
            List<TriEntry<T, TSecond, TThird>> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(new TriEntry<>(firstItems.get(i), secondItems.get(i), thirdItems.get(i)));
            }
            return result;
        });
    }

    @Override
    public List<T> toList() {
        return snapshot();
    }

    @Override
    public Enumerable<Enumerable<T>> chunk(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        List<Enumerable<T>> result = new ArrayList<>();
        for (List<T> chunk : chunkLists(size)) {
            result.add(fromList(chunk));
        }
        return fromList(result);
    }

    @Override
    public Enumerable<List<T>> chunkAsList(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        return fromList(chunkLists(size));
    }

    @Override
    public Object[] toArray() {
        return snapshot().toArray();
    }

    @Override
    public T[] toArray(java.util.function.IntFunction<T[]> generator) {
        NullCheck.requireNonNull(generator);
        return snapshot().toArray(generator.apply((int) count()));
    }

    @Override
    public <R> Enumerable<R> castTo(Class<R> clazz) {
        NullCheck.requireNonNull(clazz);

        List<R> result = new ArrayList<>();

        List<T> ts = this.materializer.get();
        for (T t : ts) {
            if  (!clazz.isInstance(t)) {
                throw new ClassCastException("Cannot cast element " + t + " to " + clazz.getName());
            }

            result.add(clazz.cast(t));
        }

        return new Linq<>(() -> result);
    }

    @Override
    public boolean any() {
        try (Enumerator<T> enumerator = enumerator()) {
            return enumerator.moveNext();
        }
    }

    @Override
    public boolean any(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        for (T element : snapshot()) {
            if (predicate.test(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean all(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        for (T element : snapshot()) {
            if (!predicate.test(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long count() {
        return snapshot().size();
    }

    @Override
    public long countBy(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        long count = 0;
        for (T element : snapshot()) {
            if (predicate.test(element)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Linq<T> distinct() {
        return new Linq<>(() -> {
            List<T> result = new ArrayList<>();
            addDistinct(result, snapshot(), Linq::defaultEquals);
            return result;
        });
    }

    @Override
    public boolean contains(T value) {
        return snapshot().contains(value);
    }

    @Override
    public T first() {
        return firstOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public T first(Predicate<? super T> predicate) {
        return firstOptional(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> firstOptional() {
        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }
            return Optional.ofNullable(enumerator.current());
        }
    }

    @Override
    public Optional<T> firstOptional(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        for (T element : snapshot()) {
            if (predicate.test(element)) {
                return Optional.ofNullable(element);
            }
        }
        return Optional.empty();
    }

    @Override
    public T single() {
        return singleOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public T single(Predicate<? super T> predicate) {
        return singleOptional(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> singleOptional() {
        List<T> source = snapshot();
        if (source.isEmpty()) {
            return Optional.empty();
        }
        if (source.size() > 1) {
            throw new IllegalStateException("Sequence contains more than one element.");
        }
        return Optional.ofNullable(source.getFirst());
    }

    @Override
    public Optional<T> singleOptional(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        T value = null;
        boolean found = false;
        for (T element : snapshot()) {
            if (!predicate.test(element)) {
                continue;
            }
            if (found) {
                throw new IllegalStateException("Sequence contains more than one matching element.");
            }
            value = element;
            found = true;
        }
        return found ? Optional.ofNullable(value) : Optional.empty();
    }

    @Override
    public <A> A aggregate(A seed, BinFunction<? super A, ? super T, ? extends A> aggregator) {
        NullCheck.requireNonNull(aggregator);
        A result = seed;
        for (T element : snapshot()) {
            result = aggregator.apply(result, element);
        }
        return result;
    }

    @Override
    public T min() {
        return minOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> minOptional() {
        List<T> source = snapshot();
        if (source.isEmpty()) {
            return Optional.empty();
        }
        T best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            T candidate = source.get(i);
            if (compareNatural(candidate, best) < 0) {
                best = candidate;
            }
        }
        return Optional.ofNullable(best);
    }

    @Override
    public T max() {
        return maxOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> maxOptional() {
        List<T> source = snapshot();
        if (source.isEmpty()) {
            return Optional.empty();
        }
        T best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            T candidate = source.get(i);
            if (compareNatural(candidate, best) > 0) {
                best = candidate;
            }
        }
        return Optional.ofNullable(best);
    }

    private List<T> snapshot() {
        return new ArrayList<>(materializer.get());
    }

    private static <T> List<T> materialize(Iterable<? extends T> source) {
        List<T> result = new ArrayList<>();
        for (T element : source) {
            result.add(element);
        }
        return result;
    }

    private static <T> boolean defaultEquals(T left, T right) {
        return Objects.equals(left, right);
    }

    @SuppressWarnings("unchecked")
    private static <T> int compareNatural(T left, T right) {
        return ((Comparable<? super T>) left).compareTo(right);
    }

    private List<List<T>> chunkLists(int size) {
        List<T> source = snapshot();
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i += size) {
            result.add(new ArrayList<>(source.subList(i, Math.min(i + size, source.size()))));
        }
        return result;
    }

    private static <T> void addDistinct(
        List<T> target,
        List<? extends T> candidates,
        Equalator<? super T> equalator
    ) {
        for (T candidate : candidates) {
            if (!containsBy(target, candidate, equalator)) {
                target.add(candidate);
            }
        }
    }

    private static <T, K> void addDistinctBy(
        List<T> target,
        List<K> seenKeys,
        List<? extends T> candidates,
        Function<? super T, ? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        for (T candidate : candidates) {
            K key = keySelector.apply(candidate);
            if (!containsBy(seenKeys, key, equalator)) {
                seenKeys.add(key);
                target.add(candidate);
            }
        }
    }

    private static <T> boolean containsBy(
        List<? extends T> source,
        T target,
        Equalator<? super T> equalator
    ) {
        for (T element : source) {
            if (equalator.equals(element, target)) {
                return true;
            }
        }
        return false;
    }

    private static <T, K, E> List<Grouping<K, E>> buildGroups(
        List<T> source,
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        List<Grouping<K, E>> groups = new ArrayList<>();
        for (T element : source) {
            K key = keySelector.apply(element);
            Grouping<K, E> group = findGroup(groups, key, equalator);
            if (group == null) {
                group = new Grouping<>(key, new ArrayList<>());
                groups.add(group);
            }
            group.elements().add(elementSelector.apply(element));
        }
        return groups;
    }

    private static <K, E> Grouping<K, E> findGroup(
        List<Grouping<K, E>> groups,
        K key,
        Equalator<? super K> equalator
    ) {
        for (Grouping<K, E> group : groups) {
            if (equalator.equals(group.groupingKey(), key)) {
                return group;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Collection<T> instantiateCollection(Class<R> clazz) {
        if (clazz.isInterface()) {
            if (clazz == List.class || clazz == Collection.class) {
                return new ArrayList<>();
            }
            if (clazz == Set.class) {
                return new LinkedHashSet<>();
            }
        }
        try {
            return (Collection<T>) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new UnsupportedOperationException("Unsupported extract target: " + clazz.getName(), ex);
        }
    }

    private static final class ListEnumerator<T> implements Enumerator<T> {
        private final List<T> elements;
        private int index = -1;
        private boolean prepared;

        private ListEnumerator(List<T> elements) {
            this.elements = elements;
        }

        @Override
        public boolean moveNext() {
            if (index + 1 >= elements.size()) {
                index = elements.size();
                prepared = false;
                return false;
            }
            index++;
            prepared = true;
            return true;
        }

        @Override
        public T current() {
            if (!prepared || index < 0 || index >= elements.size()) {
                throw new IllegalStateException("Enumerator is not positioned on an element.");
            }
            return elements.get(index);
        }

        @Override
        public boolean hasNext() {
            return index + 1 < elements.size();
        }

        @Override
        public T next() {
            if (!moveNext()) {
                throw new NoSuchElementException();
            }
            return current();
        }

        @Override
        public void forEachRemaining(java.util.function.Consumer<? super T> action) {
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
            index = -1;
            prepared = false;
        }

        @Override
        public void close() {
        }
    }

    private static final class Grouping<K, E> implements Groupable<K, E> {
        private final K key;
        private List<E> elements;

        private Grouping(K key, List<E> elements) {
            this.key = key;
            this.elements = elements;
        }

        @Override
        public K groupingKey() {
            return key;
        }

        @Override
        public List<E> elements() {
            return elements;
        }

        @Override
        public List<E> setValue(List<E> value) {
            List<E> previous = elements;
            elements = value;
            return previous;
        }
    }

    private static final class OrderedLinq<T> extends Linq<T> implements OrderedEnumerable<T> {
        private final Supplier<List<T>> sourceSupplier;
        private final Comparator<T> comparator;

        private OrderedLinq(Supplier<List<T>> sourceSupplier, Comparator<T> comparator) {
            super(() -> {
                List<T> items = new ArrayList<>(sourceSupplier.get());
                items.sort(comparator);
                return items;
            });
            this.sourceSupplier = sourceSupplier;
            this.comparator = comparator;
        }

        @Override
        public <K extends Comparable<? super K>> OrderedEnumerable<T> thenOrderBy(
            Function<? super T, ? extends K> keySelector
        ) {
            NullCheck.requireNonNull(keySelector);
            return thenOrderBy(keySelector, Comparator.naturalOrder());
        }

        @Override
        public <K> OrderedEnumerable<T> thenOrderBy(
            Function<? super T, ? extends K> keySelector,
            Comparator<? super K> nextComparator
        ) {
            NullCheck.requireNonNull(keySelector);
            NullCheck.requireNonNull(nextComparator);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return nextComparator.compare(keySelector.apply(left), keySelector.apply(right));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderByInt(ToIntFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Integer.compare(keySelector.applyAsInt(left), keySelector.applyAsInt(right));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderByLong(ToLongFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Long.compare(keySelector.applyAsLong(left), keySelector.applyAsLong(right));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderByDouble(ToDoubleFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Double.compare(keySelector.applyAsDouble(left), keySelector.applyAsDouble(right));
            });
        }

        @Override
        public <K extends Comparable<? super K>> OrderedEnumerable<T> thenOrderByDescending(
            Function<? super T, ? extends K> keySelector
        ) {
            NullCheck.requireNonNull(keySelector);
            return thenOrderByDescending(keySelector, Comparator.naturalOrder());
        }

        @Override
        public <K> OrderedEnumerable<T> thenOrderByDescending(
            Function<? super T, ? extends K> keySelector,
            Comparator<? super K> nextComparator
        ) {
            NullCheck.requireNonNull(keySelector);
            NullCheck.requireNonNull(nextComparator);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return nextComparator.compare(keySelector.apply(right), keySelector.apply(left));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByInt(ToIntFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Integer.compare(keySelector.applyAsInt(right), keySelector.applyAsInt(left));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByLong(ToLongFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Long.compare(keySelector.applyAsLong(right), keySelector.applyAsLong(left));
            });
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByDouble(ToDoubleFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceSupplier, (left, right) -> {
                int current = comparator.compare(left, right);
                if (current != 0) {
                    return current;
                }
                return Double.compare(keySelector.applyAsDouble(right), keySelector.applyAsDouble(left));
            });
        }
    }
}
