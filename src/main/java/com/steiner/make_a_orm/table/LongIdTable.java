package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.key.PrimaryKey;
import jakarta.annotation.Nonnull;

public abstract class LongIdTable extends IdTable {
    @Nonnull
    PrimaryKey.Single<Long> id;

    public LongIdTable(@Nonnull String name, @Nonnull String idName) {
        super(name, idName);
        id = primaryKey(bigint(idName)).autoIncrement();
    }
}
