package com.steiner.make_a_orm.key;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.number.NumberColumn;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.Types;
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
                throw Errors.PrimaryNotNull;
            }

            this.fromColumn.isPrimaryKey = true;
        }

        public <N extends NumberColumn<?>> Single<N> autoIncrement() {
            int sqlType = fromColumn.sqlType();

            if (sqlType != Types.INTEGER && sqlType != Types.BIGINT) {
                throw Errors.MismatchedAutoIncrementType(sqlType);
            }

            fromColumn.isAutoIncrement = true;
            return (Single<N>) this;
        }

        @Nonnull
        @Override
        public String toSQL() {
            return "primary key (%s)".formatted(Quote.quoteColumn(fromColumn));
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

            Table table = columns.getFirst().fromTable;
            boolean flag = columns.stream().skip(1).allMatch(column -> column.fromTable.equals(table));
            if (!flag) {
                throw Errors.CompositeKeyDifferent;
            }

            Optional<Column<?>> nullableColumn = columns.stream().filter(column -> column.isNullable)
                    .findFirst();

            nullableColumn.ifPresent(column -> {
                throw Errors.PrimaryNotNull;
            });
        }

        @Nonnull
        @Override
        public String toSQL() {
            String names = columns.stream().map(Quote::quoteColumn).collect(Collectors.joining(","));
            return "primary key (%s)".formatted(names);
        }
    }
}
