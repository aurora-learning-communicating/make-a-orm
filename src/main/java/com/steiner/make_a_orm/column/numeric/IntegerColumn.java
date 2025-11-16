package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.trait.predicate.ICompare;
import com.steiner.make_a_orm.column.trait.predicate.IEqual;
import com.steiner.make_a_orm.column.trait.predicate.IInList;
import com.steiner.make_a_orm.column.trait.predicate.INullOrNot;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerColumn extends NumericColumn<Integer>
        implements
        IEqual<Integer, IntegerColumn>,
        ICompare<Integer, IntegerColumn>,
        IInList<Integer, IntegerColumn>,
        INullOrNot<Integer, IntegerColumn> {

    public IntegerColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "int";
    }

    @Override
    public int sqlType() {
        return Types.INTEGER;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Integer value) throws SQLException {
        statement.setInt(index, value);
    }

    @Override
    public @Nullable Integer read(@NotNull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Integer result -> result;
            case Number number -> number.intValue();
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public IntegerColumn self() {
        return this;
    }
}
