package com.steiner.make_a_orm.statement.update;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.date.DateColumn;
import com.steiner.make_a_orm.column.date.TimeColumn;
import com.steiner.make_a_orm.column.date.TimestampColumn;
import com.steiner.make_a_orm.column.numeric.NumericColumn;
import com.steiner.make_a_orm.column.string.StringColumn;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.unit.TimeUnit;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.WhereTopStatement;

import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class UpdateStatement implements IToSQL {
    @Nonnull
    Table table;
    @Nonnull
    List<Column<?>> columns;
    @Nullable
    PreparedStatement preparedStatement;
    @Nullable
    WhereTopStatement whereStatement;
    @Nonnull
    List<String> expressions;
    @Nonnull
    Connection connection;

    public UpdateStatement(@Nonnull Table table) {
        this.table = table;
        this.columns = table.columns.stream()
                .filter(column -> !(column.isPrimaryKey && column.isAutoIncrement))
                .toList();

        this.expressions = new ArrayList<>();
        this.whereStatement = null;
        this.preparedStatement = null;
        this.connection = Transaction.currentConnection();
        this.preparedStatement = null;
    }

    public <T> void set(@Nonnull Column<T> column, @Nullable T value) {
        checkSet(column, value);

        @Nullable String valuePattern = null;

        if (value == null) {
            valuePattern = "null";
        } else {
            valuePattern = column.format(value);
        }

        String pattern = "%s = %s".formatted(Quote.quoteColumn(column), valuePattern);
        this.expressions.add(pattern);
    }

    public <T extends Number & Comparable<T>, E extends NumericColumn<T>>
    void plus(@Nonnull E column, @Nonnull T value) {
        checkModify(column);

        String expression = "%s + %s".formatted(Quote.quoteColumn(column), column.format(value));
        this.expressions.add(expression);
    }

    public <T extends Number & Comparable<T>, E extends NumericColumn<T>>
    void minus(@Nonnull E column, @Nonnull T value) {
        checkModify(column);

        String expression = "%s - %s".formatted(Quote.quoteColumn(column), column.format(value));
        this.expressions.add(expression);
    }

    public <T extends Number & Comparable<T>, E extends NumericColumn<T>>
    void times(@Nonnull E column, @Nonnull T value) {
        checkModify(column);

        String expression = "%s * %s".formatted(Quote.quoteColumn(column), column.format(value));
        this.expressions.add(expression);
    }

    public <T extends Number & Comparable<T>, E extends NumericColumn<T>>
    void div(@Nonnull E column, @Nonnull T value) {
        checkModify(column);

        if (value.equals(0)) {
            throw Errors.DivByZero;
        }

        String expression = "%s / %s".formatted(Quote.quoteColumn(column), column.format(value));
        this.expressions.add(expression);
    }


    public void plus(@Nonnull DateColumn column, int amount, @Nonnull TimeUnit.Date unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void plus(@Nonnull TimeColumn column, int amount, @Nonnull TimeUnit.Time unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void plus(@Nonnull TimestampColumn column, int amount, @Nonnull TimeUnit.DateTime unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void minus(@Nonnull DateColumn column, int amount, @Nonnull TimeUnit.Date unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void minus(@Nonnull TimeColumn column, int amount, @Nonnull TimeUnit.Time unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void minus(@Nonnull TimestampColumn column, int amount, @Nonnull TimeUnit.DateTime unit) {
        checkModify(column);
        String expression = "%s + interval %s %s".formatted(Quote.quoteColumn(column), amount, unit.unit);
        this.expressions.add(withModifyNull(column, expression));
    }

    public void plus(@Nonnull StringColumn column, @Nonnull String value) {
        checkModify(column);
        String expression = "concat(%s, %s)".formatted(Quote.quoteColumn(column), column.format(value));
        this.expressions.add(withModifyNull(column, expression));
    }





    public void where(@Nonnull WhereStatement whereStatement) {
        this.whereStatement = new WhereTopStatement(whereStatement);
    }

    public void where(@Nonnull Supplier<WhereStatement> supplier) {
        this.where(supplier.get());
    }

    public void executeUpdate() {
        try {
            this.preparedStatement = connection.prepareStatement(toSQL());
            if (this.whereStatement != null) {
                this.whereStatement.write(this.preparedStatement);
            }

            this.preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw Errors.ExecuteUpdateFailed.cause(e);
        }
    }

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();

        String expressions = String.join(",\n", this.expressions);

        stringBuilder.append("update %s".formatted(Quote.quoteTable(table)))
                .append("\n")
                .append("set %s".formatted(expressions));

        if (whereStatement != null) {
            stringBuilder.append("\nwhere")
                    .append(whereStatement.toSQL())
                    .append(";\n");
        }

        return stringBuilder.toString();
    }

    private <T> void checkSet(@Nonnull Column<T> column, @Nullable T value) {
        if (!table.columns.contains(column)) {
            throw Errors.ColumnNotExists(column, table);
        }

        if (!column.isNullable && value == null) {
            throw Errors.SetNull;
        }
    }

    private <T> void checkModify(@Nonnull Column<T> column) {
        if (!table.columns.contains(column)) {
            throw Errors.ColumnNotExists(column, table);
        }
    }

    @Nonnull
    private String withModifyNull(@Nonnull Column<?> column, @Nonnull String expression) {
        if (column.isNullable) {
            String quoteColumn = Quote.quoteColumn(column);
            return "case when %s is not null then %s else %s end"
                    .formatted(quoteColumn, expression, quoteColumn);
        } else {
            return expression;
        }
    }
}
