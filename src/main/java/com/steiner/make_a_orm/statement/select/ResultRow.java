package com.steiner.make_a_orm.statement.select;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.aggregate.Aggregate;
import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultRow {
    @Nonnull
    public ResultSet resultSet;

    public ResultRow(@Nonnull ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Nonnull
    public <T> T get(@Nonnull Column<T> column) {
        @Nullable T result = null;
        try {
            result = column.read(resultSet);
        } catch (SQLException exception) {
            throw Errors.ReadError(exception);
        }

        if (result == null) {
            throw Errors.GetNull;
        }

        return result;
    }

    @Nullable
    public <T> T getOrNull(@Nonnull Column<T> column) {
        /*
        if (!column.isNullable) {
            throw new SQLRuntimeException("the value of column %s won't be null".formatted(Quote.quoteColumnStandalone(column)));
        }
         */

        @Nullable T result = null;
        try {
            result = column.read(resultSet);
            return result;
        } catch (SQLException exception) {
            throw Errors.ReadError(exception);
        }
    }

    @Nonnull
    public <T> T get(@Nonnull Aggregate<T> aggregate) {
        @Nullable T result = null;

        try {
            result = aggregate.read(resultSet);
        } catch (SQLException exception) {
            throw Errors.ReadError(exception);
        }

        if (result == null) {
            throw Errors.GetNull;
        }

        return result;
    }

    @Nullable
    public <T> T getOrNull(@Nonnull Aggregate<T> aggregate) {
        @Nullable T result = null;
        try {
            result = aggregate.read(resultSet);
            return result;
        } catch (SQLException exception) {
            throw Errors.ReadError(exception);
        }
    }
}
