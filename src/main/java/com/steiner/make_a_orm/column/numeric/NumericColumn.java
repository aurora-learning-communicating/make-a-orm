package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

public abstract class NumericColumn<T extends Number & Comparable<T>> extends Column<T> {
    public NumericColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public final String format(@Nonnull T value) {
        return String.valueOf(value);
    }
}
