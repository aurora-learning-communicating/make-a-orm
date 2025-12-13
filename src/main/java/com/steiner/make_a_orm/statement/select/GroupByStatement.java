package com.steiner.make_a_orm.statement.select;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.aggregate.Aggregate;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.WhereTopStatement;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GroupByStatement implements Spliterator<ResultRow>, IToSQL {
    private static final Logger logger = LoggerFactory.getLogger("GroupByStatement");
    private static class OrderBy implements IToSQL {
        @Nullable
        public Column<?> orderByColumn;
        @Nullable
        public Aggregate<?> orderByAggregate;

        public OrderBy(@Nullable Column<?> orderByColumn, @Nullable Aggregate<?> orderByAggregate) {
            this.orderByColumn = orderByColumn;
            this.orderByAggregate = orderByAggregate;
        }

        @Override
        @Nonnull
        public String toSQL() {
            if (orderByColumn != null) {
                return Quote.quoteColumn(orderByColumn);
            }

            if (orderByAggregate != null) {
                return orderByAggregate.alias();
            }

            throw Errors.GroupByOrderBySetError;
        }
    }

    @Nonnull
    public Table table;
    @Nullable
    public Column<?> byColumn;
    @Nullable
    public WhereTopStatement havingOn;
    @Nonnull
    public List<Aggregate<?>> aggregates;

    @Nullable
    OrderBy orderBy;
    public boolean reversed;

    @Nullable
    public Long limit;
    @Nullable
    public Long offset;

    @Nonnull
    public Connection connection;

    @Nullable
    public ResultSet resultSet;
    @Nullable
    public ResultRow resultRow;

    public GroupByStatement(@Nonnull Table table,@Nonnull List<Aggregate<?>> aggregates) {
        this.table = table;
        this.byColumn = null;
        this.aggregates = aggregates;

        this.orderBy = null;
        this.reversed = false;
        this.resultSet = null;
        this.resultRow = null;
        this.havingOn = null;
        this.limit = null;
        this.offset = null;
        this.connection = Transaction.currentConnection();
    }

    public GroupByStatement groupBy(@Nonnull Column<?> column) {
        if (this.byColumn == null) {
            this.byColumn = column;
        } else {
            if (!this.byColumn.equals(column)) {
                throw Errors.GroupByColumnNotMatch(this.byColumn);
            }
        }

        return this;
    }

    public GroupByStatement havingOn(@Nonnull WhereStatement statement) {
        this.havingOn = new WhereTopStatement(statement);
        return this;
    }

    public GroupByStatement havingOn(@Nonnull Supplier<WhereStatement> block) {
        return havingOn(block.get());
    }

    public GroupByStatement orderBy(@Nonnull Column<?> orderBy) {
        if (!orderBy.equals(this.byColumn)) {
            throw Errors.GroupByColumnNotMatch(orderBy);
        }

        this.orderBy = new OrderBy(orderBy, null);
        this.reversed = false;

        return this;
    }

    public GroupByStatement orderBy(@Nonnull Column<?> orderBy, boolean reversed) {
        if (!orderBy.equals(this.byColumn)) {
            throw Errors.GroupByColumnNotMatch(orderBy);
        }

        this.orderBy = new OrderBy(orderBy, null);
        this.reversed = reversed;

        return this;
    }

    public GroupByStatement orderBy(@Nonnull Aggregate<?> orderBy) {
        if (!aggregates.contains(orderBy)) {
            throw Errors.GroupByAggregateNotInclude(orderBy);
        }

        this.orderBy = new OrderBy(null, orderBy);
        this.reversed = false;

        return this;
    }

    public GroupByStatement orderBy(@Nonnull Aggregate<?> orderBy, boolean reversed) {
        if (!aggregates.contains(orderBy)) {
            throw Errors.GroupByAggregateNotInclude(orderBy);
        }

        this.orderBy = new OrderBy(null, orderBy);
        this.reversed = reversed;

        return this;
    }

    public GroupByStatement limit(long value) {
        this.limit = value;
        return this;
    }

    public GroupByStatement offset(long value) {
        this.offset = value;
        return this;
    }

    @Nonnull
    public Stream<ResultRow> stream() {
        try {
            String sql = toSQL();
            PreparedStatement statement = connection.prepareStatement(sql);

            if (havingOn != null) {
                havingOn.write(statement);
            }

            logger.info("exec sql template: {}", sql);

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
        boolean hasNext = true;

        try {
            if (this.resultSet != null) {
                hasNext = this.resultSet.next();
            } else {
                hasNext = false;
            }
        } catch (SQLException e) {
            hasNext = false;
        }

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

    @Override
    @Nonnull
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();

        String byColumnName = Quote.quoteColumnStandalone(Objects.requireNonNull(byColumn));
        String aggregateNames = aggregates
                .stream()
                .map(IToSQL::toSQL)
                .collect(Collectors.joining(", "));

        @Nullable String sliceNames = null;
        if (this.aggregates.isEmpty()) {
            sliceNames = byColumnName;
        } else {
            sliceNames = "%s, %s".formatted(byColumnName, aggregateNames);
        }

        String select = "select %s from %s group by %s"
                .formatted(sliceNames, Quote.quoteTable(table), byColumnName);

        stringBuilder.append(select);

        if (havingOn != null) {
            stringBuilder.append(" having ")
                    .append(havingOn.toSQL());
        }

        if (orderBy != null) {
            stringBuilder.append(" order by %s".formatted(orderBy.toSQL()));
        }

        if (reversed) {
            stringBuilder.append(" desc");
        }

        if (limit != null) {
            stringBuilder.append(" limit %s".formatted(limit));
        }

        if (offset != null) {
            stringBuilder.append(" offset %s".formatted(offset));
        }

        return stringBuilder.toString();
    }
}
