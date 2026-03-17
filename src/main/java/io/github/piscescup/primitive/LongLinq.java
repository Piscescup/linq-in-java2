package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.Enumerator;
import io.github.piscescup.Groupable;
import io.github.piscescup.Linq;
import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.primitive.LongBinFunction;
import io.github.piscescup.util.validation.NullCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

public final class LongLinq implements LongEnumerable {
    private final Supplier<List<Long>> materializer;

    public LongLinq(Iterable<Long> source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> materialize(source);
    }

    public LongLinq(long[] source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> {
            List<Long> result = new ArrayList<>(source.length);
            for (long value : source) {
                result.add(value);
            }
            return result;
        };
    }

    public LongLinq(Supplier<List<Long>> materializer) {
        this.materializer = materializer;
    }

    @Override
    public Enumerator<Long> enumerator() {
        return new ListEnumerator(snapshot());
    }

    @Override
    public Enumerable<Long> boxed() {
        return Linq.fromIterable(snapshot());
    }

    @Override
    public LongEnumerable order() {
        return new LongLinq(() -> {
            List<Long> result = snapshot();
            result.sort(Long::compare);
            return result;
        });
    }

    @Override
    public LongEnumerable orderDescending() {
        return new LongLinq(() -> {
            List<Long> result = snapshot();
            result.sort((left, right) -> Long.compare(right, left));
            return result;
        });
    }

