package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.constraint.StandAloneConstraint;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CheckConstraint extends StandAloneConstraint {
    @Nonnull
    WhereStatement whereStatement;

    public CheckConstraint(@Nonnull String name, @Nonnull Supplier<? extends WhereStatement> supplier) {
        super(name);
        this.whereStatement = supplier.get();
    }

    @NotNull
    @Override
    public String constraint() {
        return "constraint %s check (%s)".formatted(name, whereStatement.toSQL());
    }
}
