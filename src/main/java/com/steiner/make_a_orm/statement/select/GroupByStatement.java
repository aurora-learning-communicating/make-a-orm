package com.steiner.make_a_orm.statement.select;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.where.WhereTopStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupByStatement {
    private static final Logger logger = LoggerFactory.getLogger("GroupByStatement");

    @Nonnull
    public Table table;
    @Nonnull
    public Column<?> byColumn;
    @Nullable
    public WhereTopStatement havingOn;


}
