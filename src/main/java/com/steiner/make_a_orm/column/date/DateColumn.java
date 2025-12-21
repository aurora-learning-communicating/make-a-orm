package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.time.LocalDate;

public class DateColumn extends Column<java.time.LocalDate>
        implements
        IEqual<LocalDate, DateColumn>,
        ICompare<LocalDate, DateColumn>,
        IBetween<LocalDate, DateColumn>,
        INullOrNot<LocalDate, DateColumn>,
        IInList<LocalDate, DateColumn> {

    public DateColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull LocalDate value) {
        return Quote.quoteString(value.toString());
    }

    @Override
    @Nonnull
    public  String typeQuote(@Nonnull Dialect dialect) {
        return dialect.dataTypeProvider.dateType();
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
