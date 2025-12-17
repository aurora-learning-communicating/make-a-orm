package com.steiner.make_a_orm.column.number;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.aggregate.Average;
import com.steiner.make_a_orm.aggregate.Maximum;
import com.steiner.make_a_orm.aggregate.Minimum;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

public abstract class NumberColumn<T extends Number & Comparable<T>> extends Column<T> {
    public NumberColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public final String format(@Nonnull T value) {
        return String.valueOf(value);
    }

    @Nonnull
    public final Average<T> avg() {
        return new Average<>(this, null);
    }

    @Nonnull
    public final Average<T> avg(int scale) {
        if (scale <= 0) {
            throw Errors.SetScaleNegative;
        }

        return new Average<>(this, scale);
    }

    @Nonnull
    public final Maximum<T> max() {
        return new Maximum<>(this);
    }

    @Nonnull
    public final Minimum<T> min() {
        return new Minimum<>(this);
    }
}
