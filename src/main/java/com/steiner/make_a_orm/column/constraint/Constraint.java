package com.steiner.make_a_orm.column.constraint;

import com.steiner.make_a_orm.IToSQL;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class Constraint implements IToSQL {
    @Nonnull
    public abstract ConstraintType type();

    public static abstract class Inline extends Constraint {
        @Nonnull
        @Override
        public ConstraintType type() {
            return ConstraintType.Inline;
        }
    }

    public static abstract class StandAlone extends Constraint {
        @Nullable
        public String constraintName;

        public StandAlone(@Nullable String constraintName) {
            this.constraintName = constraintName;
        }

        @Nonnull
        @Override
        public ConstraintType type() {
            return ConstraintType.StandAlone;
        }
    }

    public static abstract class Suffix extends Constraint {
        @Nonnull
        @Override
        public ConstraintType type() {
            return ConstraintType.Suffix;
        }
    }
}
