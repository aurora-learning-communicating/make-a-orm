package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class TimeColumn extends Column<java.sql.Time>
        implements
        IEqualColumn<java.sql.Time, TimeColumn>,
        ICompareDateColumn<java.sql.Time, TimeColumn>,
        INullOrNotColumn<java.sql.Time, TimeColumn>,
        IBetweenColumn<java.sql.Time, TimeColumn>,
        IInListColumn<java.sql.Time, TimeColumn>,
        IPlusColumn<java.sql.Time, TimeColumn>,
        IMinusColumn<java.sql.Time, TimeColumn> {

    public TimeColumn(String name) {
        super(name);
    }


    @Nonnull
    @Override
    public String sqlType() {
        return "time";
    }

    @Nonnull
    @Override
    public String format(@Nonnull java.sql.Time value) {
        return "'%s'".formatted(value.toString());
    }


    @Nonnull
    @Override
    public TimeColumn self() {
        return this;
    }

    @Nullable
    @Override
    public java.sql.Time valueFromDB(@Nonnull ResultSet result) {
        try {
            return result.getTime(name);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull java.sql.Time value) {
        try {
            statement.setTime(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }
}
