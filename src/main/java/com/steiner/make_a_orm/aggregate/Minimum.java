package com.steiner.make_a_orm.aggregate;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Minimum<T extends Number & Comparable<T>> extends Aggregate<T> {
    public Minimum(@Nonnull Column<T> column) {
        super(column);
    }


    @Nonnull
    @Override
    public String alias() {
        return Quote.quoteAggregate("min", column);
    }

    @Nullable
    @Override
    public T read(@Nonnull ResultSet resultSet) throws SQLException {
        Object value = resultSet.getObject(alias());
        return switch (value) {
            case null -> null;
            case Number number -> (T) number;
            default -> throw Errors.UnExpectedValueType(value);
        };
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "min(%s) as %s".formatted(Quote.quoteColumnStandalone(column), alias());
    }
}
