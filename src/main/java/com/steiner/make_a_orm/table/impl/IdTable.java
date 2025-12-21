package com.steiner.make_a_orm.table.impl;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.util.Objects;

public abstract class IdTable<T, E extends Column<T>> extends Table {
    @Nonnull
    protected String idName;

    public IdTable(@Nonnull String name, @Nonnull String idName, @Nonnull Dialect dialect) {
        super(name, dialect);
        this.idName = idName;
    }

    public IdTable(@Nonnull String name, @Nonnull Dialect dialect) {
        this(name, "id", dialect);
    }

    @Nonnull
    public E id() {
        PrimaryKey primaryKeyOriginal = Objects.requireNonNull(this.primaryKey());
        PrimaryKey.Single<E> primaryKey = (PrimaryKey.Single<E>) primaryKeyOriginal;
        return primaryKey.fromColumn;
    }
}
