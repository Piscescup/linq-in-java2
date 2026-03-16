package io.github.piscescup.primitive;

import io.github.piscescup.Enumerable;
import io.github.piscescup.BaseEnumerable;

import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;

/**
 * LINQ-style enumerable specialized for {@code double} values.
 *
 * @author REN YuanTong
 * @since 1.1.0
 */
public interface DoubleEnumerable
    extends BaseEnumerable<Double, DoubleEnumerable>, PrimitiveEnumerable<Double, DoubleEnumerable> {
    DoubleEnumerable whereByDouble(DoublePredicate predicate);

    DoubleEnumerable select(DoubleUnaryOperator selector);

    <R> Enumerable<R> selectToObj(DoubleFunction<? extends R> selector);

    IntEnumerable mapToInt(DoubleToIntFunction selector);

    LongEnumerable mapToLong(DoubleToLongFunction selector);

    DoubleEnumerable skip(long count);

    DoubleEnumerable take(long count);

    double first();

    OptionalDouble firstOptional();

    double single();

    OptionalDouble singleOptional();

    DoubleEnumerable concat(DoubleEnumerable other);

    DoubleEnumerable append(double value);

    DoubleEnumerable prepend(double value);

    DoubleEnumerable distinct();

    boolean anyByDouble(DoublePredicate predicate);

    boolean allByDouble(DoublePredicate predicate);

    long countByDouble(DoublePredicate predicate);

    boolean contains(double value);

    <A> A aggregate(A seed, BiFunction<? super A, ? super Double, ? extends A> aggregator);

    double firstByDouble(DoublePredicate predicate);

    OptionalDouble firstOptionalByDouble(DoublePredicate predicate);

    double singleByDouble(DoublePredicate predicate);

    OptionalDouble singleOptionalByDouble(DoublePredicate predicate);

    double sum();

    double average();

    OptionalDouble averageOptional();

    double min();

    OptionalDouble minOptional();

    double max();

    OptionalDouble maxOptional();

    double[] toDoubleArray();

}
