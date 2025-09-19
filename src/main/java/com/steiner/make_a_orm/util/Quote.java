package com.steiner.make_a_orm.util;

import jakarta.annotation.Nonnull;

public class Quote {
    public static final String formatLiteral = "'%s'";
    public static final String formatIdentify = "`%s`";
    public static final String autoIncrement = "autoincrement";
    public static final String slot = "?";

    @Nonnull
    public static String quoteTableName(@Nonnull String tableName) {
        return formatIdentify.formatted(tableName);
    }

    @Nonnull
    public static String quoteColumnName(@Nonnull String columnName) {
        return formatIdentify.formatted(columnName);
    }

    @Nonnull
    public static String quoteKeyName(@Nonnull String name) {
        return name;
    }


    @Nonnull
    public static String quoteString(@Nonnull String string) {
        return formatLiteral.formatted(string);
    }
}
