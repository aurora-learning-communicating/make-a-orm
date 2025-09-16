package com.steiner.make_a_orm.column.trait;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Key;
import jakarta.annotation.Nonnull;

public interface IAutoIncrementTrait<T extends Number, E extends Key.Primary<T>> {
    @Nonnull
    E self();

    default E autoIncrement() {
        E primaryKey = self();
        primaryKey.fromColumn.isAutoIncrement = true;
        return primaryKey;
    }
}
