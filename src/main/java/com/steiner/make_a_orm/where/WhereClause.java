package com.steiner.make_a_orm.where;

import com.steiner.make_a_orm.IToSQL;
import jakarta.annotation.Nonnull;

public class WhereClause implements IToSQL {
    @Nonnull
    @Override
    public String toSQL() {
        return "";
    }
}
