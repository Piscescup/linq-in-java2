package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code int} values.
 *
 * @author REN YuanTong
 * @since 1.1.0
 */
public interface IntEnumerable
    extends BaseEnumerable<Integer, IntEnumerable>, PrimitiveEnumerable<Integer, IntEnumerable> {
    IntEnumerable whereByInt(IntPredicate predicate);

    IntEnumerable select(IntUnaryOperator selector);

    <R> Enumerable<R> selectToObj(IntFunction<? extends R> selector);

    LongEnumerable mapToLong(IntToLongFunction selector);

    DoubleEnumerable mapToDouble(IntToDoubleFunction selector);

    IntEnumerable skip(long count);

    IntEnumerable take(long count);

    int first();

    OptionalInt firstOptional();

    int single();

    OptionalInt singleOptional();

    IntEnumerable concat(IntEnumerable other);

    IntEnumerable append(int value);

    IntEnumerable prepend(int value);

    IntEnumerable distinct();

    boolean anyByInt(IntPredicate predicate);

    boolean allByInt(IntPredicate predicate);

    long countByInt(IntPredicate predicate);

    boolean contains(int value);

    <A> A aggregate(A seed, BiFunction<? super A, ? super Integer, ? extends A> aggregator);

    int firstByInt(IntPredicate predicate);

    OptionalInt firstOptionalByInt(IntPredicate predicate);

    int singleByInt(IntPredicate predicate);

    OptionalInt singleOptionalByInt(IntPredicate predicate);

    int sum();

    double average();

    OptionalDouble averageOptional();

    int min();

    OptionalInt minOptional();

    int max();

    OptionalInt maxOptional();

    int[] toIntArray();

}
