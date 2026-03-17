package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.Enumerator;
import io.github.piscescup.Groupable;
import io.github.piscescup.Linq;
import io.github.piscescup.entries.BinEntry;
import io.github.piscescup.entries.TriEntry;
import io.github.piscescup.interfaces.Equalator;
import io.github.piscescup.interfaces.exfunction.primitive.DoubleBinFunction;
import io.github.piscescup.util.validation.NullCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;

public final class DoubleLinq implements DoubleEnumerable {
    private final Supplier<List<Double>> materializer;

    public DoubleLinq(Iterable<Double> source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> materialize(source);
    }

    public DoubleLinq(double[] source) {
        NullCheck.requireNonNull(source);
        this.materializer = () -> {
            List<Double> result = new ArrayList<>(source.length);
            for (double value : source) {
                result.add(value);
            }
            return result;
        };
    }

    public DoubleLinq(Supplier<List<Double>> materializer) {
        this.materializer = materializer;
    }

    @Override
    public Enumerator<Double> enumerator() {
        return new ListEnumerator(snapshot());
    }

    @Override
    public Enumerable<Double> boxed() {
        return Linq.fromIterable(snapshot());
    }

    @Override
    public DoubleEnumerable order() {
        return new DoubleLinq(() -> {
            List<Double> result = snapshot();
            result.sort(Double::compare);
            return result;
        });
    }

    @Override
    public DoubleEnumerable orderDescending() {
        return new DoubleLinq(() -> {
            List<Double> result = snapshot();
            result.sort((left, right) -> Double.compare(right, left));
            return result;
        });
    }

