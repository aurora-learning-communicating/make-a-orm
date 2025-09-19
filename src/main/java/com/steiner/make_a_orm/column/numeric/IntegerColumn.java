package com.steiner.make_a_orm.column.numeric;

import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerColumn extends NumericColumn<Integer> {
    public IntegerColumn(@Nonnull String name) {
        super(name);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "int";
    }

    @Override
    public int sqlType() {
        return Types.INTEGER;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Integer value) throws SQLException {
        statement.setInt(index, value);
    }
}
