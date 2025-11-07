package com.steiner.make_a_orm.key;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.numeric.NumericColumn;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class PrimaryKey extends Key {
    public static class Single<T extends Column<?>> extends PrimaryKey {
        @Nonnull
        public T fromColumn;

        public Single(@Nonnull T fromColumn) {
            this.fromColumn = fromColumn;
            if (this.fromColumn.isNullable) {
                String errorMessage = "consider that value of primary key cannot be null, so I forbid this case when column %s can be null".formatted(Quote.quoteColumnName(fromColumn.name));
                throw new SQLBuildException(errorMessage, null);
            }

            this.fromColumn.isPrimaryKey = true;
        }

        public <N extends NumericColumn<?>> Single<N> autoIncrement() {
            fromColumn.isAutoIncrement = true;
            return (Single<N>) this;
        }

        @Nonnull
        @Override
        public String toSQL() {
            return "primary key (%s)".formatted(Quote.quoteColumnName(fromColumn.name));
        }
    }

    public static class Composite extends PrimaryKey {
        @Nonnull
        List<Column<?>> columns;

        public Composite(@Nonnull Column<?> first, @Nonnull Column<?> second, Column<?>... rest) {
            List<Column<?>> columns = new ArrayList<>();
            columns.add(first);
            columns.add(second);

            columns.addAll(Arrays.asList(rest));

            this.columns = columns;

            Table table = columns.get(0).fromTable;
            boolean flag = columns.stream().skip(1).allMatch(column -> column.fromTable.equals(table));
            if (!flag) {
                throw new SQLBuildException("in composite key, all the column must from the same table", null);
            }

            Optional<Column<?>> nullableColumn = columns.stream().filter(column -> column.isNullable)
                    .findFirst();

            nullableColumn.ifPresent(column -> {
                String errorMessage = "consider that value of primary key cannot be null, so I forbid this case when column %s can be null".formatted(Quote.quoteColumnName(column.name));
                throw new SQLBuildException(errorMessage, null);
            });
        }

        @Nonnull
        @Override
        public String toSQL() {
            String names = columns.stream().map(column -> Quote.quoteColumnName(column.name)).collect(Collectors.joining(","));
            return "primary key (%s)".formatted(names);
        }
    }
}
