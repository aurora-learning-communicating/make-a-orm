package com.steiner.make_a_orm.column.numeric;

import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TinyIntColumn extends NumericColumn<Byte> {
    public TinyIntColumn(@Nonnull String name) {
        super(name);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "tinyint";
    }

    @Override
    public int sqlType() {
        return Types.TINYINT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Byte value) throws SQLException {
        statement.setByte(index, value);
    }
}
