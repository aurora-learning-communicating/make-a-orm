package com.steiner.make_a_orm.key;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

public class ForeignKey extends Key {
    @Nonnull
    String name;

    boolean isNullable;

    @Nonnull
    public Column<?> referenceColumn;

    public ForeignKey(@Nonnull String name, @Nonnull Column<?> referenceColumn) {
        this.name = name;
        this.referenceColumn = referenceColumn;
        this.isNullable = false;
    }

    public ForeignKey nullable() {
        this.isNullable = true;
        return this;
    }

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Quote.quoteColumnName(name))
                .append(" ")
                .append(referenceColumn.typeQuote())
                .append(" ");

        if (isNullable) {
            stringBuilder.append("null")
                    .append(" ");
        }

        stringBuilder.append("reference")
                .append(" ")
                .append(Quote.quoteTableName(referenceColumn.fromTable.name))
                .append(" ")
                .append("(")
                .append(Quote.quoteColumnName(referenceColumn.name))
                .append(")");

        return stringBuilder.toString();
    }
}
