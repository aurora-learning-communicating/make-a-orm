package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DateColumn extends Column<java.sql.Date>
        implements
        IEqualColumn<java.sql.Date, DateColumn>,
        ICompareDateColumn<java.sql.Date, DateColumn>,
        INullOrNotColumn<java.sql.Date, DateColumn>,
        IBetweenColumn<java.sql.Date, DateColumn>,
        IInListColumn<java.sql.Date, DateColumn>,
        IPlusColumn<java.sql.Date, DateColumn>,
        IMinusColumn<java.sql.Date, DateColumn> {
    public DateColumn(String name) {
        super(name);
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "date";
    }

    @Nonnull
    @Override
    public String format(@Nonnull Date value) {
        return "'%s'".formatted(value.toString());
    }

    @Nonnull
    @Override
    public DateColumn self() {
        return this;
    }

    @Nullable
    @Override
    public java.sql.Date valueFromDB(@Nonnull ResultSet result) {
        try {
            return result.getDate(name);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull java.sql.Date value) {
        try {
            statement.setDate(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }
}
