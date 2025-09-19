package com.steiner.make_a_orm.column.numeric;

import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntColumn extends NumericColumn<Long> {
    public BigIntColumn(@Nonnull String name) {
        super(name);
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
}
