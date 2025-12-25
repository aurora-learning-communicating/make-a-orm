package com.steiner.make_a_orm;

import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class SchemaUtils {
    // 1. create(Table...)
    public static void create(@Nonnull Table... tables) {
        Connection connection = Transaction.currentConnection();

        try {
            Statement statement = connection.createStatement();
            List<String> sqls = Arrays.stream(tables)
                    .map(SchemaUtils::createStatement)
                    .toList();

            for (String sql: sqls) {
                statement.execute(sql);
            }

        } catch (SQLException exception) {
            throw Errors.CreateTableError.cause(exception);
        }
    }

    // 2. createStatements(Table)
    @Nonnull
    public static String createStatement(@Nonnull Table table) {
        return table.toSQL();
    }

    // 3. sortTablesWithReference(ArrayList<Table>)
    // 4. using graph, traverse by its depth, and sort -> List<Table>

    // 5. drop
    public static void drop(@Nonnull Table... tables) {
        Connection connection = Transaction.currentConnection();

        try {
            Statement statement = connection.createStatement();
            List<String> tableNames = Arrays.stream(tables)
                    .map(Quote::quoteTable)
                    .toList();

            List<String> sqls = tableNames.stream()
                    .map("drop table if exists %s"::formatted)
                    .toList();

            for (String sql: sqls) {
                statement.execute(sql);
            }
        } catch (SQLException exception) {
            throw Errors.DropTableError.cause(exception);
        }
    }
}
