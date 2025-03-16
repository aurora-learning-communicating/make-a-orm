package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CharacterColumn extends StringColumn
        implements
        IEqualColumn<String, CharacterColumn>,
        IDefaultValueColumn<String, CharacterColumn>,
        IInListColumn<String, CharacterColumn>,
        ILikeColumn<CharacterColumn>,
        INullOrNotColumn<String, CharacterColumn>,
        IPlusColumn<String, StringColumn> {
    public int length;

    public CharacterColumn(@Nonnull String name, int length) {
        super(name);
        this.length = length;
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
        return "char(%s)".formatted(length);
    }

    @Nonnull
    @Override
    public CharacterColumn self() {
        return this;
    }
}
