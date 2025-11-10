package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.predicate.NullOrNot;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import jakarta.annotation.Nonnull;

public interface INullOrNot<T, E extends Column<T>> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<T, E> isNull() {
        return new NullOrNot<>(self(), true);
    }

    @Nonnull
    default WherePredicate<T, E> isNotNull() {
        return new NullOrNot<>(self(), false);
    }
}
