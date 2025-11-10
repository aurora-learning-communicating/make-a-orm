package com.steiner.make_a_orm.column.trait.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.predicate.Compare;
import com.steiner.make_a_orm.where.predicate.WherePredicate;
import com.steiner.make_a_orm.where.operator.Comparator;
import jakarta.annotation.Nonnull;

public interface ICompare<T extends Comparable<T>, E extends Column<T>> {
    @Nonnull
    E self();

    @Nonnull
    default WherePredicate<T, E> greater(@Nonnull T value) {
        return new Compare<>(self(), Comparator.Greater, value);
    }

    @Nonnull
    default WherePredicate<T, E> greaterEq(@Nonnull T value) {
        return new Compare<>(self(), Comparator.GreaterEq, value);
    }

    @Nonnull
    default WherePredicate<T, E> less(@Nonnull T value) {
        return new Compare<>(self(), Comparator.Less, value);
    }

    @Nonnull
    default WherePredicate<T, E> lessEq(@Nonnull T value) {
        return new Compare<>(self(), Comparator.LessEq, value);
    }

    // TODO: compare with column
}
