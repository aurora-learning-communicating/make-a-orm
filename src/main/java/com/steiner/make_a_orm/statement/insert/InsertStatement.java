package com.steiner.make_a_orm.statement.insert;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.statement.select.ResultRow;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.util.StreamExtension;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class InsertStatement implements IToSQL {
    @Nonnull
    public Table table;
    @Nonnull
    public List<Column<?>> sliceColumns;
    @Nonnull
    public PreparedStatement preparedStatement;
    @Nonnull
    public boolean[] insertedBits; // insert 没有指定字段时，查看这个字段有没有被设置
    @Nonnull
    public Map<Column<?>, Integer> indexMap; // 与 insertedBits 相互配合
    @Nonnull
    public Connection connection;

    @Nullable
    public Column<?>[] returningColumns;

    public InsertStatement(@Nonnull Table table, @Nullable Column<?>[] returningColumns) {
        this.returningColumns = returningColumns;
        this.table = table;
        this.sliceColumns = table.columns.stream()
                .filter(column -> !(column.isPrimaryKey && column.isAutoIncrement))
                .toList();

        this.connection = Transaction.currentConnection();
        this.insertedBits = new boolean[sliceColumns.size()];
        Arrays.fill(this.insertedBits, false);

        this.indexMap = new HashMap<>();
        for (int offset = 0; offset < sliceColumns.size(); offset += 1) {
            indexMap.put(sliceColumns.get(offset), offset + 1);
        }

        try {
            this.preparedStatement = connection.prepareStatement(toSQL());
        } catch (SQLException e) {
            throw Errors.CreateStatementFailed.cause(e);
        }
    }

    public <T> void set(@Nonnull Column<T> column, @Nullable T value) {
        @Nullable Integer index = indexMap.getOrDefault(column, null);
        if (index == null) {
            throw Errors.ColumnNotExists(column, table);
        }

        if (column.isPrimaryKey && column.isAutoIncrement) {
            throw Errors.SetOnPrimary;
        }

        this.insertedBits[index - 1] = true;

        try {
            Objects.requireNonNull(this.preparedStatement);
            if (value == null) {
                this.preparedStatement.setObject(index, null);
            } else {
                column.write(this.preparedStatement, index, value);
            }
        } catch (SQLException e) {
            throw Errors.InsertError(e);
        }

    }

    public void executeInsert() {
        try {
            Objects.requireNonNull(this.preparedStatement);

            StreamExtension.forEachIndexedThrows(this.sliceColumns, (column, index) -> {
                boolean bit = insertedBits[index];

                if (!bit) {
                    if (column.hasDefault()) {
                        column.writeDefault(this.preparedStatement, index + 1);
                    } else {
                        throw Errors.NoDefaultValueSet(column);
                    }
                }
            });

            this.preparedStatement.execute();

        } catch (SQLException e) {
            throw Errors.ExecuteInsertFailed.cause(e);
        }
    }

    @Nonnull
    public ResultRow executeInsertReturning() {
        if (this.returningColumns == null) {
            throw Errors.NoReturningColumn;
        }

        boolean allMatch = Arrays.stream(this.returningColumns)
                .allMatch(column -> column.fromTable.equals(this.table));

        if (!allMatch) {
            throw Errors.ColumnsNotInclude(this.table);
        }

        try {
            Objects.requireNonNull(this.preparedStatement);

            StreamExtension.forEachIndexedThrows(this.sliceColumns, (column, index) -> {
                boolean bit = insertedBits[index];

                if (!bit) {
                    if (column.hasDefault()) {
                        column.writeDefault(this.preparedStatement, index + 1);
                    } else {
                        throw Errors.NoDefaultValueSet(column);
                    }
                }
            });

            this.preparedStatement.execute();
            ResultSet resultSet = this.preparedStatement.getResultSet();
            resultSet.next();

            return new ResultRow(resultSet);
        } catch (SQLException e) {
          throw Errors.CreateStatementFailed.cause(e);
        }
    }

    @Nonnull
    @Override
    public String toSQL() {
        String tableName = Quote.quoteTable(table);
        List<String> listOfColumnString = sliceColumns.stream()
                .map(Quote::quoteColumn)
                .toList();
        String columnString = String.join(", ", listOfColumnString);
        String slotString = listOfColumnString.stream()
                .map(value -> Quote.slot)
                .collect(Collectors.joining(", "));

        String pattern = "insert into %s(%s) values(%s)";
        if (returningColumns == null) {
            return pattern.formatted(tableName, columnString, slotString);
        } else {
            pattern = pattern + " returning %s";
            String returnings = Arrays.stream(returningColumns).map(Quote::quoteColumnStandalone).collect(Collectors.joining(", "));
            return pattern.formatted(tableName, columnString, slotString, returnings);
        }
    }
}