    @Override
    public LongEnumerable whereByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new LongLinq(() -> filter(predicate));
    }

    @Override
    public LongEnumerable select(LongUnaryOperator selector) {
        NullCheck.requireNonNull(selector);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            for (Long value : snapshot()) {
                result.add(selector.applyAsLong(value));
            }
            return result;
        });
    }

    @Override
    public <R> Enumerable<R> selectToObj(LongFunction<? extends R> selector) {
        NullCheck.requireNonNull(selector);
        List<R> result = new ArrayList<>();
        for (Long value : snapshot()) {
            result.add(selector.apply(value));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K> Enumerable<Groupable<K, Long>> groupBy(LongFunction<? extends K> keySelector) {
        return groupBy(keySelector, value -> value, LongLinq::defaultEquals);
    }

    @Override
    public <K> Enumerable<Groupable<K, Long>> groupBy(LongFunction<? extends K> keySelector, Equalator<? super K> equalator) {
        return groupBy(keySelector, value -> value, equalator);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(LongFunction<? extends K> keySelector, LongFunction<? extends E> elementSelector) {
        return groupBy(keySelector, elementSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(
        LongFunction<? extends K> keySelector,
        LongFunction<? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(equalator);
        return Linq.fromIterable(buildGroups(snapshot(), keySelector, elementSelector, equalator));
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        LongFunction<? extends K> keySelector,
        BiFunction<? super K, ? super LongEnumerable, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, resultSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        LongFunction<? extends K> keySelector,
        BiFunction<? super K, ? super LongEnumerable, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<R> result = new ArrayList<>();
        for (Grouping<? extends K, Long> group : buildGroups(snapshot(), keySelector, value -> value, equalator)) {
            result.add(resultSelector.apply(group.groupingKey(), new LongLinq(group.elements())));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        LongFunction<? extends K> keySelector,
        LongFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, elementSelector, resultSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        LongFunction<? extends K> keySelector,
        LongFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<R> result = new ArrayList<>();
        for (Grouping<? extends K, ? extends E> group : buildGroups(snapshot(), keySelector, elementSelector, equalator)) {
            result.add(resultSelector.apply(group.groupingKey(), Linq.fromIterable(group.elements())));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public IntEnumerable mapToInt(LongToIntFunction selector) {
        NullCheck.requireNonNull(selector);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            for (Long value : snapshot()) {
                result.add(selector.applyAsInt(value));
            }
            return result;
        });
    }

    @Override
    public DoubleEnumerable mapToDouble(LongToDoubleFunction selector) {
        NullCheck.requireNonNull(selector);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            for (Long value : snapshot()) {
                result.add(selector.applyAsDouble(value));
            }
            return result;
        });
    }

    @Override
    public LongEnumerable skip(long count) {
        return new LongLinq(() -> slice(count, false));
    }

    @Override
    public LongEnumerable take(long count) {
        return new LongLinq(() -> slice(count, true));
    }

    @Override
    public Enumerable<LongEnumerable> chunk(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        List<LongEnumerable> result = new ArrayList<>();
        for (List<Long> chunk : chunkLists(size)) {
            result.add(new LongLinq(chunk));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<List<Long>> chunkAsList(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        return Linq.fromIterable(chunkLists(size));
    }

    @Override
    public <R> R extractTo(Class<R> clazz) {
        NullCheck.requireNonNull(clazz);
        if (clazz == long[].class) {
            return clazz.cast(toLongArray());
        }
        Collection<Long> target = instantiateCollection(clazz);
        target.addAll(snapshot());
        return clazz.cast(target);
    }

    @Override
    public LongEnumerable takeWhileByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            for (Long value : snapshot()) {
                if (!predicate.test(value)) {
                    break;
                }
                result.add(value);
            }
            return result;
        });
    }

    @Override
    public LongEnumerable skipWhileByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new LongLinq(() -> {
            List<Long> source = snapshot();
            int index = 0;
            while (index < source.size() && predicate.test(source.get(index))) {
                index++;
            }
            return new ArrayList<>(source.subList(index, source.size()));
        });
    }

    @Override
    public long first() {
        return firstOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong firstOptional() {
        List<Long> source = snapshot();
        return source.isEmpty() ? OptionalLong.empty() : OptionalLong.of(source.getFirst());
    }

    @Override
    public long single() {
        return singleOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong singleOptional() {
        List<Long> source = snapshot();
        if (source.isEmpty()) {
            return OptionalLong.empty();
        }
        if (source.size() > 1) {
            throw new IllegalStateException("Sequence contains more than one element.");
        }
        return OptionalLong.of(source.getFirst());
    }

    @Override
    public LongEnumerable concat(LongEnumerable other) {
        NullCheck.requireNonNull(other);
        return new LongLinq(() -> {
            List<Long> result = snapshot();
            result.addAll(toList(other));
            return result;
        });
    }

    @Override
    public LongEnumerable intersect(LongEnumerable other) {
        return intersect(other, LongLinq::defaultEquals);
    }

    @Override
    public LongEnumerable intersect(LongEnumerable other, Equalator<? super Long> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new LongLinq(() -> {
            List<Long> right = toList(other);
            List<Long> result = new ArrayList<>();
            for (Long value : snapshot()) {
                if (containsBy(right, value, equalator) && !containsBy(result, value, equalator)) {
                    result.add(value);
                }
            }
            return result;
        });
    }

    @Override
    public <K> LongEnumerable intersectBy(Enumerable<? extends K> other, LongFunction<? extends K> keySelector) {
        return intersectBy(other, keySelector, LongLinq::defaultEquals);
    }

    @Override
    public <K> LongEnumerable intersectBy(
        Enumerable<? extends K> other,
        LongFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new LongLinq(() -> {
            List<K> keys = new ArrayList<>(other.toList());
            List<K> emitted = new ArrayList<>();
            List<Long> result = new ArrayList<>();
            for (Long value : snapshot()) {
                K key = keySelector.apply(value);
                if (containsBy(keys, key, equalator) && !containsBy(emitted, key, equalator)) {
                    emitted.add(key);
                    result.add(value);
                }
            }
            return result;
        });
    }

    @Override
    public LongEnumerable union(LongEnumerable other) {
        return union(other, LongLinq::defaultEquals);
    }

    @Override
    public LongEnumerable union(LongEnumerable other, Equalator<? super Long> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            addDistinct(result, snapshot(), equalator);
            addDistinct(result, toList(other), equalator);
            return result;
        });
    }

    @Override
    public <K> LongEnumerable unionBy(LongEnumerable other, LongFunction<? extends K> keySelector) {
        return unionBy(other, keySelector, LongLinq::defaultEquals);
    }

    @Override
    public <K> LongEnumerable unionBy(
        LongEnumerable other,
        LongFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            List<K> seenKeys = new ArrayList<>();
            addDistinctBy(result, seenKeys, snapshot(), keySelector, equalator);
            addDistinctBy(result, seenKeys, toList(other), keySelector, equalator);
            return result;
        });
    }

    @Override
    public <K, R> Enumerable<R> join(LongEnumerable inner, LongFunction<? extends K> outerKeySelector, LongFunction<? extends K> innerKeySelector, LongBinFunction<? extends R> resultSelector) {
        return join(inner, outerKeySelector, innerKeySelector, resultSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> join(
        LongEnumerable inner,
        LongFunction<? extends K> outerKeySelector,
        LongFunction<? extends K> innerKeySelector,
        LongBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Long> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Long outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            for (Long innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    result.add(resultSelector.applyAsLong(outerValue, innerValue));
                }
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(LongEnumerable inner, LongFunction<? extends K> outerKeySelector, LongFunction<? extends K> innerKeySelector, LongBinFunction<? extends R> resultSelector) {
        return leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(
        LongEnumerable inner,
        LongFunction<? extends K> outerKeySelector,
        LongFunction<? extends K> innerKeySelector,
        LongBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Long> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Long outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            boolean matched = false;
            for (Long innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    matched = true;
                    result.add(resultSelector.applyAsLong(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.applyAsLong(outerValue, 0L));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(LongEnumerable inner, LongFunction<? extends K> outerKeySelector, LongFunction<? extends K> innerKeySelector, LongBinFunction<? extends R> resultSelector) {
        return rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector, LongLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(
        LongEnumerable inner,
        LongFunction<? extends K> outerKeySelector,
        LongFunction<? extends K> innerKeySelector,
        LongBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Long> outerItems = snapshot();
        List<Long> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Long innerValue : innerItems) {
            K innerKey = innerKeySelector.apply(innerValue);
            boolean matched = false;
            for (Long outerValue : outerItems) {
                if (equalator.equals(innerKey, outerKeySelector.apply(outerValue))) {
                    matched = true;
                    result.add(resultSelector.applyAsLong(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.applyAsLong(0L, innerValue));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <R> Enumerable<R> zip(LongEnumerable second, LongBinFunction<? extends R> resultSelector) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(resultSelector);
        List<Long> left = snapshot();
        List<Long> right = toList(second);
        int size = Math.min(left.size(), right.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(resultSelector.applyAsLong(left.get(i), right.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<BinEntry<Long, Long>> zip(LongEnumerable second) {
        return zip(second, BinEntry::new);
    }

    @Override
    public Enumerable<TriEntry<Long, Long, Long>> zip(LongEnumerable second, LongEnumerable third) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(third);
        List<Long> first = snapshot();
        List<Long> secondItems = toList(second);
        List<Long> thirdItems = toList(third);
        int size = Math.min(first.size(), Math.min(secondItems.size(), thirdItems.size()));
        List<TriEntry<Long, Long, Long>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new TriEntry<>(first.get(i), secondItems.get(i), thirdItems.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public LongEnumerable append(long value) {
        return new LongLinq(() -> {
            List<Long> result = snapshot();
            result.add(value);
            return result;
        });
    }

    @Override
    public LongEnumerable prepend(long value) {
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            result.add(value);
            result.addAll(snapshot());
            return result;
        });
    }

    @Override
    public LongEnumerable distinct() {
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            addDistinct(result, snapshot(), LongLinq::defaultEquals);
            return result;
        });
    }

    @Override
    public boolean any() {
        return !snapshot().isEmpty();
    }

    @Override
    public boolean anyByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Long value : snapshot()) {
            if (predicate.test(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Long value : snapshot()) {
            if (!predicate.test(value)) {
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
    public long countByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        long count = 0;
        for (Long value : snapshot()) {
            if (predicate.test(value)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean contains(long value) {
        return snapshot().contains(value);
    }

    @Override
    public <A> A aggregate(A seed, BiFunction<? super A, ? super Long, ? extends A> aggregator) {
        NullCheck.requireNonNull(aggregator);
        A result = seed;
        for (Long value : snapshot()) {
            result = aggregator.apply(result, value);
        }
        return result;
    }

    @Override
    public long firstByLong(LongPredicate predicate) {
        return firstOptionalByLong(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong firstOptionalByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Long value : snapshot()) {
            if (predicate.test(value)) {
                return OptionalLong.of(value);
            }
        }
        return OptionalLong.empty();
    }

    @Override
    public long singleByLong(LongPredicate predicate) {
        return singleOptionalByLong(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong singleOptionalByLong(LongPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        Long found = null;
        for (Long value : snapshot()) {
            if (!predicate.test(value)) {
                continue;
            }
            if (found != null) {
                throw new IllegalStateException("Sequence contains more than one matching element.");
            }
            found = value;
        }
        return found == null ? OptionalLong.empty() : OptionalLong.of(found);
    }

    @Override
    public long sum() {
        long sum = 0;
        for (Long value : snapshot()) {
            sum += value;
        }
        return sum;
    }

    @Override
    public double average() {
        return averageOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble averageOptional() {
        List<Long> source = snapshot();
        return source.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of((double) sum() / source.size());
    }

    @Override
    public long min() {
        return minOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong minOptional() {
        List<Long> source = snapshot();
        if (source.isEmpty()) {
            return OptionalLong.empty();
        }
        long best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.min(best, source.get(i));
        }
        return OptionalLong.of(best);
    }

    @Override
    public long max() {
        return maxOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalLong maxOptional() {
        List<Long> source = snapshot();
        if (source.isEmpty()) {
            return OptionalLong.empty();
        }
        long best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.max(best, source.get(i));
        }
        return OptionalLong.of(best);
    }

    @Override
    public long[] toLongArray() {
        List<Long> source = snapshot();
        long[] result = new long[source.size()];
        for (int i = 0; i < source.size(); i++) {
            result[i] = source.get(i);
        }
        return result;
    }

    @Override
    public List<Long> toList() {
        return snapshot();
    }

    private List<Long> snapshot() {
        return new ArrayList<>(materializer.get());
    }

    private List<Long> filter(LongPredicate predicate) {
        List<Long> result = new ArrayList<>();
        for (Long value : snapshot()) {
            if (predicate.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    private List<Long> slice(long count, boolean take) {
        List<Long> source = snapshot();
        if (take) {
            if (count <= 0) {
                return new ArrayList<>();
            }
            if (count >= source.size()) {
                return source;
            }
            return new ArrayList<>(source.subList(0, (int) count));
        }
        if (count <= 0) {
            return source;
        }
        if (count >= source.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source.subList((int) count, source.size()));
    }

    private List<List<Long>> chunkLists(int size) {
        List<Long> source = snapshot();
        List<List<Long>> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i += size) {
            result.add(new ArrayList<>(source.subList(i, Math.min(i + size, source.size()))));
        }
        return result;
    }

    private static List<Long> materialize(Iterable<Long> source) {
        List<Long> result = new ArrayList<>();
        for (Long element : source) {
            result.add(element);
        }
        return result;
    }

    private static List<Long> toList(LongEnumerable enumerable) {
        List<Long> result = new ArrayList<>();
        try (Enumerator<Long> enumerator = enumerable.enumerator()) {
            while (enumerator.moveNext()) {
                result.add(enumerator.current());
            }
        }
        return result;
    }

    private static boolean defaultEquals(Object left, Object right) {
        return Objects.equals(left, right);
    }

    private static <T> void addDistinct(List<T> target, List<? extends T> candidates, Equalator<? super T> equalator) {
        for (T candidate : candidates) {
            if (!containsBy(target, candidate, equalator)) {
                target.add(candidate);
            }
        }
    }

    private static <K> void addDistinctBy(List<Long> target, List<K> seenKeys, List<Long> candidates, LongFunction<? extends K> keySelector, Equalator<? super K> equalator) {
        for (Long candidate : candidates) {
            K key = keySelector.apply(candidate);
            if (!containsBy(seenKeys, key, equalator)) {
                seenKeys.add(key);
                target.add(candidate);
            }
        }
    }

    private static <T> boolean containsBy(List<? extends T> source, T target, Equalator<? super T> equalator) {
        for (T element : source) {
            if (equalator.equals(element, target)) {
                return true;
            }
        }
        return false;
    }

    private static <K, E> List<Grouping<K, E>> buildGroups(List<Long> source, LongFunction<? extends K> keySelector, LongFunction<? extends E> elementSelector, Equalator<? super K> equalator) {
        List<Grouping<K, E>> groups = new ArrayList<>();
        for (Long value : source) {
            K key = keySelector.apply(value);
            Grouping<K, E> group = findGroup(groups, key, equalator);
            if (group == null) {
                group = new Grouping<>(key, new ArrayList<>());
                groups.add(group);
            }
            group.elements().add(elementSelector.apply(value));
        }
        return groups;
    }

    private static <K, E> Grouping<K, E> findGroup(List<Grouping<K, E>> groups, K key, Equalator<? super K> equalator) {
        for (Grouping<K, E> group : groups) {
            if (equalator.equals(group.groupingKey(), key)) {
                return group;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <R> Collection<Long> instantiateCollection(Class<R> clazz) {
        if (clazz.isInterface()) {
            if (clazz == List.class || clazz == Collection.class) {
                return new ArrayList<>();
            }
            if (clazz == Set.class) {
                return new LinkedHashSet<>();
            }
        }
        try {
            return (Collection<Long>) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new UnsupportedOperationException("Unsupported extract target: " + clazz.getName(), ex);
        }
    }

    private static final class ListEnumerator implements Enumerator<Long> {
        private final List<Long> elements;
        private int index = -1;
        private boolean prepared;

        private ListEnumerator(List<Long> elements) {
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
        public Long current() {
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
        public Long next() {
            if (!moveNext()) {
                throw new NoSuchElementException();
            }
            return current();
        }

        @Override
        public void forEachRemaining(java.util.function.Consumer<? super Long> action) {
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
}
