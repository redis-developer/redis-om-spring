
package com.redis.om.spring.tuple.impl.mapper;

import java.util.function.Function;

import com.redis.om.spring.tuple.AbstractTupleMapper;
import com.redis.om.spring.tuple.Pair;
import com.redis.om.spring.tuple.TupleMapper;
import com.redis.om.spring.tuple.Tuples;

/**
 * An implementation class of a {@link TupleMapper } of degree 2
 * <p>
 * Generated by com.speedment.sources.pattern.tuple.TupleMapperImplPattern
 *
 * @param <T>  Type of the original object for the mapper to use when creating a
 *             {@code Tuple }
 * @param <T0> type of element 0
 * @param <T1> type of element 1
 */
public final class Tuple2MapperImpl<T, T0, T1>
extends AbstractTupleMapper<T, Pair<T0, T1>>
implements TupleMapper<T, Pair<T0, T1>> {

    /**
     * Constructs a {@link TupleMapper } that can create {@link Pair }.
     *
     * @param m0 mapper to apply for element 0
     * @param m1 mapper to apply for element 1
     */
    public Tuple2MapperImpl(Function<T, T0> m0, Function<T, T1> m1) {
        super(2);
        set(0, m0);
        set(1, m1);
    }

    @Override
    public Pair<T0, T1> apply(T t) {
        return Tuples.of(
            get0().apply(t),
            get1().apply(t)
        );
    }

    public Function<T, T0> get0() {
        return getAndCast(0);
    }

    public Function<T, T1> get1() {
        return getAndCast(1);
    }
}