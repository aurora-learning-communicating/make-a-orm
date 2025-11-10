package com.steiner.make_a_orm.where.predicate;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;

public abstract class WherePredicate<T, E extends Column<T>> extends WhereStatement implements IToSQL {
    @Nonnull
    public E column;

    public boolean isInCheck;

    public WherePredicate(@Nonnull E column) {
        this(true, column);
    }

    public WherePredicate(boolean writable, @Nonnull E column) {
        super(writable);
        this.column = column;
        this.isInCheck = false;
    }
}
