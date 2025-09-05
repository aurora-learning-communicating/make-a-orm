package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.constraint.Constraint;
import jakarta.annotation.Nonnull;

public class AutoIncrementConstraint extends Constraint.Inline {

    @Nonnull
    @Override
    public String toSQL() {
        return "auto_increment";
    }
}
