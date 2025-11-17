package com.steiner.make_a_orm.key;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

public class ForeignKey<T extends Column<?>> extends Key {
    @Nonnull
    String name;

    boolean isNullable;

    @Nonnull
    public T referenceColumn;

    public ForeignKey(@Nonnull String name, @Nonnull T referenceColumn) {
        this.name = name;
        this.referenceColumn = referenceColumn;
        this.isNullable = false;
    }

    public ForeignKey<T> nullable() {
        this.isNullable = true;
        return this;
    }

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name)
                .append(" ")
                .append(referenceColumn.typeQuote())
                .append(" ");

        if (isNullable) {
            stringBuilder.append("null")
                    .append(" ");
        }

        stringBuilder.append("references")
                .append(" ")
                .append(Quote.quoteTable(referenceColumn.fromTable))
                .append(" ")
                .append("(")
                .append(Quote.quoteColumnStandalone(referenceColumn))
                .append(")");

        return stringBuilder.toString();
    }
}
