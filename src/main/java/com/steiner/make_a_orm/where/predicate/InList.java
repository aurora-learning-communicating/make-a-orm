package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class InList<T, E extends Column<T>> extends WherePredicate<T, E> {
    @Nonnull
    public List<T> list;

    public InList(@Nonnull E column, @Nonnull List<T> list) {
        super(column);
        this.list = list;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        int currentIndex = getWriteIndex();
        for (T value: list) {
            column.write(statement, currentIndex, value);
            currentIndex += 1;
        }

        return currentIndex + 1;
    }

    @Nonnull
    @Override
    public String toSQL() {
        String format = "%s in (%s)";

        if (isInCheck) {
            String listString = list.stream()
                    .map(value -> column.format(value))
                    .collect(Collectors.joining(", "));

            return format.formatted(Quote.quoteColumnStandalone(column), listString);
        } else {
            String slot = list.stream()
                    .map(value -> Quote.slot)
                    .collect(Collectors.joining(", "));

            return format.formatted(Quote.quoteColumnStandalone(column), slot);
        }
    }
}
