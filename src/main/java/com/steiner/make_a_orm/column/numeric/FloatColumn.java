package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class FloatColumn extends NumericColumn<Float>
        implements
        IEqualColumn<Float, FloatColumn>,
        IDefaultValueColumn<Float, FloatColumn>,
        ICompareColumn<Float, FloatColumn>,
        IBetweenColumn<Float, FloatColumn>,
        IInListColumn<Float, FloatColumn>,
        INullOrNotColumn<Float, FloatColumn>,
        IPlusColumn<Float, FloatColumn>,
        IMinusColumn<Float, FloatColumn> {
    public FloatColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Float value) {
        try {
            statement.setFloat(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "float";
    }

    @Nonnull
    @Override
    public FloatColumn self() {
        return this;
    }
}
