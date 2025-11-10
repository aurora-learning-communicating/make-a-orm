package com.steiner.make_a_orm.where.statement;

import com.steiner.make_a_orm.where.Errors;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereNot extends WhereStatement {
    @Nonnull
    public WhereStatement statement;

    public WhereNot(@Nonnull WhereStatement statement) {
        super(statement.writable);
        this.statement = statement;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        if (!writable) {
            throw Errors.WriteUnable;
        }

        this.statement.setWriteIndex(getWriteIndex());
        return this.statement.writeReturning(statement);
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "not (%s)".formatted(statement.toSQL());
    }
}
