package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.string.StringColumn;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Like<E extends StringColumn> extends WherePredicate<String, E> {
    @Nonnull
    public String pattern;

    public Like(@Nonnull E column, @Nonnull String pattern) {
        super(column);
        this.pattern = pattern;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        int index = getWriteIndex();

        column.write(statement, index, pattern);
        return index + 1;
    }

    @Nonnull
    @Override
    public String toSQL() {
        String format = "%s like %s";
        if (isInCheck) {
            return format.formatted(Quote.quoteColumnStandalone(column), column.format(pattern));
        } else {
            return format.formatted(Quote.quoteColumnStandalone(column), Quote.slot);
        }

    }
}
