package com.steiner.make_a_orm.column;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.constraint.Constraint;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Column<T> implements IToSQL {
    @Nullable
    private Table fromTable; // late
    @Nonnull
    public String name;
    @Nonnull
    public List<Constraint> constraints;

    public boolean isPrimaryKey;
    public boolean isAutoIncrement;
    public boolean hasDefault;
    public boolean isNullable;

    public Column(@Nonnull String name) {
        this.name = name;
        this.fromTable = null;
        this.constraints = new ArrayList<>();
        // TODO: 有那个必要吗，直接 去掉 就成了嘛，顺带 加个 `Nullable` 的限制
        // this.constraints.add(new NotNullConstraint());
        this.isPrimaryKey = false;
        this.isAutoIncrement = false;
        this.hasDefault = false;
        this.isNullable = false;
    }

    @Nonnull
    public Table getTable() {
        return Objects.requireNonNull(fromTable);
    }

    public void setTable(@Nonnull Table table) {
        this.fromTable = table;
    }

    // TODO: modify method


    @Nullable
    public abstract T read(@Nonnull ResultSet resultSet);
    public abstract void write(@Nonnull PreparedStatement statement, int index, @Nonnull T value);
    public abstract void writeDefault(@Nonnull PreparedStatement statement, int index);

    @Nonnull
    public abstract String format(@Nonnull T value);
}
