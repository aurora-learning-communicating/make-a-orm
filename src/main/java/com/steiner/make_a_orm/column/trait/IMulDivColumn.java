package com.steiner.make_a_orm.column.trait;

import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

public interface IMulDivColumn<T, E extends Column<T>> {
    @Nonnull
    E self();
}
