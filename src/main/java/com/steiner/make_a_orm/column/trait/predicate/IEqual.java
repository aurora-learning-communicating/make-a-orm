package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.predicate.Equal;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import com.steiner.make_a_orm.where.operator.Equality;
import jakarta.annotation.Nonnull;

public interface IEqual<T, E extends Column<T>> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<T, E> equal(@Nonnull T value) {
        return new Equal<>(self(), Equality.Eq, value);
    }

    @Nonnull
    default WherePredicate<T, E> notEqual(@Nonnull T value) {
        return new Equal<>(self(), Equality.NotEq, value);
    }
}
