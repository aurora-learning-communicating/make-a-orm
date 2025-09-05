package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.constraint.Constraint;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class DefaultValueConstraint<T> extends Constraint.Inline {
    @Nullable
    T value;

    @Nonnull
    Column<T> column;

    public DefaultValueConstraint(@Nullable T value, @Nonnull Column<T> column) {
        this.value = value;
        this.column = column;
    }

    @Nonnull
    @Override
    public String toSQL() {
        if (value == null) {
            return "default null";
        } else {
            return "default %s".formatted(column.format(value));
        }
    }
}
