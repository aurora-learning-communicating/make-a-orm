package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntColumn extends NumericColumn<Long>
        implements
        IEqual<Long, BigIntColumn>,
        ICompare<Long, BigIntColumn>,
        IBetween<Long, BigIntColumn>,
        INullOrNot<Long, BigIntColumn>,
        IInList<Long, BigIntColumn> {
    public BigIntColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "bigint";
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
    public @Nullable Long read(@NotNull ResultSet resultSet) throws SQLException {
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
}
