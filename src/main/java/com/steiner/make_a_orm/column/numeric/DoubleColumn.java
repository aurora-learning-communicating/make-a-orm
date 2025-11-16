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

public class DoubleColumn extends NumericColumn<Double>
        implements
        IEqual<Double, DoubleColumn>,
        ICompare<Double, DoubleColumn>,
        IBetween<Double, DoubleColumn>,
        IInList<Double, DoubleColumn>,
        INullOrNot<Double, DoubleColumn> {
    public DoubleColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "double";
    }

    @Override
    public int sqlType() {
        return Types.DOUBLE;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Double value) throws SQLException {
        statement.setDouble(index, value);
    }

    @Override
    public @Nullable Double read(@NotNull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Double result -> result;
            case Float result -> Double.valueOf(result.toString());
            case Number result -> result.doubleValue();
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public DoubleColumn self() {
        return this;
    }
}
