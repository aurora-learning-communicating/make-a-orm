package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.time.LocalTime;

public class TimeColumn extends Column<java.time.LocalTime>
        implements
        IEqual<LocalTime, TimeColumn>,
        ICompare<LocalTime, TimeColumn>,
        INullOrNot<LocalTime, TimeColumn>,
        IBetween<LocalTime, TimeColumn>,
        IInList<LocalTime, TimeColumn> {

    public TimeColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Override
    public @Nonnull String typeQuote(@Nonnull Dialect dialect) {
        return dialect.dataTypeProvider.timeType();
    }

    @Nonnull
    @Override
    public String format(@Nonnull LocalTime value) {
        return Quote.quoteString(value.toString());
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
