package com.steiner.make_a_orm.column.numeric;

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

public class TinyIntColumn extends NumberColumn<Byte>
        implements
        IEqual<Byte, TinyIntColumn>,
        ICompare<Byte, TinyIntColumn>,
        IBetween<Byte, TinyIntColumn>,
        IInList<Byte, TinyIntColumn>,
        INullOrNot<Byte, TinyIntColumn>,
        ISummary<Byte, Long> {
    public TinyIntColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        if (isAutoIncrement) {
            return "smallserial";
        } else {
            return "tinyint";
        }
    }

    @Override
    public int sqlType() {
        return Types.TINYINT;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Byte value) throws SQLException {
        statement.setByte(index, value);
    }

    @Override
    public @Nullable Byte read(@NotNull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Byte result -> result;
            case Number result -> result.byteValue();
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public TinyIntColumn self() {
        return this;
    }

    @Nonnull
    @Override
    public Summary<Byte, Long> sum() {
        return new Summary<Byte, Long>(this) {
            @Nullable
            @Override
            public Long read(@Nonnull ResultSet resultSet) throws SQLException {
                return resultSet.getLong(alias());
            }
        };
    }
}
