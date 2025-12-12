package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.statement.select.JoinStatement;
import com.steiner.make_a_orm.statement.select.JoinType;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class JoinTable implements IToSQL {
    @Nonnull
    public Table leftTable;
    @Nonnull
    public Column<?> leftColumn;
    @Nonnull
    public JoinType joinType;

    @Nonnull
    public Table rightTable;
    @Nonnull
    public Column<?> rightColumn;

    public JoinTable(@Nonnull JoinType joinType, @Nonnull Table leftTable, @Nonnull Column<?> leftColumn, @Nonnull Table rightTable, @Nonnull Column<?> rightColumn) {
        if (!leftColumn.fromTable.equals(leftTable)) {
            throw Errors.ColumnNotExists(leftColumn, leftTable);
        }

        if (!rightColumn.fromTable.equals(rightTable)) {
            throw Errors.ColumnNotExists(rightColumn, rightTable);
        }

        this.joinType = joinType;
        this.leftTable = leftTable;
        this.leftColumn = leftColumn;
        this.rightTable = rightTable;
        this.rightColumn = rightColumn;
    }

    public JoinStatement select(@Nonnull Column<?> column, @Nonnull Column<?>... otherColumns) {
        checkColumn(column);
        for (Column<?> otherColumn : otherColumns) {
            checkColumn(otherColumn);
        }

        List<Column<?>> sliceColumns = new ArrayList<>();
        sliceColumns.add(column);
        sliceColumns.addAll(List.of(otherColumns));

        return new JoinStatement(this, sliceColumns);
    }

    private void checkColumn(@Nonnull Column<?> column) {
        if (column.fromTable.equals(leftTable)) {
            return;
        }

        if (column.fromTable.equals(rightTable)) {
            return;
        }

        throw Errors.ColumnNotExists(column, rightTable);
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "%s %s %s on %s = %s".formatted(Quote.quoteTable(leftTable), joinType.sign, Quote.quoteTable(rightTable), Quote.quoteColumnStandalone(leftColumn), Quote.quoteColumnStandalone(rightColumn));
    }
}
