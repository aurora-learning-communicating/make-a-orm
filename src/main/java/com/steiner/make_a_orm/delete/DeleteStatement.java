package com.steiner.make_a_orm.delete;

import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class DeleteStatement {
    @Nonnull
    Table table;

    @Nullable
    WhereStatement whereStatement;

    @Nullable
    PreparedStatement statement;

    public DeleteStatement(@Nonnull Table table) {
        this.table = table;
        this.whereStatement = null;
        this.statement = null;
    }

    public void where(@Nonnull WhereStatement whereStatement) {
        this.whereStatement = whereStatement;
    }

    public void where(@Nonnull Supplier<WhereStatement> supplier) {
        this.whereStatement = supplier.get();
    }

    public void executeDelete() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from `%s`".formatted(table.name));

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

}
