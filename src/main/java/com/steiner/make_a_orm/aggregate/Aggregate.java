package com.steiner.make_a_orm.aggregate;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Aggregate<R> implements IToSQL {
    @Nonnull
    protected Column<?> column;

    public Aggregate(@Nonnull Column<?> column) {
        this.column = column;
    }

    @Nonnull
    public abstract String alias();

    @Nullable
    public abstract R read(@Nonnull ResultSet resultSet) throws SQLException;
}