    @Override
    public DoubleEnumerable whereByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new DoubleLinq(() -> filter(predicate));
    }

    @Override
    public DoubleEnumerable select(DoubleUnaryOperator selector) {
        NullCheck.requireNonNull(selector);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            for (Double value : snapshot()) {
                result.add(selector.applyAsDouble(value));
            }
            return result;
        });
    }

    @Override
    public <R> Enumerable<R> selectToObj(DoubleFunction<? extends R> selector) {
        NullCheck.requireNonNull(selector);
        List<R> result = new ArrayList<>();
        for (Double value : snapshot()) {
            result.add(selector.apply(value));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K> Enumerable<Groupable<K, Double>> groupBy(DoubleFunction<? extends K> keySelector) {
        return groupBy(keySelector, value -> value, DoubleLinq::defaultEquals);
    }

    @Override
    public <K> Enumerable<Groupable<K, Double>> groupBy(DoubleFunction<? extends K> keySelector, Equalator<? super K> equalator) {
        return groupBy(keySelector, value -> value, equalator);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(DoubleFunction<? extends K> keySelector, DoubleFunction<? extends E> elementSelector) {
        return groupBy(keySelector, elementSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, E> Enumerable<Groupable<K, E>> groupBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(elementSelector);
        NullCheck.requireNonNull(equalator);
        return Linq.fromIterable(buildGroups(snapshot(), keySelector, elementSelector, equalator));
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        BiFunction<? super K, ? super DoubleEnumerable, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, resultSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        BiFunction<? super K, ? super DoubleEnumerable, ? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<R> result = new ArrayList<>();
        for (Grouping<? extends K, Double> group : buildGroups(snapshot(), keySelector, value -> value, equalator)) {
            result.add(resultSelector.apply(group.groupingKey(), new DoubleLinq(group.elements())));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
        BiFunction<? super K, ? super Enumerable<E>, ? extends R> resultSelector
    ) {
        return groupResultBy(keySelector, elementSelector, resultSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, E, R> Enumerable<R> groupResultBy(
        DoubleFunction<? extends K> keySelector,
        DoubleFunction<? extends E> elementSelector,
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
    public IntEnumerable mapToInt(DoubleToIntFunction selector) {
        NullCheck.requireNonNull(selector);
        return new IntLinq(() -> {
            List<Integer> result = new ArrayList<>();
            for (Double value : snapshot()) {
                result.add(selector.applyAsInt(value));
            }
            return result;
        });
    }

    @Override
    public LongEnumerable mapToLong(DoubleToLongFunction selector) {
        NullCheck.requireNonNull(selector);
        return new LongLinq(() -> {
            List<Long> result = new ArrayList<>();
            for (Double value : snapshot()) {
                result.add(selector.applyAsLong(value));
            }
            return result;
        });
    }

    @Override
    public DoubleEnumerable skip(long count) {
        return new DoubleLinq(() -> slice(count, false));
    }

    @Override
    public DoubleEnumerable take(long count) {
        return new DoubleLinq(() -> slice(count, true));
    }

    @Override
    public Enumerable<DoubleEnumerable> chunk(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        List<DoubleEnumerable> result = new ArrayList<>();
        for (List<Double> chunk : chunkLists(size)) {
            result.add(new DoubleLinq(chunk));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<List<Double>> chunkAsList(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }
        return Linq.fromIterable(chunkLists(size));
    }

    @Override
    public <R> R extractTo(Class<R> clazz) {
        NullCheck.requireNonNull(clazz);
        if (clazz == double[].class) {
            return clazz.cast(toDoubleArray());
        }
        Collection<Double> target = instantiateCollection(clazz);
        target.addAll(snapshot());
        return clazz.cast(target);
    }

    @Override
    public DoubleEnumerable takeWhileByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            for (Double value : snapshot()) {
                if (!predicate.test(value)) {
                    break;
                }
                result.add(value);
            }
            return result;
        });
    }

    @Override
    public DoubleEnumerable skipWhileByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        return new DoubleLinq(() -> {
            List<Double> source = snapshot();
            int index = 0;
            while (index < source.size() && predicate.test(source.get(index))) {
                index++;
            }
            return new ArrayList<>(source.subList(index, source.size()));
        });
    }

    @Override
    public double first() {
        return firstOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble firstOptional() {
        List<Double> source = snapshot();
        return source.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of(source.getFirst());
    }

    @Override
    public double single() {
        return singleOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble singleOptional() {
        List<Double> source = snapshot();
        if (source.isEmpty()) {
            return OptionalDouble.empty();
        }
        if (source.size() > 1) {
            throw new IllegalStateException("Sequence contains more than one element.");
        }
        return OptionalDouble.of(source.getFirst());
    }

    @Override
    public DoubleEnumerable concat(DoubleEnumerable other) {
        NullCheck.requireNonNull(other);
        return new DoubleLinq(() -> {
            List<Double> result = snapshot();
            result.addAll(toList(other));
            return result;
        });
    }

    @Override
    public DoubleEnumerable intersect(DoubleEnumerable other) {
        return intersect(other, DoubleLinq::defaultEquals);
    }

    @Override
    public DoubleEnumerable intersect(DoubleEnumerable other, Equalator<? super Double> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new DoubleLinq(() -> {
            List<Double> right = toList(other);
            List<Double> result = new ArrayList<>();
            for (Double value : snapshot()) {
                if (containsBy(right, value, equalator) && !containsBy(result, value, equalator)) {
                    result.add(value);
                }
            }
            return result;
        });
    }

    @Override
    public <K> DoubleEnumerable intersectBy(Enumerable<? extends K> other, DoubleFunction<? extends K> keySelector) {
        return intersectBy(other, keySelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K> DoubleEnumerable intersectBy(
        Enumerable<? extends K> other,
        DoubleFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new DoubleLinq(() -> {
            List<K> keys = new ArrayList<>(other.toList());
            List<K> emitted = new ArrayList<>();
            List<Double> result = new ArrayList<>();
            for (Double value : snapshot()) {
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
    public DoubleEnumerable union(DoubleEnumerable other) {
        return union(other, DoubleLinq::defaultEquals);
    }

    @Override
    public DoubleEnumerable union(DoubleEnumerable other, Equalator<? super Double> equalator) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(equalator);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            addDistinct(result, snapshot(), equalator);
            addDistinct(result, toList(other), equalator);
            return result;
        });
    }

    @Override
    public <K> DoubleEnumerable unionBy(DoubleEnumerable other, DoubleFunction<? extends K> keySelector) {
        return unionBy(other, keySelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K> DoubleEnumerable unionBy(
        DoubleEnumerable other,
        DoubleFunction<? extends K> keySelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(other);
        NullCheck.requireNonNull(keySelector);
        NullCheck.requireNonNull(equalator);
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            List<K> seenKeys = new ArrayList<>();
            addDistinctBy(result, seenKeys, snapshot(), keySelector, equalator);
            addDistinctBy(result, seenKeys, toList(other), keySelector, equalator);
            return result;
        });
    }

    @Override
    public <K, R> Enumerable<R> join(DoubleEnumerable inner, DoubleFunction<? extends K> outerKeySelector, DoubleFunction<? extends K> innerKeySelector, DoubleBinFunction<? extends R> resultSelector) {
        return join(inner, outerKeySelector, innerKeySelector, resultSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> join(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Double> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Double outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            for (Double innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(DoubleEnumerable inner, DoubleFunction<? extends K> outerKeySelector, DoubleFunction<? extends K> innerKeySelector, DoubleBinFunction<? extends R> resultSelector) {
        return leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> leftJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Double> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Double outerValue : snapshot()) {
            K outerKey = outerKeySelector.apply(outerValue);
            boolean matched = false;
            for (Double innerValue : innerItems) {
                if (equalator.equals(outerKey, innerKeySelector.apply(innerValue))) {
                    matched = true;
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.apply(outerValue, 0d));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(DoubleEnumerable inner, DoubleFunction<? extends K> outerKeySelector, DoubleFunction<? extends K> innerKeySelector, DoubleBinFunction<? extends R> resultSelector) {
        return rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector, DoubleLinq::defaultEquals);
    }

    @Override
    public <K, R> Enumerable<R> rightJoin(
        DoubleEnumerable inner,
        DoubleFunction<? extends K> outerKeySelector,
        DoubleFunction<? extends K> innerKeySelector,
        DoubleBinFunction<? extends R> resultSelector,
        Equalator<? super K> equalator
    ) {
        NullCheck.requireNonNull(inner);
        NullCheck.requireNonNull(outerKeySelector);
        NullCheck.requireNonNull(innerKeySelector);
        NullCheck.requireNonNull(resultSelector);
        NullCheck.requireNonNull(equalator);
        List<Double> outerItems = snapshot();
        List<Double> innerItems = toList(inner);
        List<R> result = new ArrayList<>();
        for (Double innerValue : innerItems) {
            K innerKey = innerKeySelector.apply(innerValue);
            boolean matched = false;
            for (Double outerValue : outerItems) {
                if (equalator.equals(innerKey, outerKeySelector.apply(outerValue))) {
                    matched = true;
                    result.add(resultSelector.apply(outerValue, innerValue));
                }
            }
            if (!matched) {
                result.add(resultSelector.apply(0d, innerValue));
            }
        }
        return Linq.fromIterable(result);
    }

    @Override
    public <R> Enumerable<R> zip(DoubleEnumerable second, DoubleBinFunction<? extends R> resultSelector) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(resultSelector);
        List<Double> left = snapshot();
        List<Double> right = toList(second);
        int size = Math.min(left.size(), right.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(resultSelector.apply(left.get(i), right.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public Enumerable<BinEntry<Double, Double>> zip(DoubleEnumerable second) {
        return zip(second, BinEntry::new);
    }

    @Override
    public Enumerable<TriEntry<Double, Double, Double>> zip(DoubleEnumerable second, DoubleEnumerable third) {
        NullCheck.requireNonNull(second);
        NullCheck.requireNonNull(third);
        List<Double> first = snapshot();
        List<Double> secondItems = toList(second);
        List<Double> thirdItems = toList(third);
        int size = Math.min(first.size(), Math.min(secondItems.size(), thirdItems.size()));
        List<TriEntry<Double, Double, Double>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new TriEntry<>(first.get(i), secondItems.get(i), thirdItems.get(i)));
        }
        return Linq.fromIterable(result);
    }

    @Override
    public DoubleEnumerable append(double value) {
        return new DoubleLinq(() -> {
            List<Double> result = snapshot();
            result.add(value);
            return result;
        });
    }

    @Override
    public DoubleEnumerable prepend(double value) {
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            result.add(value);
            result.addAll(snapshot());
            return result;
        });
    }

    @Override
    public DoubleEnumerable distinct() {
        return new DoubleLinq(() -> {
            List<Double> result = new ArrayList<>();
            addDistinct(result, snapshot(), DoubleLinq::defaultEquals);
            return result;
        });
    }

    @Override
    public boolean any() {
        return !snapshot().isEmpty();
    }

    @Override
    public boolean anyByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Double value : snapshot()) {
            if (predicate.test(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Double value : snapshot()) {
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
    public long countByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        long count = 0;
        for (Double value : snapshot()) {
            if (predicate.test(value)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean contains(double value) {
        return snapshot().contains(value);
    }

    @Override
    public <A> A aggregate(A seed, BiFunction<? super A, ? super Double, ? extends A> aggregator) {
        NullCheck.requireNonNull(aggregator);
        A result = seed;
        for (Double value : snapshot()) {
            result = aggregator.apply(result, value);
        }
        return result;
    }

    @Override
    public double firstByDouble(DoublePredicate predicate) {
        return firstOptionalByDouble(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble firstOptionalByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        for (Double value : snapshot()) {
            if (predicate.test(value)) {
                return OptionalDouble.of(value);
            }
        }
        return OptionalDouble.empty();
    }

    @Override
    public double singleByDouble(DoublePredicate predicate) {
        return singleOptionalByDouble(predicate).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble singleOptionalByDouble(DoublePredicate predicate) {
        NullCheck.requireNonNull(predicate);
        Double found = null;
        for (Double value : snapshot()) {
            if (!predicate.test(value)) {
                continue;
            }
            if (found != null) {
                throw new IllegalStateException("Sequence contains more than one matching element.");
            }
            found = value;
        }
        return found == null ? OptionalDouble.empty() : OptionalDouble.of(found);
    }

    @Override
    public double sum() {
        double sum = 0;
        for (Double value : snapshot()) {
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
        List<Double> source = snapshot();
        return source.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of(sum() / source.size());
    }

    @Override
    public double min() {
        return minOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble minOptional() {
        List<Double> source = snapshot();
        if (source.isEmpty()) {
            return OptionalDouble.empty();
        }
        double best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.min(best, source.get(i));
        }
        return OptionalDouble.of(best);
    }

    @Override
    public double max() {
        return maxOptional().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public OptionalDouble maxOptional() {
        List<Double> source = snapshot();
        if (source.isEmpty()) {
            return OptionalDouble.empty();
        }
        double best = source.getFirst();
        for (int i = 1; i < source.size(); i++) {
            best = Math.max(best, source.get(i));
        }
        return OptionalDouble.of(best);
    }

    @Override
    public double[] toDoubleArray() {
        List<Double> source = snapshot();
        double[] result = new double[source.size()];
        for (int i = 0; i < source.size(); i++) {
            result[i] = source.get(i);
        }
        return result;
    }

    private List<Double> snapshot() {
        return new ArrayList<>(materializer.get());
    }

    private List<Double> filter(DoublePredicate predicate) {
        List<Double> result = new ArrayList<>();
        for (Double value : snapshot()) {
            if (predicate.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    private List<Double> slice(long count, boolean take) {
        List<Double> source = snapshot();
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

    private List<List<Double>> chunkLists(int size) {
        List<Double> source = snapshot();
        List<List<Double>> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i += size) {
            result.add(new ArrayList<>(source.subList(i, Math.min(i + size, source.size()))));
        }
        return result;
    }

    private static List<Double> materialize(Iterable<Double> source) {
        List<Double> result = new ArrayList<>();
        for (Double element : source) {
            result.add(element);
        }
        return result;
    }

    private static List<Double> toList(DoubleEnumerable enumerable) {
        List<Double> result = new ArrayList<>();
        try (Enumerator<Double> enumerator = enumerable.enumerator()) {
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

    private static <K> void addDistinctBy(List<Double> target, List<K> seenKeys, List<Double> candidates, DoubleFunction<? extends K> keySelector, Equalator<? super K> equalator) {
        for (Double candidate : candidates) {
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

    private static <K, E> List<Grouping<K, E>> buildGroups(List<Double> source, DoubleFunction<? extends K> keySelector, DoubleFunction<? extends E> elementSelector, Equalator<? super K> equalator) {
        List<Grouping<K, E>> groups = new ArrayList<>();
        for (Double value : source) {
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
    private static <R> Collection<Double> instantiateCollection(Class<R> clazz) {
        if (clazz.isInterface()) {
            if (clazz == List.class || clazz == Collection.class) {
                return new ArrayList<>();
            }
            if (clazz == Set.class) {
                return new LinkedHashSet<>();
            }
        }
        try {
            return (Collection<Double>) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new UnsupportedOperationException("Unsupported extract target: " + clazz.getName(), ex);
        }
    }

    private static final class ListEnumerator implements Enumerator<Double> {
        private final List<Double> elements;
        private int index = -1;
        private boolean prepared;

        private ListEnumerator(List<Double> elements) {
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
        public Double current() {
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
        public Double next() {
            if (!moveNext()) {
                throw new NoSuchElementException();
            }
            return current();
        }

        @Override
        public void forEachRemaining(java.util.function.Consumer<? super Double> action) {
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
