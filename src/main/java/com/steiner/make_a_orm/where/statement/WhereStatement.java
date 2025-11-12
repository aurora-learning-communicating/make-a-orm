package com.steiner.make_a_orm.where.statement;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.where.operator.Logical;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public abstract class WhereStatement implements IToSQL {
    public boolean writable;
    private int writeIndex;

    @Nullable
    public List<WhereStatement> otherStatements;

    public WhereStatement(boolean writable) {
        this.writable = writable;
        this.writeIndex = -1;
        this.otherStatements = null;
    }

    // 返回下一 ? 占位符的位置，从 1 开始
    public abstract int writeReturning(@Nonnull PreparedStatement statement) throws SQLException;


    public void setWriteIndex(int index) {
        if (!writable) {
            throw Errors.WriteUnable;
        }

        if (index <= 0) {
            throw Errors.SetIndex;
        }

        this.writeIndex = index;
    }

    public int getWriteIndex() {
        if (!writable) {
            throw Errors.WriteUnable;
        }

        if (writeIndex <= 0) {
            throw Errors.MinusIndex;
        }

        return writeIndex;
    }

    @Nonnull
    public WhereStatement and(@Nonnull WhereStatement other) {
        if (this.otherStatements == null) {
            this.otherStatements = new LinkedList<>();
        }

        this.otherStatements.add(new WhereCombination(Logical.And, other));

        return this;
    }

    @Nonnull
    public WhereStatement or(@Nonnull WhereStatement other) {
        if (this.otherStatements == null) {
            this.otherStatements = new LinkedList<>();
        }

        this.otherStatements.add(new WhereCombination(Logical.Or, other));

        return this;
    }

    @Nonnull
    public WhereStatement not() {
        return new WhereNot(this);
    }

}
