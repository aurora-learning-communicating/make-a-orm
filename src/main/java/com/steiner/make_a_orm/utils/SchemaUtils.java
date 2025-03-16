package com.steiner.make_a_orm.utils;

import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaUtils {
    public static void create(Table... tables) {
        Connection connection = Transaction.currentConnection();

        try {
            Statement statement = connection.createStatement();

            for (Table table: tables) {
                String sql = table.toSQL();

                GlobalLogger.logger().info("execute create table: {}", sql);

                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public static void drop(Table... tables) {
        Connection connection = Transaction.currentConnection();

        try {
            Statement statement = connection.createStatement();

            for (Table table: tables) {
                String sql = "drop table if exists `%s`".formatted(table.name);

                GlobalLogger.logger().info("execute drop table: {}", sql);

                statement.execute(sql);
            }

        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }
}
