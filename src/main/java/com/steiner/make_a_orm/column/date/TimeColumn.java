package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.time.LocalTime;

public class TimeColumn extends Column<java.time.LocalTime>
        implements
        IEqual<java.time.LocalTime, TimeColumn>,
        ICompare<java.time.LocalTime, TimeColumn>,
        INullOrNot<java.time.LocalTime, TimeColumn>,
        IBetween<java.time.LocalTime, TimeColumn>,
        IInList<java.time.LocalTime, TimeColumn> {

    public TimeColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull LocalTime value) {
        return Quote.quoteString(value.toString());
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "time";
    }

    @Override
    public int sqlType() {
        return Types.TIME;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull LocalTime value) throws SQLException {
        statement.setTime(index, java.sql.Time.valueOf(value));
    }

    @Nullable
    @Override
    public LocalTime read(@Nonnull ResultSet resultSet) throws SQLException {
        @Nullable Time sqlTime = resultSet.getTime(name);
        if (sqlTime != null) {
            return sqlTime.toLocalTime();
        } else {
            return null;
        }
    }

    @Override
    public @Nonnull TimeColumn self() {
        return this;
    }
}
