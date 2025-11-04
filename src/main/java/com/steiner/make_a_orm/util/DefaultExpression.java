package com.steiner.make_a_orm.util;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public abstract class DefaultExpression implements IToSQL {
    public abstract void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException;

    public static DefaultExpression Null = new DefaultExpression() {
        @Override
        public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
            statement.setObject(index, null);
        }

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

        public Literal(@Nonnull T value, @Nonnull Column<T> column) {
            this.value = value;
            this.literal = column.format(value);
        }

        @Nonnull
        @Override
        public String toSQL() {
            return "default %s".formatted(literal);
        }


        @Override
        public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
            statement.setObject(index, value);
        }
    }

    public static abstract class Expression extends DefaultExpression {

        public static Expression True = new Expression() {
            @Override
            public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
                statement.setBoolean(index, true);
            }

            @Nonnull
            @Override
            public String toSQL() {
                return "default true";
            }
        };

        public static Expression False = new Expression() {
            @Override
            public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
                statement.setBoolean(index, false);
            }

            @Nonnull
            @Override
            public String toSQL() {
                return "default false";
            }
        };

        public static Expression Now = new Expression() {
            @Override
            public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
                // STUB: attention here, might be error
                statement.setObject(index, new Date(), column.sqlType());
            }

            @Nonnull
            @Override
            public String toSQL() {
                return "default now()";
            }
        };

        public static Expression CurrentTimestamp = new Expression() {
            @Override
            public void writeIntoStatement(@Nonnull Column<?> column, @Nonnull PreparedStatement statement, int index) throws SQLException {
                statement.setObject(index, new Date(), column.sqlType());
            }

            @Nonnull
            @Override
            public String toSQL() {
                return "default current_timestamp";
            }
        };
    }
}
