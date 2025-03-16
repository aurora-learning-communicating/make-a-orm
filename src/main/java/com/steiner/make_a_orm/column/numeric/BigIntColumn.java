package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class BigIntColumn extends NumericColumn<Long>
        implements
        IAutoIncrementColumn<Long, BigIntColumn>,
        IDefaultValueColumn<Long, BigIntColumn>,
        IEqualColumn<Long, BigIntColumn>,
        IPrimaryKeyColumn<Long, BigIntColumn>,
        ICompareColumn<Long, BigIntColumn>,
        IBetweenColumn<Long, BigIntColumn>,
        INullOrNotColumn<Long, BigIntColumn>,
        IInListColumn<Long, BigIntColumn>,
        IPlusColumn<Long, BigIntColumn>,
        IMinusColumn<Long, BigIntColumn>,
        IMulDivColumn<Long, BigIntColumn> {
    public BigIntColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Long value) {
        try {
            statement.setLong(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Override
    @Nonnull
    public String sqlType() {
        return "bigint";
    }

    @Nonnull
    @Override
    public BigIntColumn self() {
        return this;
    }
}
