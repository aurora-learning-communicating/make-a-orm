package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.constraint.Constraint;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

public class PrimaryKeyConstraint extends Constraint.StandAlone {
    @Nonnull
    Column<?> column;

    public PrimaryKeyConstraint(@Nonnull Column<?> column) {
        super(null);
        this.column = column;
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "primary key (%s)".formatted(Quote.quoteColumnName(column.name));
    }
}
