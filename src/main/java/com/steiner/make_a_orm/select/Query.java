package com.steiner.make_a_orm.select;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.IEqualColumn;
import com.steiner.make_a_orm.column.trait.IPrimaryKeyColumn;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.utils.GlobalLogger;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Query implements Spliterator<ResultRow> {
    @Nonnull
    public Table fromTable;

    @Nonnull
    public List<Column<?>> columns;

    @Nullable
    public ResultSet resultSet;

    @Nullable
    public ResultRow resultRow;

    @Nullable
    public WhereStatement whereStatement;

    @Nullable
    public Column<?> orderBy;

    public boolean reverse;

    @Nullable
    public Long limit;

    @Nullable
    public Long offset;

    public Connection connection;

    public Query(@Nonnull Table fromTable, Column<?>... columns) {
        this.fromTable = fromTable;
        this.columns = Arrays.asList(columns);
        this.orderBy = null;
        this.reverse = false;
        this.resultSet = null;
        this.resultRow = null;
        this.whereStatement = null;
        this.limit = null;
        this.offset = null;

        this.connection = Transaction.currentConnection();
    }

    // TODO edit here with column inject
    public Query where(@Nonnull WhereStatement statement) {
        this.whereStatement = statement;
        return this;
    }

    public Query where(@Nonnull Supplier<WhereStatement> block) {
        this.whereStatement = block.get();
        return this;
    }

    public <T, E extends Column<T> & IPrimaryKeyColumn<T, E> & IEqualColumn<T, E>>
    Query whereMatchPrimaryKey(T value) {
        E primaryColumn = fromTable.primaryKeyColumn();
        this.whereStatement = primaryColumn.equal(value);
        return this;
    }

    public Query orderBy(@Nonnull Column<?> column) {
        this.orderBy = column;
        return this;
    }

    public Query orderBy(@Nonnull Column<?> column, boolean reverse) {
        this.orderBy = column;
        this.reverse = reverse;
        return this;
    }

    public Query limit(long value) {
        this.limit = value;
        return this;
    }

    public Query offset(long value) {
        this.offset = value;
        return this;
    }

    @Nonnull
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        String columnNames = columns.stream()
                .map(column -> "`%s`".formatted(column.name))
                .collect(Collectors.joining(", "));

        String mainPart = "select %s from `%s`".formatted(columnNames, fromTable.name);

        stringBuilder.append(mainPart);

        if (whereStatement != null) {
            stringBuilder.append(" ")
                    .append("where")
                    .append(" ")
                    .append(WhereStatement.buildWhere(whereStatement));
        }

        if (orderBy != null) {
            stringBuilder.append(" ")
                    .append("order by `%s`".formatted(orderBy.name));
        }

        if (reverse) {
            stringBuilder.append(" desc");
        }

        if (limit != null) {
            stringBuilder.append(" ")
                    .append("limit %s".formatted(limit));
        }

        if (offset != null) {
            stringBuilder.append(" ")
                    .append("offset %s".formatted(offset));
        }

        return stringBuilder.toString();
    }

    @Nonnull
    public Stream<ResultRow> stream() {
        try {
            String sql = toSQL();
            PreparedStatement statement = connection.prepareStatement(sql);

            if (whereStatement != null) {
                injectWhere(statement);
            }

            GlobalLogger.logger().info("exec query: {}", sql);

            this.resultSet = statement.executeQuery();
            this.resultRow = new ResultRow(this.resultSet);
            return StreamSupport.stream(this, false);
        } catch (SQLException exception) {
            exception.printStackTrace(System.out);
            return Stream.empty();
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super ResultRow> action) {
        boolean hasNext = Optional.ofNullable(this.resultSet).map(value -> {
            try {
                return value.next();
            } catch (SQLException e) {
                return false;
            }
        }).orElse(false);

        if (hasNext) {
            action.accept(this.resultRow);
        }

        return hasNext;
    }

    @Override
    public Spliterator<ResultRow> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.IMMUTABLE;
    }

    private void injectWhere(@Nonnull PreparedStatement statement) {
        Objects.requireNonNull(whereStatement);
        List<WhereStatement> whereStatements = WhereStatement.flatten(whereStatement);
        int startIndex = 1;

        for (WhereStatement whereStat: whereStatements) {
            whereStat.setInjectIndex(startIndex);
            startIndex = whereStat.inject(statement);
        }
    }
}
