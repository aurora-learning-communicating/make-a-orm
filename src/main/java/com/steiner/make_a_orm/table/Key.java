package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class Key implements IToSQL {
    public static class Primary<T> extends Key {
        @Nonnull
        public Column<T> fromColumn;

        public Primary(@Nonnull Column<T> fromColumn) {
            this.fromColumn = fromColumn;
            this.fromColumn.isPrimaryKey = true;
        }

        @Nonnull
        @Override
        public String toSQL() {
            return "primary key (%s)".formatted(Quote.quoteColumnName(fromColumn.name));
        }
    }

    public static class Foreign extends Key {
        @Nullable
        String name;

        @Nonnull
        Table referenceTable;

        @Nonnull
        Column<?> referenceColumn;


    }

    public static class Composite extends Key {

    }
}
