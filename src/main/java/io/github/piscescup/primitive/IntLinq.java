package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.Enumerator;
import io.github.piscescup.Groupable;
import io.github.piscescup.Linq;
import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.primitive.IntBinFunction;
import io.github.piscescup.util.validation.NullCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public final class IntLinq implements IntEnumerable {
    private final Supplier<List<Integer>> materializer;

    public IntLinq(Iterable<Integer> source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> materialize(source);
    }

    public IntLinq(int[] source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> {
            List<Integer> result = new ArrayList<>(source.length);
            for (int value : source) {
                result.add(value);
            }
            return result;
        };
    }

    public IntLinq(Supplier<List<Integer>> materializer) {
        this.materializer = materializer;
    }

    @Override
    public Enumerator<Integer> enumerator() {
        return new ListEnumerator(snapshot());
    }

    @Override
    public Enumerable<Integer> boxed() {
        return Linq.fromIterable(snapshot());
    }

    @Override
    public IntEnumerable order() {
        return new IntLinq(() -> {
            List<Integer> result = snapshot();
            result.sort(Integer::compare);
            return result;
        });
    }

    @Override
    public IntEnumerable orderDescending() {
        return new IntLinq(() -> {
            List<Integer> result = snapshot();
            result.sort((left, right) -> Integer.compare(right, left));
            return result;
        });
    }

    @Override
    public IntEnumerable whereByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new IntLinq(() -> filter(predicate));
    }

    @Override
    public IntEnumerable select(IntUnaryOperator selector) {
        NullCheck.requireNonNull(selector);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            for (int value : snapshot()) {
                result.add(selector.applyAsInt(value));
            }
            return result;
        });
    }

    @Override
    public <R> Enumerable<R> selectToObj(IntFunction<? extends R> selector) {
        NullCheck.requireNonNull(selector);
        List<R> result = new ArrayList<>();
        for (int value : snapshot()) {
            result.add(selector.apply(value));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K> Enumerable<Groupable<K, Integer>> groupBy(IntFunction<? extends K> keySelector) {
        return groupBy(keySelector, value -> value, IntLinq::defaultEquals);
    }

    @Override
    public <K> Enumerable<Groupable<K, Integer>> groupBy(IntFunction<? extends K> keySelector, Equalator<? super K> equalator) {
        return groupBy(keySelector, value -> value, equalator);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(IntFunction<? extends K> keySelector, IntFunction<? extends E> elementSelector) {
        return groupBy(keySelector, elementSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(equalator);
        return Linq.fromIterable(buildGroups(snapshot(), keySelector, elementSelector, equalator));
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        IntFunction<? extends K> keySelector,
        BiFunction<? super K, ? super IntEnumerable, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, resultSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        IntFunction<? extends K> keySelector,
        BiFunction<? super K, ? super IntEnumerable, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<R> result = new ArrayList<>();
        for (Grouping<? extends K, Integer> group : buildGroups(snapshot(), keySelector, value -> value, equalator)) {
            result.add(resultSelector.apply(group.groupingKey(), new IntLinq(group.elements())));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, elementSelector, resultSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
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
    public LongEnumerable mapToLong(IntToLongFunction selector) {
        NullCheck.requireNonNull(selector);
        throw new UnsupportedOperationException("LongLinq has not been added yet.");
    }

    @Override
    public DoubleEnumerable mapToDouble(IntToDoubleFunction selector) {
        NullCheck.requireNonNull(selector);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            for (int value : snapshot()) {
                result.add(selector.applyAsDouble(value));
            }
            return result;
        });
    }

    @Override
    public IntEnumerable skip(long count) {
        return new IntLinq(() -> slice(count, false));
    }

    @Override
    public IntEnumerable take(long count) {
        return new IntLinq(() -> slice(count, true));
    }

    @Override
    public Enumerable<IntEnumerable> chunk(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        List<IntEnumerable> result = new ArrayList<>();
        for (List<Integer> chunk : chunkLists(size)) {
            result.add(new IntLinq(chunk));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<List<Integer>> chunkAsList(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        return Linq.fromIterable(chunkLists(size));
    }

    @Override
    public <R> R extractTo(Class<R> clazz) {
        NullCheck.requireNonNull(clazz);
        if (clazz == int[].class) {
            return clazz.cast(toIntArray());
        }
        Collection<Integer> target = instantiateCollection(clazz);
        target.addAll(snapshot());
        return clazz.cast(target);
    }

    @Override
    public IntEnumerable takeWhileByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            for (int value : snapshot()) {
                if (!predicate.test(value)) {
                    break;
                }
                result.add(value);
            }
            return result;
        });
    }

    @Override
    public IntEnumerable skipWhileByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new IntLinq(() -> {
            List<Integer> source = snapshot();
            int index = 0;
            while (index < source.size() && predicate.test(source.get(index))) {
                index++;
            }
            return new ArrayList<>(source.subList(index, source.size()));
        });
    }

    @Override
    public int first() {
        return firstOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt firstOptional() {
        List<Integer> source = snapshot();
        return source.isEmpty() ? OptionalInt.empty() : OptionalInt.of(source.getFirst());
    }

    @Override
    public int single() {
        return singleOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt singleOptional() {
        List<Integer> source = snapshot();
        if (source.isEmpty()) {
            return OptionalInt.empty();
        }
        if (source.size() > 1) {
            throw new IllegalStateException("Sequence contains more than one element.");
        }
        return OptionalInt.of(source.getFirst());
    }

    @Override
    public IntEnumerable concat(IntEnumerable other) {
        NullCheck.requireNonNull(other);
        return new IntLinq(() -> {
            List<Integer> result = snapshot();
            result.addAll(toList(other));
            return result;
        });
    }

    @Override
    public IntEnumerable intersect(IntEnumerable other) {
        return intersect(other, IntLinq::defaultEquals);
    }

    @Override
    public IntEnumerable intersect(IntEnumerable other, Equalator<? super Integer> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new IntLinq(() -> {
            List<Integer> right = toList(other);
            List<Integer> result = new ArrayList<>();
            for (int value : snapshot()) {
                if (containsBy(right, value, equalator) && !containsBy(result, value, equalator)) {
                    result.add(value);
                }
            }
            return result;
        });
    }

    @Override
    public <K> IntEnumerable intersectBy(Enumerable<? extends K> other, IntFunction<? extends K> keySelector) {
        return intersectBy(other, keySelector, IntLinq::defaultEquals);
    }

    @Override
    public <K> IntEnumerable intersectBy(
        Enumerable<? extends K> other,
        IntFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new IntLinq(() -> {
            List<K> keys = new ArrayList<>(other.toList());
            List<K> emitted = new ArrayList<>();
            List<Integer> result = new ArrayList<>();
            for (int value : snapshot()) {
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
    public IntEnumerable union(IntEnumerable other) {
        return union(other, IntLinq::defaultEquals);
    }

    @Override
    public IntEnumerable union(IntEnumerable other, Equalator<? super Integer> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            addDistinct(result, snapshot(), equalator);
            addDistinct(result, toList(other), equalator);
            return result;
        });
    }

    @Override
    public <K> IntEnumerable unionBy(IntEnumerable other, IntFunction<? extends K> keySelector) {
        return unionBy(other, keySelector, IntLinq::defaultEquals);
    }

    @Override
    public <K> IntEnumerable unionBy(
        IntEnumerable other,
        IntFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            List<K> seenKeys = new ArrayList<>();
            addDistinctBy(result, seenKeys, snapshot(), keySelector, equalator);
            addDistinctBy(result, seenKeys, toList(other), keySelector, equalator);
            return result;
        });
    }

    @Override
    public <K, R> Enumerable<R> join(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector
    ) {
        return join(inner, outerKeySelector, innerKeySelector, resultSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> join(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Integer> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (int outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            for (int innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector
    ) {
        return leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Integer> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (int outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            boolean matched = false;
            for (int innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    matched = true;
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.apply(outerValue, 0));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector
    ) {
        return rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector, IntLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(
        IntEnumerable inner,
        IntFunction<? extends K> outerKeySelector,
        IntFunction<? extends K> innerKeySelector,
        IntBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Integer> outerItems = snapshot();
        List<Integer> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (int innerValue : innerItems) {
            K innerKey = innerKeySelector.apply(innerValue);
            boolean matched = false;
            for (int outerValue : outerItems) {
                if (equalator.equals(innerKey, outerKeySelector.apply(outerValue))) {
                    matched = true;
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.apply(0, innerValue));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <R> Enumerable<R> zip(IntEnumerable second, IntBinFunction<? extends R> resultSelector) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(resultSelector);
        List<Integer> left = snapshot();
        List<Integer> right = toList(second);
        int size = Math.min(left.size(), right.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(resultSelector.apply(left.get(i), right.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<BinEntry<Integer, Integer>> zip(IntEnumerable second) {
        return zip(second, BinEntry::new);
    }

    @Override
    public Enumerable<TriEntry<Integer, Integer, Integer>> zip(IntEnumerable second, IntEnumerable third) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(third);
        List<Integer> first = snapshot();
        List<Integer> secondItems = toList(second);
        List<Integer> thirdItems = toList(third);
        int size = Math.min(first.size(), Math.min(secondItems.size(), thirdItems.size()));
        List<TriEntry<Integer, Integer, Integer>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new TriEntry<>(first.get(i), secondItems.get(i), thirdItems.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public IntEnumerable append(int value) {
        return new IntLinq(() -> {
            List<Integer> result = snapshot();
            result.add(value);
            return result;
        });
    }

    @Override
    public IntEnumerable prepend(int value) {
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            result.add(value);
            result.addAll(snapshot());
            return result;
        });
    }

    @Override
    public IntEnumerable distinct() {
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            addDistinct(result, snapshot(), IntLinq::defaultEquals);
            return result;
        });
    }

    @Override
    public boolean any() {
        return !snapshot().isEmpty();
    }

    @Override
    public boolean anyByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (int value : snapshot()) {
            if (predicate.test(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (int value : snapshot()) {
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
    public long countByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        long count = 0;
        for (int value : snapshot()) {
            if (predicate.test(value)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean contains(int value) {
        return snapshot().contains(value);
    }

    @Override
    public <A> A aggregate(A seed, BiFunction<? super A, ? super Integer, ? extends A> aggregator) {
        NullCheck.requireNonNull(aggregator);
        A result = seed;
        for (int value : snapshot()) {
            result = aggregator.apply(result, value);
        }
        return result;
    }

    @Override
    public int firstByInt(IntPredicate predicate) {
        return firstOptionalByInt(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt firstOptionalByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (int value : snapshot()) {
            if (predicate.test(value)) {
                return OptionalInt.of(value);
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public int singleByInt(IntPredicate predicate) {
        return singleOptionalByInt(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt singleOptionalByInt(IntPredicate predicate) {
        NullCheck.requireNonNull(predicate);
        Integer found = null;
        for (int value : snapshot()) {
            if (!predicate.test(value)) {
                continue;
            }
            if (found != null) {
                throw new IllegalStateException("Sequence contains more than one matching element.");
            }
            found = value;
        }
        return found == null ? OptionalInt.empty() : OptionalInt.of(found);
    }

    @Override
    public int sum() {
        int sum = 0;
        for (int value : snapshot()) {
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
        List<Integer> source = snapshot();
        return source.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of((double) sum() / source.size());
    }

    @Override
    public int min() {
        return minOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt minOptional() {
        List<Integer> source = snapshot();
        if (source.isEmpty()) {
            return OptionalInt.empty();
        }
        int best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.min(best, source.get(i));
        }
        return OptionalInt.of(best);
    }

    @Override
    public int max() {
        return maxOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalInt maxOptional() {
        List<Integer> source = snapshot();
        if (source.isEmpty()) {
            return OptionalInt.empty();
        }
        int best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.max(best, source.get(i));
        }
        return OptionalInt.of(best);
    }

    @Override
    public int[] toIntArray() {
        List<Integer> source = snapshot();
        int[] result = new int[source.size()];
        for (int i = 0; i < source.size(); i++) {
            result[i] = source.get(i);
        }
        return result;
    }

    private List<Integer> snapshot() {
        return new ArrayList<>(materializer.get());
    }

    private List<Integer> filter(IntPredicate predicate) {
        List<Integer> result = new ArrayList<>();
        for (int value : snapshot()) {
            if (predicate.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    private List<Integer> slice(long count, boolean take) {
        List<Integer> source = snapshot();
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

    private List<List<Integer>> chunkLists(int size) {
        List<Integer> source = snapshot();
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i += size) {
            result.add(new ArrayList<>(source.subList(i, Math.min(i + size, source.size()))));
        }
        return result;
    }

    private static List<Integer> materialize(Iterable<Integer> source) {
        List<Integer> result = new ArrayList<>();
        for (Integer element : source) {
            result.add(element);
        }
        return result;
    }

    private static List<Integer> toList(IntEnumerable enumerable) {
        List<Integer> result = new ArrayList<>();
        try (Enumerator<Integer> enumerator = enumerable.enumerator()) {
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

    private static <K> void addDistinctBy(
        List<Integer> target, List<K> seenKeys, List<Integer> candidates,
        IntFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        for (int candidate : candidates) {
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

    private static <K, E> List<Grouping<K, E>> buildGroups(
        List<Integer> source,
        IntFunction<? extends K> keySelector,
        IntFunction<? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        List<Grouping<K, E>> groups = new ArrayList<>();
        for (int value : source) {
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
    private static <R> Collection<Integer> instantiateCollection(Class<R> clazz) {
        if (clazz.isInterface()) {
            if (clazz == List.class || clazz == Collection.class) {
                return new ArrayList<>();
            }
            if (clazz == Set.class) {
                return new LinkedHashSet<>();
            }
        }
        try {
            return (Collection<Integer>) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new UnsupportedOperationException("Unsupported extract target: " + clazz.getName(), ex);
        }
    }

    private static final class ListEnumerator implements Enumerator<Integer> {
        private final List<Integer> elements;
        private int index = -1;
        private boolean prepared;

        private ListEnumerator(List<Integer> elements) {
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
        public Integer current() {
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
        public Integer next() {
            if (!moveNext()) {
                throw new NoSuchElementException();
            }
            return current();
        }

        @Override
        public void forEachRemaining(java.util.function.Consumer<? super Integer> action) {
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
