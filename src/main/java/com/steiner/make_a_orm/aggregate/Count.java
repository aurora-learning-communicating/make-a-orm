package com.steiner.make_a_orm.aggregate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Count extends Aggregate<Long> {
    public Count(@Nonnull Column<?> column) {
        super(column);
    }


    @Nonnull
    @Override
    public String toSQL() {
        return "count(%s) as %s".formatted(Quote.quoteColumnStandalone(column), alias());
    }

    @Nonnull
    @Override
    public String alias() {
        return Quote.quoteAggregate("count", column);
    }

    @Nullable
    @Override
    public Long read(@Nonnull ResultSet resultSet) throws SQLException {
        return resultSet.getLong(alias());
    }
}
