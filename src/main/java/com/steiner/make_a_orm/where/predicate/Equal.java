package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.operator.Equality;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Equal<T, E extends Column<T>> extends WherePredicate<T, E> {
    @Nonnull
    public Equality equality;
    @Nonnull
    public T literal;

    public Equal(@Nonnull E column, @Nonnull Equality equality, @Nonnull T literal) {
        super(column);
        this.equality = equality;
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
        return format.formatted(Quote.quoteColumnName(column.name), equality.sign, Quote.slot);
    }
}
