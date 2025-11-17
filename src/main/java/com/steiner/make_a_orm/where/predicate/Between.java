package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Between<T, E extends Column<T>> extends WherePredicate<T, E> {
    @FunctionalInterface
    interface ProcessStrategy<E> {
        void process(@Nonnull PreparedStatement statement, int index, @Nonnull E value) throws SQLException;
    }

    public static Map<Class<?>, ProcessStrategy<?>> strategies;

    static {
        strategies = new HashMap<>();
        strategies.put(Byte.class, (ProcessStrategy<Byte>) PreparedStatement::setByte);
        strategies.put(Short.class, (ProcessStrategy<Short>) PreparedStatement::setShort);
        strategies.put(Integer.class, (ProcessStrategy<Integer>) PreparedStatement::setInt);
        strategies.put(Long.class, (ProcessStrategy<Long>) PreparedStatement::setLong);
        strategies.put(Double.class, (ProcessStrategy<Integer>) PreparedStatement::setDouble);
        strategies.put(Float.class, (ProcessStrategy<Integer>) PreparedStatement::setFloat);
        strategies.put(BigDecimal.class, (ProcessStrategy<BigDecimal>) PreparedStatement::setBigDecimal);
        strategies.put(java.sql.Date.class, (ProcessStrategy<java.sql.Date>) PreparedStatement::setDate);
        strategies.put(java.sql.Time.class, (ProcessStrategy<java.sql.Time>) PreparedStatement::setTime);
        strategies.put(java.sql.Timestamp.class, (ProcessStrategy<java.sql.Timestamp>) PreparedStatement::setTimestamp);
    }

    @Nonnull
    public T min;

    @Nonnull
    public T max;

    public Between(@Nonnull E column, @Nonnull T min, @Nonnull T max) {
        super(column);
        this.min = min;
        this.max = max;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        int index = getWriteIndex();

        ProcessStrategy<T> strategy = (ProcessStrategy<T>) strategies.get(this.min.getClass());
        strategy.process(statement, index, min);
        strategy.process(statement, index + 1, max);

        return index + 2;
    }

    @Nonnull
    @Override
    public String toSQL() {
        String format = "%s between %s and %";

        if (isInCheck) {
            return format.formatted(Quote.quoteColumnStandalone(column), column.format(min), column.format(max));
        } else {
            return format.formatted(Quote.quoteColumnStandalone(column), Quote.slot, Quote.slot);
        }
    }
}
