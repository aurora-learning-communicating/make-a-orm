package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class StringColumn extends Column<String> {
    public StringColumn(@Nonnull String name) {
        super(name);
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
