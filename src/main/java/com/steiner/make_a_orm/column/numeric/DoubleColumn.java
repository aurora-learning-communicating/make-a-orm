package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DoubleColumn extends NumericColumn<Double>
        implements
        IEqualColumn<Double, DoubleColumn>,
        IDefaultValueColumn<Double, DoubleColumn>,
        ICompareColumn<Double, DoubleColumn>,
        IBetweenColumn<Double, DoubleColumn>,
        IInListColumn<Double, DoubleColumn>,
        INullOrNotColumn<Double, DoubleColumn>,
        IPlusColumn<Double, DoubleColumn>,
        IMinusColumn<Double, DoubleColumn>,
        IMulDivColumn<Double, DoubleColumn> {

    public DoubleColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Double value) {
        try {
            statement.setDouble(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "double";
    }

    @Nonnull
    @Override
    public DoubleColumn self() {
        return this;
    }
}
