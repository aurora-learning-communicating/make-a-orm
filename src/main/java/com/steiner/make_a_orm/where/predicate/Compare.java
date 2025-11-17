package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.operator.Comparator;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Compare<T extends Comparable<T>, E extends Column<T>> extends WherePredicate<T, E> {
    @Nonnull
    public Comparator comparator;
    @Nonnull
    public T literal;

    public Compare(@Nonnull E column, @Nonnull Comparator comparator, @Nonnull T literal) {
        super(column);
        this.comparator = comparator;
        this.literal = literal;
    }

    @Override
    public int writeReturning(@Nonnull PreparedStatement statement) throws SQLException {
        int index = getWriteIndex();

        column.write(statement, index, literal);
        return index + 1;
    }

    @Nonnull
    @Override
    public String toSQL() {
        String format = "%s %s %s";

        if (isInCheck) {
            return format.formatted(Quote.quoteColumnStandalone(column), comparator.sign, column.format(literal));
        } else {
            return format.formatted(Quote.quoteColumnStandalone(column), comparator.sign, Quote.slot);
        }
    }
}
