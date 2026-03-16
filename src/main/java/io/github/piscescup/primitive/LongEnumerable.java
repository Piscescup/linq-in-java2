package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code long} values.
 *
 * @author REN YuanTong
 * @since 1.1.0
 */
public interface LongEnumerable
    extends BaseEnumerable<Long, LongEnumerable>, PrimitiveEnumerable<Long, LongEnumerable> {
    LongEnumerable whereByLong(LongPredicate predicate);

    LongEnumerable select(LongUnaryOperator selector);

    <R> Enumerable<R> selectToObj(LongFunction<? extends R> selector);

    IntEnumerable mapToInt(LongToIntFunction selector);

    DoubleEnumerable mapToDouble(LongToDoubleFunction selector);

    LongEnumerable skip(long count);

    LongEnumerable take(long count);

    long first();

    OptionalLong firstOptional();

    long single();

    OptionalLong singleOptional();

    LongEnumerable concat(LongEnumerable other);

    LongEnumerable append(long value);

    LongEnumerable prepend(long value);

    LongEnumerable distinct();

    boolean anyByLong(LongPredicate predicate);

    boolean allByLong(LongPredicate predicate);

    long countByLong(LongPredicate predicate);

    boolean contains(long value);

    <A> A aggregate(A seed, BiFunction<? super A, ? super Long, ? extends A> aggregator);

    long firstByLong(LongPredicate predicate);

    OptionalLong firstOptionalByLong(LongPredicate predicate);

    long singleByLong(LongPredicate predicate);

    OptionalLong singleOptionalByLong(LongPredicate predicate);

    long sum();

    double average();

    OptionalDouble averageOptional();

    long min();

    OptionalLong minOptional();

    long max();

    OptionalLong maxOptional();

    long[] toLongArray();

}
