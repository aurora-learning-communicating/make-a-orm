package com.steiner.make_a_orm.util;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.unit.TimeUnit;
import jakarta.annotation.Nonnull;

public class Quote {
    public static final String literalFormatter = "'%s'";
    public static final String tableFormatter = "\"%s\"";
    public static final String columnFormatter = "\"%s\"";
    public static final String autoIncrement = "auto_increment";
    public static final String slot = "?";

    @Nonnull
    public static String quoteTable(@Nonnull Table table) {
        return tableFormatter.formatted(table.name);
    }

    @Nonnull
    public static String quoteColumnStandalone(@Nonnull Column<?> column) {
        return "%s".formatted(quoteTable(column.fromTable)) +
                "." +
                "%s".formatted(columnFormatter.formatted(column.name));
    }

    @Nonnull
    public static String quoteColumn(@Nonnull Column<?> column) {
        return "%s".formatted(columnFormatter.formatted(column.name));
    }

    @Nonnull
    public static String quoteString(@Nonnull String string) {
        return literalFormatter.formatted(string);
    }

    @Nonnull
    public static String quoteInterval(int amount, @Nonnull TimeUnit.Date date) {
        return quoteString("%s %s".formatted(amount, date.unit));
    }

    @Nonnull
    public static String quoteInterval(int amount, @Nonnull TimeUnit.DateTime dateTime) {
        return quoteString("%s %s".formatted(amount, dateTime.unit));
    }

    @Nonnull
    public static String quoteInterval(int amount, @Nonnull TimeUnit.Time time) {
        return quoteString("%s %s".formatted(amount, time.unit));
    }
}
