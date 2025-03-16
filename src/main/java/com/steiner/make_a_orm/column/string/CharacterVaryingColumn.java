package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CharacterVaryingColumn extends StringColumn
        implements
        IEqualColumn<String, CharacterVaryingColumn>,
        IDefaultValueColumn<String, CharacterVaryingColumn>,
        IInListColumn<String, CharacterVaryingColumn>,
        ILikeColumn<CharacterVaryingColumn>,
        INullOrNotColumn<String, CharacterVaryingColumn>,
        IPlusColumn<String, CharacterVaryingColumn> {
    public int length;

    public CharacterVaryingColumn(@Nonnull String name, int length) {
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
        return "varchar(%s)".formatted(length);
    }

    @Nonnull
    @Override
    public CharacterVaryingColumn self() {
        return this;
    }
}
