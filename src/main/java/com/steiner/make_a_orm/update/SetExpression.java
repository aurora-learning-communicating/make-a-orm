package com.steiner.make_a_orm.update;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SetExpression<T> extends UpdateExpression {
    @Nonnull
    public Column<T> column;

    @Nullable
    public T value;

    public SetExpression(@Nonnull Column<T> column, @Nullable T value) {
        super(UpdateExpression.Type.Set);
        this.column = column;
        this.value = value;
    }

    public void inject(@Nonnull PreparedStatement statement, int index) {
        if (value == null) {
            try {
                statement.setObject(index, null);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            column.inject(statement, index, value);
        }
    }

    @Override
    public String toSQL() {
        if (value == null) {
            return "`%s` = null".formatted(column.name);
        } else {
            return "`%s` = ?".formatted(column.name);
        }
    }
}
