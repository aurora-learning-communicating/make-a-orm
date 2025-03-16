package com.steiner.make_a_orm.update;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.string.StringColumn;
import com.steiner.make_a_orm.column.trait.IMulDivColumn;
import com.steiner.make_a_orm.column.trait.IPlusColumn;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.update.timeunit.DateTimeUnit;
import com.steiner.make_a_orm.update.timeunit.DateUnit;
import com.steiner.make_a_orm.update.timeunit.TimeUnit;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class UpdateStatement {
    @Nonnull
    Table table;

    @Nonnull
    List<Column<?>> columns;

    @Nullable
    PreparedStatement statement;

    @Nullable
    WhereStatement whereStatement;

    @Nonnull
    // List<SetExpression<?>> settingColumns;
    List<UpdateExpression> updateExpressions;

    public UpdateStatement(@Nonnull Table table) {
        this.table = table;
        this.columns = table.columns.stream()
                .filter(column -> !(column.isPrimaryKey && column.isAutoIncrement))
                .toList();

        this.updateExpressions = new ArrayList<>();
        this.whereStatement = null;
        this.statement = null;
    }

    public <T> void set(@Nonnull Column<T> column, @Nullable T value) {
        checkSet(column, value);

        String pattern = "`%s` = ?".formatted(column.name);
        this.updateExpressions.add(new SetExpression<>(column, value));
    }


    // plus number
    public <T extends Number, E extends Column<T> & IPlusColumn<T, E>>
    void plus(@Nonnull E column, @Nonnull T value) {
        checkExpression(column);
        String expression = "`%s` + %s".formatted(column.name, column.format(value));
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    // minus number
    public <T extends Number, E extends Column<T> & IPlusColumn<T, E>>
    void minus(@Nonnull E column, @Nonnull T value) {
        checkExpression(column);
        String expression = "`%s` - %s".formatted(column.name, column.format(value));
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    // plus/minus Date/Time/DateTime
    public <T extends java.sql.Date, E extends Column<T> & IPlusColumn<T, E>>
    void plus(@Nonnull E column, int value, @Nonnull DateUnit unit) {
        checkExpression(column);
        String expression = "`%s` + interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends java.sql.Date, E extends Column<T> & IPlusColumn<T, E>>
    void minus(@Nonnull E column, int value, @Nonnull DateUnit unit) {
        checkExpression(column);
        String expression = "`%s` - interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends java.sql.Time, E extends Column<T> & IPlusColumn<T, E>>
    void plus(@Nonnull Column<T> column, int value, @Nonnull TimeUnit unit) {
        checkExpression(column);
        String expression = "`%s` + interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends java.sql.Time, E extends Column<T> & IPlusColumn<T, E>>
    void minus(@Nonnull E column, int value, @Nonnull TimeUnit unit) {
        checkExpression(column);
        String expression = "`%s` - interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends java.sql.Timestamp, E extends Column<T> & IPlusColumn<T, E>>
    void plus(@Nonnull E column, int value, @Nonnull DateTimeUnit unit) {
        checkExpression(column);
        String expression = "`%s` + interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends java.sql.Timestamp, E extends Column<T> & IPlusColumn<T, E>>
    void minus(@Nonnull E column, int value, @Nonnull DateTimeUnit unit) {
        checkExpression(column);
        String expression = "`%s` - interval %s %s".formatted(column.name, value, unit.unit);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    // multi number
    public <T extends Number, E extends Column<T> & IMulDivColumn<T, E>>
    void multi(@Nonnull E column, int value) {
        checkExpression(column);
        String expression = "`%s` * %s".formatted(column.name, value);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends Number, E extends Column<T> & IMulDivColumn<T, E>>
    void multi(@Nonnull E column, double value) {
        checkExpression(column);
        String expression = "`%s` * %s".formatted(column.name, value);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    // div number
    public <T extends Number, E extends Column<T> & IMulDivColumn<T, E>>
    void div(@Nonnull E column, int value) {
        if (value == 0) {
            throw new SQLBuildException("cannot div 0");
        }

        checkExpression(column);
        String expression = "`%s` / %s".formatted(column.name, value);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public <T extends Number, E extends Column<T> & IMulDivColumn<T, E>>
    void div(@Nonnull E column, double value) {
        if (value == 0.0) {
            throw new SQLBuildException("cannot div 0");
        }

        checkExpression(column);
        String expression = "`%s` / %s".formatted(column.name, value);
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    // plus string
    public <T extends String, E extends Column<T> & IPlusColumn<T, E>>
    void plus(@Nonnull E column, @Nonnull T value) {
        checkExpression(column);
        String expression = "concat(`%s`, %s)".formatted(column.name, column.format(value));
        this.updateExpressions.add(new ReassignExpression(column, expression));
    }

    public void executeUpdate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("update `%s` \nset %s".formatted(table.name, updateExpressions.stream()
                .map(UpdateExpression::toSQL)
                .collect(Collectors.joining(", \n"))));

        List<? extends SetExpression<?>> setExpressions = updateExpressions.stream()
                .filter(expression -> expression.type == UpdateExpression.Type.Set)
                .map(expression -> (SetExpression<?>) expression)
                .toList();

        if (whereStatement != null) {
            stringBuilder.append("\nwhere ")
                    .append(WhereStatement.buildWhere(whereStatement))
                    .append(";\n");
        }

        String sql = stringBuilder.toString();
        Connection connection = Transaction.currentConnection();

        try {
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }

        // replace slot
        Objects.requireNonNull(statement);
        int startIndex = 1;
        for (SetExpression<?> setting: setExpressions) {
            setting.inject(statement, startIndex);
            startIndex += 1;
        }

        if (whereStatement != null) {
            List<WhereStatement> whereStatements = WhereStatement.flatten(whereStatement);

            for (WhereStatement whereStat: whereStatements) {
                whereStat.setInjectIndex(startIndex);
                startIndex = whereStat.inject(statement);
            }
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public void where(@Nonnull WhereStatement where) {
        this.whereStatement = where;
    }

    public void where(@Nonnull Supplier<WhereStatement> supplier) {
        this.whereStatement = supplier.get();
    }

    private <T> void checkSet(@Nonnull Column<T> column, @Nullable T value) {
        int index = table.columns.indexOf(column);
        if (index == -1) {
            throw new SQLBuildException("column `%s` not exist in table `%s`".formatted(column.name, table.name));
        }

        if (!column.isNullable && value == null) {
            throw new SQLBuildException("cannot set the not null column with null");
        }
    }

    private <T> void checkExpression(@Nonnull Column<T> column) {
        int index = table.columns.indexOf(column);
        if (index == -1) {
            throw new SQLBuildException("column `%s` not exist in table `%s`".formatted(column.name, table.name));
        }
    }
}
