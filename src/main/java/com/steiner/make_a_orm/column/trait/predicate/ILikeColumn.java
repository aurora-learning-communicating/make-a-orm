package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.string.StringColumn;
import com.steiner.make_a_orm.where.predicate.Like;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import jakarta.annotation.Nonnull;

public interface ILikeColumn<E extends StringColumn> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<String, E> like(@Nonnull String pattern) {
        return new Like<>(self(), pattern);
    }
}
