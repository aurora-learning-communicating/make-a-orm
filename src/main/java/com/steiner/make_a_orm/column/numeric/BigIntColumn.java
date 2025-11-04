package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntColumn extends NumericColumn<Long> {
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
}
