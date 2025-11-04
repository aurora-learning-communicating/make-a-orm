package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FloatColumn extends NumericColumn<Float> {
    public FloatColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
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
