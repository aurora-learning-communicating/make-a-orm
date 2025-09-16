package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;

import java.util.function.Supplier;

public class Check implements IToSQL {
    @Nonnull
    String name;

    @Nonnull
    WhereStatement whereStatement;

    public Check(@Nonnull String name, @Nonnull WhereStatement whereStatement) {
        this.name = name;
        this.whereStatement = whereStatement;
    }

    public Check(@Nonnull String name, @Nonnull Supplier<WhereStatement> whereStatement) {
        this.name = name;
        this.whereStatement = whereStatement.get();
    }

    @Nonnull
    @Override
    public String toSQL() {

    }
}
