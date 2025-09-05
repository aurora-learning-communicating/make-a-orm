package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import jakarta.annotation.Nonnull;

public abstract class Table implements IToSQL {
    @Nonnull
    public String name;


    @Override
    public @Nonnull String toSQL() {
        return "";
    }
}
