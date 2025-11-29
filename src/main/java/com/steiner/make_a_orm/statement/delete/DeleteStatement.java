package com.steiner.make_a_orm.statement.delete;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.WhereTopStatement;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;

public class DeleteStatement implements IToSQL {
    @Nonnull
    Table table;
    @Nullable
    WhereTopStatement whereStatement;
    @Nullable
    PreparedStatement preparedStatement;
    @Nonnull
    Connection connection;

    public DeleteStatement(@Nonnull Table table) {
        this.table = table;
        this.whereStatement = null;
        this.preparedStatement = null;
        this.connection = Transaction.currentConnection();
    }

    public void where(@Nonnull WhereStatement whereStatement) {
        this.whereStatement = new WhereTopStatement(whereStatement);
    }

    public void where(@Nonnull Supplier<WhereStatement> supplier) {
        where(supplier.get());
    }

    public void executeDelete() {
        try {
            this.preparedStatement = connection.prepareStatement(toSQL());
            if (whereStatement != null) {
                whereStatement.write(this.preparedStatement);
            }

            this.preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw Errors.ExecuteDeleteFailed.cause(exception);
        }
    }

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from %s".formatted(Quote.quoteTable(table)));

        if (whereStatement != null) {
            stringBuilder.append("\nwhere")
                    .append(whereStatement.toSQL())
                    .append(";\n");
        }

        return stringBuilder.toString();
    }
}
