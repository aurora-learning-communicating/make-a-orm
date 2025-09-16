package com.steiner.make_a_orm.column;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.DefaultExpression;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Column<T> implements IToSQL {
    @Nullable
    private Table fromTable; // late
    @Nonnull
    public String name;

    // attribute
    public boolean isPrimaryKey;
    public boolean isAutoIncrement;
    public boolean isNullable;
    public boolean isUnique;

    @Nullable
    public DefaultExpression defaultExpression;

    public Column(@Nonnull String name) {
        this.name = name;
        this.fromTable = null;
        this.isPrimaryKey = false;
        this.isAutoIncrement = false;
        this.defaultExpression = null;
        this.isNullable = false;
        this.isUnique = false;
    }

    @Nonnull
    public Table getTable() {
        return Objects.requireNonNull(fromTable);
    }

    public void setTable(@Nonnull Table table) {
        this.fromTable = table;
    }

    // TODO: nullable
    @Nonnull
    public Column<T> nullable() {
        this.isNullable = true;
        this.defaultExpression = DefaultExpression.Null;
        return this;
    }

    // TODO: unique
    @Nonnull
    public Column<T> uniqueIndex() {
        this.isUnique = true;
        return this;
    }

    // TODO: primaryKey -> PrimaryKey
    // TODO: autoIncrement after primaryKey
    // TODO: withDefault
    @Nonnull
    public Column<T> withDefault(@Nonnull T value) {
        if (isAutoIncrement) {
            throw new SQLBuildException("cannot both set default and autoincrement", null);
        }

        Objects.requireNonNull(value);
        this.defaultExpression = new DefaultExpression.Literal<>(value, this.format(value));
        return this;
    }

    // TODO: withDefaultExpression
    @Nonnull
    public Column<T> withDefaultExpression(@Nonnull DefaultExpression.Expression expression) {
        if (isAutoIncrement) {
            throw new SQLBuildException("cannot both set default and autoincrement", null);
        }

        this.defaultExpression = expression;
        return this;
    }

    public boolean hasDefault() {
        return this.defaultExpression != null;
    }

    @Nullable
    public final T read(@Nonnull ResultSet resultSet) {
        try {
            return (T) resultSet.getObject(this.name);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            throw new SQLRuntimeException("error when read", e);
        }
    }

    public abstract void write(@Nonnull PreparedStatement statement, int index, @Nonnull T value);
    public final void writeDefault(@Nonnull PreparedStatement statement, int index) {
        if (isPrimaryKey && isAutoIncrement) {
            throw new SQLBuildException("do not set value on a primary key and autoincrement column", null);
        }

        if (!hasDefault()) {
            throw new SQLBuildException("there is no default value on the column %s".formatted(name), null);
        }


        // TODO: how about meeting when default value is null ??
        try {
            Objects.requireNonNull(this.defaultExpression);
            statement.setObject(index, this.defaultExpression.toSQL());
        } catch (SQLException e) {
            throw new SQLRuntimeException("error when `setObject`", e);
        }


    }

    @Nonnull
    public abstract String format(@Nonnull T value);

    @Nonnull
    public abstract String sqlType();

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        // 1. quote(name) type
        stringBuilder.append(Quote.quoteColumnName(name))
                .append(" ")
                .append(this.sqlType());
        // 2. constraint

        if (isAutoIncrement) {
            stringBuilder.append(" ")
                    .append(Quote.autoIncrement);
        }

        if (hasDefault()) {
            Objects.requireNonNull(defaultExpression);
            stringBuilder.append(" ")
                    .append(defaultExpression.toSQL());
        }

        if (isUnique) {
            stringBuilder.append(" ")
                    .append("unique");
        }


        return stringBuilder.toString();
    }
}
