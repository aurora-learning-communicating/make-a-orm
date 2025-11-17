package com.steiner.make_a_orm.statement.select;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
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
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class SelectStatement implements Spliterator<ResultRow>, IToSQL {
    private static final Logger logger = LoggerFactory.getLogger("SelectStatement");

    @Nonnull
    public Table table;
    @Nonnull
    public List<Column<?>> sliceColumns;
    @Nullable
    public ResultSet resultSet;
    @Nullable
    public ResultRow resultRow;
    @Nullable
    private WhereTopStatement whereStatement;
    @Nullable
    public Column<?> orderBy;

    public boolean reversed;

    @Nullable
    public Long limit;
    @Nullable
    public Long offset;

    @Nonnull
    public Connection connection;

    public SelectStatement(@Nonnull Table table, Column<?>... columns) {
        this.table = table;
        this.sliceColumns = Arrays.asList(columns);
        this.orderBy = null;
        this.reversed = false;
        this.resultSet = null;
        this.resultRow = null;
        this.whereStatement = null;
        this.limit = null;
        this.offset = null;
        this.connection = Transaction.currentConnection();
    }

    public SelectStatement where(@Nonnull WhereStatement statement) {
        this.whereStatement = new WhereTopStatement(statement);
        return this;
    }

    public SelectStatement where(@Nonnull Supplier<WhereStatement> block) {
        this.whereStatement = new WhereTopStatement(block.get());
        return this;
    }

    public SelectStatement orderBy(@Nonnull Column<?> column) {
        this.orderBy = column;
        return this;
    }

    public SelectStatement orderBy(@Nonnull Column<?> column, boolean reversed) {
        this.orderBy = column;
        this.reversed = reversed;
        return this;
    }

    public SelectStatement limit(long value) {
        this.limit = value;
        return this;
    }

    public SelectStatement offset(long value) {
        this.offset = value;
        return this;
    }


    @Nonnull
    public Stream<ResultRow> stream() {
        try {
            String sql = toSQL();
            PreparedStatement statement = connection.prepareStatement(sql);

            if (whereStatement != null) {
                whereStatement.write(statement);
            }

            // TODO: log
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

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        String columnNames = sliceColumns.stream()
                .map(Quote::quoteColumnStandalone)
                .collect(Collectors.joining(", "));

        String select = "select %s from %s".formatted(columnNames, Quote.quoteTable(table));
        stringBuilder.append(select);

        if (whereStatement != null) {
            stringBuilder.append(" where ")
                    .append(whereStatement.toSQL());
        }

        if (orderBy != null) {
            stringBuilder.append(" order by %s".formatted(Quote.quoteColumnStandalone(orderBy)));
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
