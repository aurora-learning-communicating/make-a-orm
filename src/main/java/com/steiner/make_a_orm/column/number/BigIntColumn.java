package com.steiner.make_a_orm.column.number;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.aggregate.Summary;
import com.steiner.make_a_orm.column.trait.aggregate.ISummary;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntColumn extends NumberColumn<Long>
        implements
        IEqual<Long, BigIntColumn>,
        ICompare<Long, BigIntColumn>,
        IBetween<Long, BigIntColumn>,
        INullOrNot<Long, BigIntColumn>,
        IInList<Long, BigIntColumn>,
        ISummary<Long, BigDecimal> {
    public BigIntColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Override
    @Nonnull
    public String typeQuote(@Nonnull Dialect dialect) {
        if (isAutoIncrement) {
            return dialect.dataTypeProvider.longAutoIncrementType();
        } else {
            return dialect.dataTypeProvider.longType();
        }
    }

    @Override
    public int sqlType() {
        return Types.BIGINT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Long value) throws SQLException {
        statement.setLong(index, value);
    }

    @Override
    public @Nullable Long read(@Nonnull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Long result -> result;
            case Number result -> result.longValue();
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public BigIntColumn self() {
        return this;
    }

    @Nonnull
    @Override
    public Summary<Long, BigDecimal> sum() {
        return new Summary<Long, BigDecimal>(this) {
            @Nullable
            @Override
            public BigDecimal read(@Nonnull ResultSet resultSet) throws SQLException {
                return resultSet.getBigDecimal(alias());
            }
        };
    }
}
