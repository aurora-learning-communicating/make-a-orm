package com.steiner.make_a_orm.table.impl;

import com.steiner.make_a_orm.column.numeric.IntegerColumn;
import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

public abstract class IntIdTable extends IdTable<Integer, IntegerColumn> {
    public IntIdTable(@Nonnull String name, @Nonnull String idName) {
        super(name, idName);
    }

    public IntIdTable(@Nonnull String name) {
        super(name);
    }

    @Nullable
    @Override
    public PrimaryKey.Single<IntegerColumn> primaryKey() {
        return new PrimaryKey.Single<>(integer(idName)).autoIncrement();
    }
}
