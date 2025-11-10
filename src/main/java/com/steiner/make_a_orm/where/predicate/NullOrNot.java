package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NullOrNot<T, E extends Column<T>> extends WherePredicate<T, E> {
    public boolean isNull;

    public NullOrNot(@Nonnull E column, boolean isNull) {
        super(false, column);
        this.isNull = isNull;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        return 0;
    }

    @Nonnull
    @Override
    public String toSQL() {
        if (isNull) {
            return "%s is null".formatted(Quote.quoteColumnName(column.name));
        } else {
            return "%s is not null".formatted(Quote.quoteColumnName(column.name));
        }
    }
}
