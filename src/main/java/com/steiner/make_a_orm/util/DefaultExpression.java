package com.steiner.make_a_orm.util;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

public abstract class DefaultExpression implements IToSQL {
    public static DefaultExpression Null = new DefaultExpression() {
        @Nonnull
        @Override
        public String toSQL() {
            return "default null";
        }
    };

    public static class Literal<T> extends DefaultExpression {
        @Nonnull
        T value;

        @Nonnull
        String literal;

        public Literal(@Nonnull T value, @Nonnull String literal) {
            this.value = value;
            this.literal = literal;
        }

        @Nonnull
        @Override
        public String toSQL() {
            return "default %s".formatted(literal);
        }
    }

    public static abstract class Expression extends DefaultExpression {

        public static Expression True = new Expression() {
            @Nonnull
            @Override
            public String toSQL() {
                return "default true";
            }
        };

        public static Expression False = new Expression() {
            @Nonnull
            @Override
            public String toSQL() {
                return "default false";
            }
        };

        public static Expression Now = new Expression() {
            @Nonnull
            @Override
            public String toSQL() {
                return "default now()";
            }
        };

        public static Expression CurrentTimestamp = new Expression() {
            @Nonnull
            @Override
            public String toSQL() {
                return "default current_timestamp";
            }
        };
    }
}
