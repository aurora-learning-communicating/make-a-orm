package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.predicate.Between;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import jakarta.annotation.Nonnull;

public interface IBetween<T, E extends Column<T>> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<T, E> between(@Nonnull T min, @Nonnull T max) {
        return new Between<>(self(), min, max);
    }
}
