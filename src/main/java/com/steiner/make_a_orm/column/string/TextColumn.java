package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TextColumn extends StringColumn
        implements
        IDefaultValueColumn<String, TextColumn>,
        INullOrNotColumn<String, TextColumn>,
        ILikeColumn<TextColumn>,
        IInListColumn<String, TextColumn>,
        IPlusColumn<String, TextColumn> {
    public TextColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull String value) {
        try {
            statement.setString(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "text";
    }

    @Nonnull
    @Override
    public TextColumn self() {
        return this;
    }
}
