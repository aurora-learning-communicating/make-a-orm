package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class Table implements IToSQL {
    @Nonnull
    String name;

    @Nullable
    Key key;

    @Nonnull
    List<Column<?>> columns;

    @Nonnull
    List<Check> checks;


    @Override
    @Nonnull
    public String toSQL() {
        // if columns has primary key

        // if columns has foreign key
    }

    // TODO: reference

    // TODO: check(name) { block }
    public void check(@Nonnull String name, @Nonnull Supplier<WhereStatement> supplier) {
        this.checks.add(new Check(name, supplier));
    }
}
