package com.steiner.make_a_orm.key;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.util.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class PrimaryKey extends Key {
    public static class Single<T> extends PrimaryKey {
        @Nonnull
        public Column<T> fromColumn;

        public Single(@Nonnull Column<T> fromColumn) {
            this.fromColumn = fromColumn;
            if (this.fromColumn.isNullable) {
                String errorMessage = "consider that value of primary key cannot be null, so I forbid this case when column %s can be null".formatted(Quote.quoteColumnName(fromColumn.name));
                throw new SQLBuildException(errorMessage, null);
            }

            this.fromColumn.isPrimaryKey = true;
        }

        public <N extends Number> Single<N> autoIncrement() {
            TypeReference<T> leftType = new TypeReference<>();
            TypeReference<N> rightTYpe = new TypeReference<>();

            if (!leftType.equals(rightTYpe)) {
                throw new SQLBuildException("cannot invoke autoincrement on a non-number type", null);
            }

            fromColumn.isAutoIncrement = true;
            //noinspection unchecked
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

        public Composite(@Nonnull List<Column<?>> columns) {
            this.columns = columns;

            long tableCount = columns.stream().map(column -> column.fromTable).distinct().count();
            if (tableCount != columns.size()) {
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
