package com.steiner.make_a_orm.aggregate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Average<T extends Number & Comparable<T>> extends Aggregate<BigDecimal> {
    @Nullable
    public Integer scale;

    // primary constructor
    public Average(@Nonnull Column<T> column, @Nullable Integer scale) {
        super(column);
        this.scale = scale;
    }

    public Average(@Nonnull Column<T> column) {
        this(column, null);
    }


    @Nonnull
    @Override
    public String alias() {
        return Quote.quoteAggregate("avg", column);
    }

    @Nullable
    @Override
    public BigDecimal read(@Nonnull ResultSet resultSet) throws SQLException {
        return resultSet.getBigDecimal(alias());
    }

    @Nonnull
    @Override
    public String toSQL() {
        String columnName = "avg(%s)".formatted(Quote.quoteColumnStandalone(column));
        String alias = alias();

        if (scale == null) {
            return "%s as %s".formatted(columnName, alias);
        } else {
            return "round(%s, %s) as %s".formatted(columnName, scale, alias);
        }
    }
}
