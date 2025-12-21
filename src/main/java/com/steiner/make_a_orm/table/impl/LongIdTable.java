package com.steiner.make_a_orm.table.impl;

import com.steiner.make_a_orm.column.number.BigIntColumn;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

public abstract class LongIdTable extends IdTable<Long, BigIntColumn> {
    public LongIdTable(@Nonnull String name, @Nonnull String idName, @Nonnull Dialect dialect) {
        super(name, idName, dialect);
    }

    public LongIdTable(@Nonnull String name, @Nonnull Dialect dialect) {
        super(name, dialect);
    }

    @Nullable
    @Override
    public PrimaryKey.Single<BigIntColumn> primaryKey() {
        return new PrimaryKey.Single<>(bigint(idName)).autoIncrement();
    }
}
