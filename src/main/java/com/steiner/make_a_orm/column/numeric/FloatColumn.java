package com.steiner.make_a_orm.column.numeric;

import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FloatColumn extends NumericColumn<Float> {
    public FloatColumn(@Nonnull String name) {
        super(name);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "float";
    }

    @Override
    public int sqlType() {
        return Types.FLOAT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Float value) throws SQLException {
        statement.setFloat(index, value);
    }
}
