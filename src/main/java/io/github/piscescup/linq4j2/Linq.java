package io.github.piscescup.linq4j2;

import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.BinFunction;
import io.github.piscescup.linq4j2.primitive.DoubleLinq;
import io.github.piscescup.linq4j2.primitive.DoubleEnumerable;
import io.github.piscescup.linq4j2.primitive.IntLinq;
import io.github.piscescup.linq4j2.primitive.IntEnumerable;
import io.github.piscescup.linq4j2.primitive.LongLinq;
import io.github.piscescup.linq4j2.primitive.LongEnumerable;
import io.github.piscescup.util.validation.NullCheck;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Default {@link Enumerable} implementation backed by an enumerator factory.
 *
 * @param <T> the element type
 */
public class Linq<T> implements Enumerable<T> {
    private final Supplier<? extends Enumerator<T>> factory;
    private final Supplier<List<T>> listFactory;

    public Linq(InternalEnumerable<? extends T> source) {
        NullCheck.requireNonNull(source);
        this.factory = () -> uncheckedCast(source.enumerator());
        this.listFactory = () -> materialize(factory);
    }

    public Linq(Iterable<? extends T> source) {
        NullCheck.requireNonNull(source);
        if (source instanceof List<? extends T> list) {
            this.factory = () -> new ListEnumerator<>(uncheckedListCopy(list));
            this.listFactory = () -> uncheckedListCopy(list);
            return;
        }
        this.factory = () -> enumeratorOf(source.iterator());
        if (source instanceof Collection<? extends T> collection) {
            this.listFactory = () -> new ArrayList<>(collection);
        } else {
            this.listFactory = () -> materialize(factory);
        }
    }

    private Linq(Supplier<? extends Enumerator<T>> factory) {
        this(factory, () -> materialize(factory));
    }

