package com.steiner.make_a_orm.insert;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLInsertException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.select.ResultRow;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.utils.GlobalLogger;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InsertStatement {
    @Nonnull
    public Table intoTable;

    @Nonnull
    public List<Column<?>> columns;

    @Nullable
    public String insertPattern;

    @Nullable
    public PreparedStatement statement;

    @Nonnull
    public List<String> listOfColumnString;

    @Nonnull
    public ArrayList<Boolean> bits;

    @Nonnull
    public Map<Column<?>, Integer> indexMap;

    @Nonnull
    public Connection connection;

    public InsertStatement(@Nonnull Table intoTable) {
        this.intoTable = intoTable;
        this.columns = intoTable.columns.stream()
                .filter(column -> !(column.isPrimaryKey && column.isAutoIncrement))
                .toList();

        this.connection = Transaction.currentConnection();
        this.listOfColumnString = columns.stream()
                .map(column -> "`%s`".formatted(column.name))
                .toList();

        this.statement = null;
        this.insertPattern = null;
        this.bits = new ArrayList<>(
                Stream.iterate(false, value -> false)
                        .limit((long) listOfColumnString.size())
                        .toList()
        );

        this.indexMap = new HashMap<>();
        for (int index = 0; index < columns.size(); index += 1) {
            this.indexMap.put(columns.get(index), index + 1);
        }

        Collections.fill(bits, false);
    }

    public <T> void set(Column<T> column, @Nullable T value) {
        Optional.ofNullable(indexMap.get(column))
                .ifPresentOrElse(index -> {
                    if (column.isPrimaryKey && column.isAutoIncrement) {
                        throw new SQLBuildException("you can't set the value to primary key auto_increment column `%s`".formatted(column.name));
                    }

                    bits.set(index - 1, true);
                    if (value == null) {
                        try {
                            statement.setObject(index, null);
                        } catch (SQLException e) {
                            throw new SQLInsertException("insert error", e);
                        }
                    } else {
                        column.inject(statement, index, value);
                    }

                }, () -> {
                    throw new SQLBuildException("column `%s` not exist in table `%s`".formatted(column.name, intoTable.name));
                });

    }

    public void executeInsert() {
        String tableName = intoTable.name;
        String columnString = String.join(", ", listOfColumnString);
        String valueString = listOfColumnString.stream().map(value -> "?").collect(Collectors.joining(", "));
        this.insertPattern = "insert into `%s`(%s) values(%s);"
                .formatted(tableName, columnString, valueString);

        try {
            statement = connection.prepareStatement(insertPattern);
            Objects.requireNonNull(statement);

            // process missing value
            int endIndex = columns.size() - 1;
            for (int index = 0; index <= endIndex; index += 1) {
                Column<?> column = columns.get(index);
                boolean bit = bits.get(index);

                if (!bit) {
                    if (column.hasDefault()) {
                        column.injectDefault(statement, index + 1);
                    } else {
                        throw new SQLInsertException("there is no default value set in the `%s`".formatted(column.name));
                    }
                }
            }

            statement.execute();
        } catch (SQLException e) {
            throw new SQLRuntimeException("create statement failed", e);
        }
    }

    public ResultRow executeInsertReturning(Column<?>... returningColumns) {
        boolean allMatch = Arrays.stream(returningColumns).allMatch(column -> column.getFromTable().equals(this.intoTable));
        if (!allMatch) {
            throw new SQLBuildException("有些字段不属于这个表: %s".formatted(this.intoTable.name));
        }

        String tableName = intoTable.name;
        String columnString = String.join(", ", listOfColumnString);
        String valueString = listOfColumnString.stream().map(value -> "?").collect(Collectors.joining(", "));
        String returning = Arrays.stream(returningColumns).map(column -> "`%s`".formatted(column.name)).collect(Collectors.joining(", "));
        this.insertPattern = "insert into `%s`(%s) values(%s) returning %s;"
                .formatted(tableName, columnString, valueString, returning);

        try {
            // statement = connection.prepareStatement(insertPattern, PreparedStatement.RETURN_GENERATED_KEYS);
            statement = connection.prepareStatement(insertPattern);
            Objects.requireNonNull(statement);

            // process missing value
            int endIndex = columns.size() - 1;
            for (int index = 0; index <= endIndex; index += 1) {
                Column<?> column = columns.get(index);
                boolean bit = bits.get(index);

                if (!bit) {
                    if (column.hasDefault()) {
                        column.injectDefault(statement, index + 1);
                    } else {
                        throw new SQLInsertException("there is no default value set in the `%s`".formatted(column.name));
                    }
                }
            }

            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                throw new SQLRuntimeException("result set calling next() failed");
            }

            return new ResultRow(resultSet);
        } catch (SQLException e) {
            throw new SQLRuntimeException("create statement failed", e);
        }
    }
}
