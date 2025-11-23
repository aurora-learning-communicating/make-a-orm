package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.time.LocalDate;

public class DateColumn extends Column<java.time.LocalDate>
        implements
        IEqual<java.time.LocalDate, DateColumn>,
        ICompare<java.time.LocalDate, DateColumn>,
        IBetween<java.time.LocalDate, DateColumn>,
        INullOrNot<java.time.LocalDate, DateColumn>,
        IInList<java.time.LocalDate, DateColumn> {

    public DateColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull LocalDate value) {
        return Quote.quoteString(value.toString());
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "date";
    }

    @Override
    public int sqlType() {
        return Types.DATE;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull LocalDate value) throws SQLException {
        statement.setDate(index, java.sql.Date.valueOf(value));
    }

    @Nullable
    @Override
    public LocalDate read(@Nonnull ResultSet resultSet) throws SQLException {
        @Nullable java.sql.Date sqlDate = resultSet.getDate(name);
        if (sqlDate != null) {
            return sqlDate.toLocalDate();
        } else {
            return null;
        }
    }

    @Override
    @Nonnull
    public DateColumn self() {
        return this;
    }
}
