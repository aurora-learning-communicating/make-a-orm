package com.steiner.make_a_orm.column.trait.aggregate;

import com.steiner.make_a_orm.aggregate.Summary;
import jakarta.annotation.Nonnull;

public interface ISummary<T extends Number & Comparable<T>, R extends Number & Comparable<R>> {
    @Nonnull
    Summary<T, R> sum();
}
