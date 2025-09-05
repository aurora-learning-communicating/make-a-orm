package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.constraint.Constraint;
import jakarta.annotation.Nonnull;

public class CustomAutoIncrementConstraint extends Constraint.Suffix {
    int start;

    public CustomAutoIncrementConstraint(int start) {
        this.start = start;
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "auto_increment = %s".formatted(this.start);
    }
}
