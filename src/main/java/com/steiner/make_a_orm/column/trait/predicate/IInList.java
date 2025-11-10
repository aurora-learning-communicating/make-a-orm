package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.predicate.InList;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import jakarta.annotation.Nonnull;

import java.util.List;

public interface IInList<T, E extends Column<T>> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<T, E> inList(@Nonnull List<T> list) {
        return new InList<>(self(), list);
    }
}
