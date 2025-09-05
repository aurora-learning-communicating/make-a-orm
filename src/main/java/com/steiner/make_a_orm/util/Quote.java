package com.steiner.make_a_orm.util;

import jakarta.annotation.Nonnull;

public class Quote {
    @Nonnull
    public static String quoteTableName(@Nonnull String tableName) {
        return "`%s`".formatted(tableName);
    }

    @Nonnull
    public static String quoteColumnName(@Nonnull String columnName) {
        return "`%s`".formatted(columnName);
    }

    @Nonnull
    public static String quoteKeyName(@Nonnull String name) {
        return name;
    }
}
