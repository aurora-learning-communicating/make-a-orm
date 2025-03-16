package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TinyIntColumn extends NumericColumn<Byte>
        implements
        IEqualColumn<Byte, TinyIntColumn>,
        IDefaultValueColumn<Byte, TinyIntColumn>,
        IAutoIncrementColumn<Byte, TinyIntColumn>,
        ICompareColumn<Byte, TinyIntColumn>,
        IBetweenColumn<Byte, TinyIntColumn>,
        IInListColumn<Byte, TinyIntColumn>,
        INullOrNotColumn<Byte, TinyIntColumn> {
    public TinyIntColumn(@Nonnull String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Byte value) {
        try {
            statement.setByte(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "tinyint";
    }

    @Nonnull
    @Override
    public TinyIntColumn self() {
        return this;
    }
}
