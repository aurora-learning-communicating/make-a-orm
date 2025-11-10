package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoubleColumn extends NumericColumn<Double>
        implements
        IEqual<Double, DoubleColumn>,
        ICompare<Double, DoubleColumn>,
        IBetween<Double, DoubleColumn>,
        IInList<Double, DoubleColumn>,
        INullOrNot<Double, DoubleColumn> {
    public DoubleColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "double";
    }

    @Override
    public int sqlType() {
        return Types.DOUBLE;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull Double value) throws SQLException {
        statement.setDouble(index, value);
    }

    @Nonnull
    @Override
    public DoubleColumn self() {
        return this;
    }
}
