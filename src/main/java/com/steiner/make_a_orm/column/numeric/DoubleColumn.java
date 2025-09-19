package com.steiner.make_a_orm.column.numeric;

import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoubleColumn extends NumericColumn<Double> {
    public DoubleColumn(@Nonnull String name) {
        super(name);
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
}
