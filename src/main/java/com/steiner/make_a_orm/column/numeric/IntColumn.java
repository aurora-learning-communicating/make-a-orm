package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class IntColumn extends NumericColumn<Integer>
        implements
        IEqualColumn<Integer, IntColumn>,
        IDefaultValueColumn<Integer, IntColumn>,
        IAutoIncrementColumn<Integer, IntColumn>,
        IPrimaryKeyColumn<Integer, IntColumn>,
        ICompareColumn<Integer, IntColumn>,
        IBetweenColumn<Integer, IntColumn>,
        IInListColumn<Integer, IntColumn>,
        INullOrNotColumn<Integer, IntColumn>,
        IPlusColumn<Integer, IntColumn>,
        IMinusColumn<Integer, IntColumn>,
        IMulDivColumn<Integer, IntColumn> {
    public IntColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Integer value) {
        try {
            statement.setInt(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "int";
    }

    @Nonnull
    @Override
    public IntColumn self() {
        return this;
    }
}
