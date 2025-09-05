package com.steiner.make_a_orm.column.constraint.impl;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.constraint.Constraint;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

public class ForeignKeyConstraint extends Constraint.StandAlone {
    @Nonnull
    String columnName;

    @Nonnull
    Table referenceTable;

    @Nonnull
    Column<?> referenceColumn;

    public ForeignKeyConstraint(@Nonnull String name, @Nonnull String columnName, @Nonnull Table referenceTable, @Nonnull Column<?> referenceColumn) {
        super(name);
        this.columnName = columnName;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "constraint %s foreign key(%s) references %s (%s)"
                .formatted(Quote.quoteKeyName(constraintName),
                        Quote.quoteColumnName(columnName),
                        Quote.quoteTableName(referenceTable.name), Quote.quoteColumnName(referenceColumn.name));
    }
}
