package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.where.WhereTopStatement;
import jakarta.annotation.Nonnull;

public class Check implements IToSQL {
    @Nonnull
    String name;

    @Nonnull
    WhereTopStatement whereStatement;

    public Check(@Nonnull String name, @Nonnull WhereTopStatement whereStatement) {
        this.name = name;
        this.whereStatement = whereStatement;
        this.whereStatement.setInCheck();
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "constraint %s check (%s)".formatted(name, whereStatement.toSQL());
    }
}
