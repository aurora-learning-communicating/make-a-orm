package com.steiner.make_a_orm.column.number;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.aggregate.Summary;
import com.steiner.make_a_orm.column.trait.aggregate.ISummary;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SmallIntColumn extends NumberColumn<Short>
        implements
        IEqual<Short, SmallIntColumn>,
        ICompare<Short, SmallIntColumn>,
        IBetween<Short, SmallIntColumn>,
        IInList<Short, SmallIntColumn>,
        INullOrNot<Short, SmallIntColumn>,
        ISummary<Short, Long> {
    public SmallIntColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        if (isAutoIncrement) {
            return "smallserial";
        } else {
            return "smallint";
        }
    }

    @Override
    public int sqlType() {
        return Types.SMALLINT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Short value) throws SQLException {
        statement.setShort(index, value);
    }

    @Override
    public @Nullable Short read(@NotNull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Short result -> result;
            case Number result -> result.shortValue();
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public SmallIntColumn self() {
        return this;
    }

    @Nonnull
    @Override
    public Summary<Short, Long> sum() {
        return new Summary<Short, Long>(this) {
            @Nullable
            @Override
            public Long read(@Nonnull ResultSet resultSet) throws SQLException {
                return resultSet.getLong(alias());
            }
        };
    }
}