    private Linq(Supplier<? extends Enumerator<T>> factory, Supplier<List<T>> listFactory) {
        this.factory = factory;
        this.listFactory = listFactory;
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> Enumerable<T> fromEnumerable(InternalEnumerable<? extends T> source) {
        return new Linq<>(source);
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> Enumerable<T> fromIterable(Iterable<? extends T> source) {
        return new Linq<>(source);
    }

    @NotNull
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> Enumerable<T> of(T... elements) {
        NullCheck.requireNonNull(elements);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private int index;

            @Override
            protected boolean computeNext() {
                if (index >= elements.length) {
                    return end();
                }
                return yieldValue(elements[index++]);
            }

            @Override
            public void reset() {
                index = 0;
                resetState();
            }
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static IntEnumerable ofInts(int... ints) {
        return new IntLinq(ints);
    }

    @NotNull
    @Contract("_ -> new")
    public static LongEnumerable ofLongs(long... longs) {
        return new LongLinq(longs);
    }

    @NotNull
    @Contract("_ -> new")
    public static DoubleEnumerable ofDoubles(double... doubles) {
        return new DoubleLinq(doubles);
    }

    @NotNull
    @Contract("_ -> new")
    public static IntEnumerable ofInts(Iterable<Integer> ints) {
        return new IntLinq(ints);
    }

    @NotNull
    @Contract("_ -> new")
    public static LongEnumerable ofLongs(Iterable<Long> longs) {
        return new LongLinq(longs);
    }

    @NotNull
    @Contract("_ -> new")
    public static DoubleEnumerable ofDoubles(Iterable<Double> doubles) {
        return new DoubleLinq(doubles);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static <T> Enumerable<T> fromList(List<? extends T> source) {
        List<T> copy = new ArrayList<>(source);
        return new Linq<>(() -> new ListEnumerator<>(copy), () -> new ArrayList<>(copy));
    }

    @Override
    public Enumerator<T> enumerator() {
        return factory.get();
    }

    @Override
    public Linq<T> skip(long count) {
        if (count <= 0) {
            return new Linq<>(factory);
        }
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private long remaining = count;
            private boolean skipped;

            @Override
            protected boolean computeNext() {
                skipIfNeeded();
                if (!source.moveNext()) {
                    return end();
                }
                return yieldValue(source.current());
            }

            private void skipIfNeeded() {
                if (skipped) {
                    return;
                }
                while (remaining > 0 && source.moveNext()) {
                    remaining--;
                }
                skipped = true;
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> take(long count) {
        if (count <= 0) {
            return new Linq<>(() -> Linq.<T>emptyEnumerator());
        }
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private long remaining = count;

            @Override
            protected boolean computeNext() {
                if (remaining <= 0 || !source.moveNext()) {
                    return end();
                }
                remaining--;
                return yieldValue(source.current());
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> where(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();

            @Override
            protected boolean computeNext() {
                while (source.moveNext()) {
                    T element = source.current();
                    if (predicate.test(element)) {
                        return yieldValue(element);
                    }
                }
                return end();
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> takeWhile(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private boolean stopped;

            @Override
            protected boolean computeNext() {
                if (stopped || !source.moveNext()) {
                    return end();
                }
                T element = source.current();
                if (!predicate.test(element)) {
                    stopped = true;
                    return end();
                }
                return yieldValue(element);
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> skipWhile(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private boolean skipping = true;

            @Override
            protected boolean computeNext() {
                while (source.moveNext()) {
                    T element = source.current();
                    if (skipping && predicate.test(element)) {
                        continue;
                    }
                    skipping = false;
                    return yieldValue(element);
                }
                return end();
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> concat(Enumerable<? extends T> other) {
        NullCheck.requireNonNull(other);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> first = Linq.this.enumerator();
            private final Enumerator<? extends T> second = other.enumerator();
            private boolean usingFirst = true;

            @Override
            protected boolean computeNext() {
                if (usingFirst) {
                    if (first.moveNext()) {
                        return yieldValue(first.current());
                    }
                    usingFirst = false;
                }
                if (!second.moveNext()) {
                    return end();
                }
                return yieldValue(second.current());
            }

            @Override
            public void close() {
                first.close();
                second.close();
            }
        });
    }

    @Override
    public Linq<T> append(T element) {
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private boolean appended;

            @Override
            protected boolean computeNext() {
                if (source.moveNext()) {
                    return yieldValue(source.current());
                }
                if (appended) {
                    return end();
                }
                appended = true;
                return yieldValue(element);
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public Linq<T> prepend(T element) {
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private boolean emittedPrepended;

            @Override
            protected boolean computeNext() {
                if (!emittedPrepended) {
                    emittedPrepended = true;
                    return yieldValue(element);
                }
                if (!source.moveNext()) {
                    return end();
                }
                return yieldValue(source.current());
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public <R> Linq<R> select(Function<? super T, ? extends R> selector) {
        NullCheck.requireNonNull(selector);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();

            @Override
            protected boolean computeNext() {
                if (!source.moveNext()) {
                    return end();
                }
                return yieldValue(selector.apply(source.current()));
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public <R> Linq<R> selectMany(Function<? super T, ? extends Enumerable<? extends R>> selector) {
        NullCheck.requireNonNull(selector);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> outer = Linq.this.enumerator();
            private Enumerator<? extends R> inner;

            @Override
            protected boolean computeNext() {
                while (true) {
                    if (inner != null && inner.moveNext()) {
                        return yieldValue(inner.current());
                    }
                    closeInner();
                    if (!outer.moveNext()) {
                        return end();
                    }
                    inner = selector.apply(outer.current()).enumerator();
                }
            }

            @Override
            public void close() {
                closeInner();
                outer.close();
            }

            private void closeInner() {
                if (inner != null) {
                    inner.close();
                    inner = null;
                }
            }
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
        return groupBy(keySelector, elementSelector, Equalator.defaultEqualator());
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
        return groupResultBy(keySelector, elementSelector, resultSelector, Equalator.defaultEqualator());
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

        List<ReadOnlyGroup<K, E>> groups = buildGroups(snapshot(), keySelector, elementSelector, equalator);
        List<R> result = new ArrayList<>(groups.size());
        for (ReadOnlyGroup<K, E> group : groups) {
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
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            for (T element : snapshot()) {
                result.add(selector.applyAsInt(element));
            }
            return result;
        });
    }

    @Override
    public LongEnumerable mapToLong(ToLongFunction<? super T> selector) {
        NullCheck.requireNonNull(selector);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            for (T element : snapshot()) {
                result.add(selector.applyAsLong(element));
            }
            return result;
        });
    }

    @Override
    public DoubleEnumerable mapToDouble(ToDoubleFunction<? super T> selector) {
        NullCheck.requireNonNull(selector);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            for (T element : snapshot()) {
                result.add(selector.applyAsDouble(element));
            }
            return result;
        });
    }

    @Override
    public Linq<T> union(Enumerable<? extends T> other) {
        return union(other, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <K> Linq<T> unionBy(
        Enumerable<? extends T> other,
        Function<? super T, ? extends K> keySelector
    ) {
        return unionBy(other, keySelector, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public Linq<T> intersect(Enumerable<? extends T> other) {
        return intersect(other, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <K> Linq<T> intersectBy(
        Enumerable<? extends K> other,
        Function<? super T, ? extends K> keySelector
    ) {
        return intersectBy(other, keySelector, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> join(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return join(inner, outerKeySelector, innerKeySelector, resultSelector, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> leftJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <TInner, K, R> Enumerable<R> rightJoin(
        Enumerable<? extends TInner> inner,
        Function<? super T, ? extends K> outerKeySelector,
        Function<? super TInner, ? extends K> innerKeySelector,
        BinFunction<? super T, ? super TInner, ? extends R> resultSelector
    ) {
        return rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector, Equalator.defaultEqualator());
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
            return new ListEnumerator<>(result);
        });
    }

    @Override
    public <TSecond, R> Enumerable<R> zip(
        Enumerable<? extends TSecond> second,
        BinFunction<? super T, ? super TSecond, ? extends R> resultSelector
    ) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(resultSelector);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> first = Linq.this.enumerator();
            private final Enumerator<? extends TSecond> otherEnumerator = second.enumerator();

            @Override
            protected boolean computeNext() {
                if (!first.moveNext() || !otherEnumerator.moveNext()) {
                    return end();
                }
                return yieldValue(resultSelector.apply(first.current(), otherEnumerator.current()));
            }

            @Override
            public void close() {
                first.close();
                otherEnumerator.close();
            }
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
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> first = Linq.this.enumerator();
            private final Enumerator<? extends TSecond> secondEnumerator = second.enumerator();
            private final Enumerator<? extends TThird> thirdEnumerator = third.enumerator();

            @Override
            protected boolean computeNext() {
                if (!first.moveNext() || !secondEnumerator.moveNext() || !thirdEnumerator.moveNext()) {
                    return end();
                }
                return yieldValue(new TriEntry<>(first.current(), secondEnumerator.current(), thirdEnumerator.current()));
            }

            @Override
            public void close() {
                first.close();
                secondEnumerator.close();
                thirdEnumerator.close();
            }
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
        List<T> source = snapshot();
        return source.toArray(generator.apply(source.size()));
    }

    @Override
    public <R> Enumerable<R> castTo(Class<R> clazz) {
        NullCheck.requireNonNull(clazz);
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();

            @Override
            protected boolean computeNext() {
                if (!source.moveNext()) {
                    return end();
                }
                T element = source.current();
                if (!clazz.isInstance(element)) {
                    throw new ClassCastException("Cannot cast element " + element + " to " + clazz.getName());
                }
                return yieldValue(clazz.cast(element));
            }

            @Override
            public void close() {
                source.close();
            }
        });
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
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                if (predicate.test(enumerator.current())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean all(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                if (!predicate.test(enumerator.current())) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public long count() {
        long count = 0;
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public long countBy(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        long count = 0;
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                if (predicate.test(enumerator.current())) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Linq<T> distinct() {
        return new Linq<>(() -> new AbstractEnumerator<>() {
            private final Enumerator<T> source = Linq.this.enumerator();
            private final Set<T> seen = new HashSet<>();

            @Override
            protected boolean computeNext() {
                while (source.moveNext()) {
                    T element = source.current();
                    if (seen.add(element)) {
                        return yieldValue(element);
                    }
                }
                return end();
            }

            @Override
            public void close() {
                source.close();
            }
        });
    }

    @Override
    public boolean contains(T value) {
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                if (Objects.equals(enumerator.current(), value)) {
                    return true;
                }
            }
            return false;
        }
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
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                T element = enumerator.current();
                if (predicate.test(element)) {
                    return Optional.ofNullable(element);
                }
            }
            return Optional.empty();
        }
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
        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }
            T value = enumerator.current();
            if (enumerator.moveNext()) {
                throw new IllegalStateException("Sequence contains more than one element.");
            }
            return Optional.ofNullable(value);
        }
    }

    @Override
    public Optional<T> singleOptional(Predicate<? super T> predicate) {
        NullCheck.requireNonNull(predicate);
        T value = null;
        boolean found = false;
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                T element = enumerator.current();
                if (!predicate.test(element)) {
                    continue;
                }
                if (found) {
                    throw new IllegalStateException("Sequence contains more than one matching element.");
                }
                value = element;
                found = true;
            }
        }
        return found ? Optional.ofNullable(value) : Optional.empty();
    }

    @Override
    public <A> A aggregate(A seed, BinFunction<? super A, ? super T, ? extends A> aggregator) {
        NullCheck.requireNonNull(aggregator);
        A result = seed;
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                result = aggregator.apply(result, enumerator.current());
            }
        }
        return result;
    }

    @Override
    public <A, R> R aggregate(
        A seed,
        BinFunction<? super A, ? super T, ? extends A> aggregator,
        Function<? super A, ? extends R> resultSelector
    ) {
        NullCheck.requireNonNull(resultSelector);
        return resultSelector.apply(aggregate(seed, aggregator));
    }

    @Override
    public T aggregate(BinFunction<? super T, ? super T, ? extends T> aggregator) {
        NullCheck.requireNonNull(aggregator);
        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                throw new NoSuchElementException();
            }

            T result = enumerator.current();
            while (enumerator.moveNext()) {
                result = aggregator.apply(result, enumerator.current());
            }
            return result;
        }
    }

    @Override
    public <K, A> Enumerable<Groupable<K, A>> aggregateBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super K, ? extends A> seedSelector,
        BinFunction<? super A, ? super T, ? extends A> aggregator
    ) {
        return aggregateByCore(keySelector, seedSelector, aggregator, Equalator.defaultEqualator());
    }

    @Override
    public <K, A> Enumerable<Groupable<K, A>> aggregateBy(
        Function<? super T, ? extends K> keySelector,
        Function<? super K, ? extends A> seedSelector,
        BinFunction<? super A, ? super T, ? extends A> aggregator,
        Equalator<? super K> equalator
    ) {
        return aggregateByCore(keySelector, seedSelector, aggregator, equalator);
    }

    @Override
    public <K, A> Enumerable<Groupable<K, A>> aggregateBySeed(
        A seed,
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super A, ? super T, ? extends A> aggregator
    ) {
        return aggregateByCore(keySelector, ignored -> seed, aggregator, Equalator.defaultEqualator());
    }

    @Override
    public <K, A> Enumerable<Groupable<K, A>> aggregateBySeed(
        A seed,
        Function<? super T, ? extends K> keySelector,
        BinFunction<? super A, ? super T, ? extends A> aggregator,
        Equalator<? super K> equalator
    ) {
        return aggregateByCore(keySelector, ignored -> seed, aggregator, equalator);
    }

    @Override
    public T min() {
        return minOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> minOptional() {
        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }
            T best = enumerator.current();
            while (enumerator.moveNext()) {
                T candidate = enumerator.current();
                if (compareNatural(candidate, best) < 0) {
                    best = candidate;
                }
            }
            return Optional.ofNullable(best);
        }
    }

    @Override
    public T max() {
        return maxOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> maxOptional() {
        try (Enumerator<T> enumerator = enumerator()) {
            if (!enumerator.moveNext()) {
                return Optional.empty();
            }
            T best = enumerator.current();
            while (enumerator.moveNext()) {
                T candidate = enumerator.current();
                if (compareNatural(candidate, best) > 0) {
                    best = candidate;
                }
            }
            return Optional.ofNullable(best);
        }
    }

    private List<T> snapshot() {
        return listFactory.get();
    }

    private static <T> List<T> materialize(Iterable<? extends T> source) {
        List<T> result = new ArrayList<>();
        for (T element : source) {
            result.add(element);
        }
        return result;
    }

    private static <T> List<T> materialize(Supplier<? extends Enumerator<T>> factory) {
        List<T> result = new ArrayList<>();
        try (Enumerator<T> enumerator = factory.get()) {
            while (enumerator.moveNext()) {
                result.add(enumerator.current());
            }
        }
        return result;
    }

    private static <T> Enumerator<T> enumeratorOf(Iterator<? extends T> iterator) {
        return new AbstractEnumerator<>() {
            @Override
            protected boolean computeNext() {
                if (!iterator.hasNext()) {
                    return end();
                }
                return yieldValue(iterator.next());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> Enumerator<T> uncheckedCast(Enumerator<? extends T> source) {
        return (Enumerator<T>) source;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> uncheckedListCopy(List<? extends T> source) {
        return new ArrayList<>((List<T>) source);
    }

    private static <T> Enumerator<T> emptyEnumerator() {
        return new AbstractEnumerator<>() {
            @Override
            protected boolean computeNext() {
                return end();
            }
        };
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

    private static <T, K, E> List<ReadOnlyGroup<K, E>> buildGroups(
        List<T> source,
        Function<? super T, ? extends K> keySelector,
        Function<? super T, ? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        List<ReadOnlyGroup<K, E>> groups = new ArrayList<>();
        for (T element : source) {
            K key = keySelector.apply(element);
            ReadOnlyGroup<K, E> group = findGroup(groups, key, equalator);
            if (group == null) {
                group = new ReadOnlyGroup<>(key, new ArrayList<>());
                groups.add(group);
            }
            group.elements().add(elementSelector.apply(element));
        }
        return groups;
    }

    private static <K, E> ReadOnlyGroup<K, E> findGroup(
        List<ReadOnlyGroup<K, E>> groups,
        K key,
        Equalator<? super K> equalator
    ) {
        for (ReadOnlyGroup<K, E> group : groups) {
            if (equalator.equals(group.groupingKey(), key)) {
                return group;
            }
        }
        return null;
    }

    private static <K, A> AggregateBucket<K, A> findAggregateBucket(
        List<AggregateBucket<K, A>> buckets,
        K key,
        Equalator<? super K> equalator
    ) {
        for (AggregateBucket<K, A> bucket : buckets) {
            if (equalator.equals(bucket.key, key)) {
                return bucket;
            }
        }
        return null;
    }

    private <K, A> Enumerable<Groupable<K, A>> aggregateByCore(
        Function<? super T, ? extends K> keySelector,
        Function<? super K, ? extends A> seedSelector,
        BinFunction<? super A, ? super T, ? extends A> aggregator,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(seedSelector);
        NullCheck.requireNonNull(aggregator);
        NullCheck.requireNonNull(equalator);

        List<AggregateBucket<K, A>> buckets = new ArrayList<>();
        try (Enumerator<T> enumerator = enumerator()) {
            while (enumerator.moveNext()) {
                T element = enumerator.current();
                K key = keySelector.apply(element);
                AggregateBucket<K, A> bucket = findAggregateBucket(buckets, key, equalator);
                if (bucket == null) {
                    bucket = new AggregateBucket<>(key, seedSelector.apply(key));
                    buckets.add(bucket);
                }
                bucket.value = aggregator.apply(bucket.value, element);
            }
        }

        List<Groupable<K, A>> result = new ArrayList<>(buckets.size());
        for (AggregateBucket<K, A> bucket : buckets) {
            result.add(new ReadOnlyGroup<>(bucket.key, List.of(bucket.value)));
        }
        return fromList(result);
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

    private static final class ListEnumerator<T> extends AbstractEnumerator<T> {
        private final List<T> elements;
        private int index;

        private ListEnumerator(List<T> elements) {
            this.elements = elements;
        }

        @Override
        protected boolean computeNext() {
            if (index >= elements.size()) {
                return end();
            }
            return yieldValue(elements.get(index++));
        }

        @Override
        public void reset() {
            index = 0;
            resetState();
        }
    }

    private static final class AggregateBucket<K, A> {
        private final K key;
        private A value;

        private AggregateBucket(K key, A value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final class OrderedLinq<T> extends Linq<T> implements OrderedEnumerable<T> {
        private final Supplier<List<T>> sourceListFactory;
        private final Comparator<T> comparator;

        private OrderedLinq(Supplier<List<T>> sourceListFactory, Comparator<T> comparator) {
            super(() -> {
                List<T> items = sourceListFactory.get();
                items.sort(comparator);
                return new ListEnumerator<>(items);
            }, () -> {
                List<T> items = sourceListFactory.get();
                items.sort(comparator);
                return items;
            });
            this.sourceListFactory = sourceListFactory;
            this.comparator = comparator;
        }

        @Override
        public List<T> toList() {
            List<T> items = sourceListFactory.get();
            items.sort(comparator);
            return items;
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public T[] toArray(java.util.function.IntFunction<T[]> generator) {
            NullCheck.requireNonNull(generator);
            List<T> items = toList();
            return items.toArray(generator.apply(items.size()));
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
            return new OrderedLinq<>(sourceListFactory, comparator.thenComparing(keySelector, nextComparator));
        }

        @Override
        public OrderedEnumerable<T> thenOrderByInt(ToIntFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceListFactory, comparator.thenComparingInt(keySelector));
        }

        @Override
        public OrderedEnumerable<T> thenOrderByLong(ToLongFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceListFactory, comparator.thenComparingLong(keySelector));
        }

        @Override
        public OrderedEnumerable<T> thenOrderByDouble(ToDoubleFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(sourceListFactory, comparator.thenComparingDouble(keySelector));
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
            return new OrderedLinq<>(sourceListFactory, comparator.thenComparing(keySelector, nextComparator.reversed()));
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByInt(ToIntFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(
                sourceListFactory,
                comparator.thenComparing(Comparator.comparingInt(keySelector).reversed())
            );
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByLong(ToLongFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(
                sourceListFactory,
                comparator.thenComparing(Comparator.comparingLong(keySelector).reversed())
            );
        }

        @Override
        public OrderedEnumerable<T> thenOrderDescendingByDouble(ToDoubleFunction<? super T> keySelector) {
            NullCheck.requireNonNull(keySelector);
            return new OrderedLinq<>(
                sourceListFactory,
                comparator.thenComparing(Comparator.comparingDouble(keySelector).reversed())
            );
        }
    }
}
