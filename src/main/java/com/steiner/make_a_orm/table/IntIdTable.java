package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;

public abstract class IntIdTable extends IdTable {
    @Nonnull
    PrimaryKey.Single<Integer> id;

    public IntIdTable(@Nonnull String name, @Nonnull String idName) {
        super(name, idName);
        id = primaryKey(integer(idName)).autoIncrement();
    }
}
