package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;

import java.util.Objects;

public abstract class IdTable<T, E extends Column<T>> extends Table {
    @Nonnull
    public String idName;

    public IdTable(@Nonnull String name) {
        this(name, "id");
    }

    public IdTable(@Nonnull String name, @Nonnull String idName) {
        super(name);
        this.idName = idName;
    }

    @Nonnull
    public E id() {
        PrimaryKey primaryKeyOriginal = Objects.requireNonNull(this.primaryKey());
        PrimaryKey.Single<E> primaryKey = (PrimaryKey.Single<E>) primaryKeyOriginal;
        return primaryKey.fromColumn;
    }
}
