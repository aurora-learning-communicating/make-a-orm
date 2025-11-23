package com.steiner.make_a_orm.column.date;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.*;
import java.time.LocalDateTime;

public class TimestampColumn extends Column<java.time.LocalDateTime>
        implements
        IEqual<java.time.LocalDateTime, TimestampColumn>,
        ICompare<java.time.LocalDateTime, TimestampColumn>,
        INullOrNot<java.time.LocalDateTime, TimestampColumn>,
        IBetween<java.time.LocalDateTime, TimestampColumn>,
        IInList<java.time.LocalDateTime, TimestampColumn> {

    public TimestampColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull LocalDateTime value) {
        return Quote.quoteString(value.toString());
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "timestamp";
    }

    @Override
    public int sqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull LocalDateTime value) throws SQLException {
        statement.setTimestamp(index, java.sql.Timestamp.valueOf(value));
    }

    @Nullable
    @Override
    public LocalDateTime read(@Nonnull ResultSet resultSet) throws SQLException {
        @Nullable Timestamp sqlTimestamp = resultSet.getTimestamp(name);
        if (sqlTimestamp != null) {
            return sqlTimestamp.toLocalDateTime();
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    public TimestampColumn self() {
        return this;
    }
}
