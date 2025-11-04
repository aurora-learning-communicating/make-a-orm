package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerColumn extends NumericColumn<Integer> {
    public IntegerColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
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
