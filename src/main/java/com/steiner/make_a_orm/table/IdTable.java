package com.steiner.make_a_orm.table;

import jakarta.annotation.Nonnull;

public abstract class IdTable extends Table {
    @Nonnull
    public String idName;

    public IdTable(@Nonnull String name) {
        this(name, "id");
    }

    public IdTable(@Nonnull String name, @Nonnull String idName) {
        super(name);
        this.idName = idName;
    }
}
