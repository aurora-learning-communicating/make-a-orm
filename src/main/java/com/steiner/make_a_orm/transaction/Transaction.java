package com.steiner.make_a_orm.transaction;

import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.exception.SQLInitializeException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class Transaction {
    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public static void transaction(@Nonnull Database database, @Nonnull Runnable block) {
        @Nullable Connection connection = database.connection;
        if (connection == null) {
            throw new SQLInitializeException("missing connection");
        }

        currentConnection.set(connection);

        @Nullable Savepoint savepoint = null;

        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            savepoint = connection.setSavepoint();
            block.run();
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException exp) {
                exp.printStackTrace(System.out);
                throw new SQLRuntimeException("rollback error").cause(exp);
            }
        } finally {
            currentConnection.remove();
        }
    }

    @Nonnull
    public static Connection currentConnection() {
        @Nullable Connection connection = currentConnection.get();
        if (connection == null) {
            throw new SQLInitializeException("missing connection");
        }

        return connection;
    }
}
