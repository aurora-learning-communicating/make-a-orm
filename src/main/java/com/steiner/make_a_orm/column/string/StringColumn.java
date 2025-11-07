package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class StringColumn extends Column<String> {
    public StringColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull String value) {
        String replacement = value.replace("'", "\\'");
        return Quote.quoteString(replacement);
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull String value) throws SQLException {
        statement.setString(index, value);
    }
}
