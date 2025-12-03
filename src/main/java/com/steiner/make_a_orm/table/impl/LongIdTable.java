package com.steiner.make_a_orm.table.impl;

import com.steiner.make_a_orm.column.numeric.BigIntColumn;
import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

public abstract class LongIdTable extends IdTable<Long, BigIntColumn> {
    public LongIdTable(@Nonnull String name, @Nonnull String idName) {
        super(name, idName);
    }

    public LongIdTable(@Nonnull String name) {
        super(name);
    }

    @Nullable
    @Override
    public PrimaryKey.Single<BigIntColumn> primaryKey() {
        return new PrimaryKey.Single<>(bigint(idName)).autoIncrement();
    }
}
