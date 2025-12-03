package com.steiner.make_a_orm.statement.jointable;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.statement.select.ResultRow;
import com.steiner.make_a_orm.table.JoinTable;
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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JoinSelectStatement implements Spliterator<ResultRow>, IToSQL {
    private static final Logger logger = LoggerFactory.getLogger("JoinSelectStatement");

    @Nonnull
    public JoinTable joinTable;

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

    public JoinSelectStatement(@Nonnull JoinTable joinTable, @Nonnull List<Column<?>> sliceColumns) {
        this.joinTable = joinTable;
        this.sliceColumns = sliceColumns;

        this.orderBy = null;
        this.reversed = false;
        this.resultSet = null;
        this.resultRow = null;
        this.whereStatement = null;
        this.limit = null;
        this.offset = null;
        this.connection = Transaction.currentConnection();
    }


    public JoinSelectStatement where(@Nonnull WhereStatement statement) {
        this.whereStatement = new WhereTopStatement(statement);
        return this;
    }

    public JoinSelectStatement where(@Nonnull Supplier<WhereStatement> block) {
        this.whereStatement = new WhereTopStatement(block.get());
        return this;
    }

    public JoinSelectStatement orderBy(@Nonnull Column<?> column) {
        this.orderBy = column;
        return this;
    }

    public JoinSelectStatement orderBy(@Nonnull Column<?> column, boolean reversed) {
        this.orderBy = column;
        this.reversed = reversed;
        return this;
    }

    public JoinSelectStatement limit(long value) {
        this.limit = value;
        return this;
    }

    public JoinSelectStatement offset(long value) {
        this.offset = value;
        return this;
    }


    // TODO: groupby

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

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        String sliceColumnNames = sliceColumns.stream()
                .map(Quote::quoteColumnStandalone)
                .collect(Collectors.joining(", "));

        String pattern = "select %s from %s";
        String select = pattern.formatted(sliceColumnNames, joinTable.toSQL());
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
}
