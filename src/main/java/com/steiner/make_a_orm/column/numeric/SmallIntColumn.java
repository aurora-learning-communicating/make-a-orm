package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class SmallIntColumn extends NumericColumn<Short> {
    public SmallIntColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "smallint";
    }

    @Override
    public int sqlType() {
        return Types.SMALLINT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Short value) throws SQLException {
        statement.setShort(index, value);
    }
}
