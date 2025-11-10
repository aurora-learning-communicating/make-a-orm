package com.steiner.make_a_orm.where.statement;

import com.steiner.make_a_orm.where.Errors;
import com.steiner.make_a_orm.where.operator.Logical;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereCombination extends WhereStatement {
    @Nonnull
    public Logical logical;

    @Nonnull
    public WhereStatement other;

    public WhereCombination(@Nonnull Logical logical, @Nonnull WhereStatement other) {
        super(other.writable);
        this.logical = logical;
        this.other = other;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        if (!writable) {
            throw Errors.WriteUnable;
        }

        // 这里不需要手动设置 WhereCombination 的 write index ，因为会在外部设置，无需担心 (需要测试)
        this.other.setWriteIndex(getWriteIndex());
        return other.writeReturning(statement);
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "%s %s".formatted(logical.sign, other.toSQL());
    }
}
