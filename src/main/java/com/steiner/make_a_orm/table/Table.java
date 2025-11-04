package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.numeric.*;
import com.steiner.make_a_orm.column.string.CharacterColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.column.string.TextColumn;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.key.ForeignKey;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class Table implements IToSQL {
    @Nonnull
    public String name;

    @Nullable
    public PrimaryKey primaryKey;

    @Nonnull
    public List<ForeignKey> foreignKeys;

    @Nonnull
    public List<Column<?>> columns;

    @Nonnull
    public List<Check> checks;

    public Table(@Nonnull String name) {
        this.name = name;
        this.primaryKey = null;
        this.foreignKeys = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.checks = new ArrayList<>();
    }


    // register column
    @Nonnull
    protected BigIntColumn bigint(@Nonnull String name) {
        return registerColumn(new BigIntColumn(name, this));
    }

    protected DoubleColumn float64(@Nonnull String name) {
        return registerColumn(new DoubleColumn(name, this));
    }

    protected FloatColumn float32(@Nonnull String name) {
        return registerColumn(new FloatColumn(name, this));
    }

    protected IntegerColumn integer(@Nonnull String name) {
        return registerColumn(new IntegerColumn(name, this));
    }

    protected SmallIntColumn smallint(@Nonnull String name) {
        return registerColumn(new SmallIntColumn(name, this));
    }

    protected TinyIntColumn tinyint(@Nonnull String name) {
        return registerColumn(new TinyIntColumn(name, this));
    }

    protected CharacterColumn character(@Nonnull String name, int length) {
        return registerColumn(new CharacterColumn(name, length));
    }

    protected CharacterVaryingColumn characterVarying(@Nonnull String name, int length) {
        return registerColumn(new CharacterVaryingColumn(name, length));
    }

    protected TextColumn text(@Nonnull String name) {
        return registerColumn(new TextColumn(name));
    }

    // TODO: reference
    protected ForeignKey reference(@Nonnull String name, @Nonnull Column<?> fromColumn) {
        ForeignKey key = new ForeignKey(name, fromColumn);
        foreignKeys.add(key);
        return key;
    }

    protected PrimaryKey.Composite primaryKey(Column<?> first, Column<?> second, Column<?>... rest) {
        List<Column<?>> columns = new ArrayList<>();
        columns.add(first);
        columns.add(second);

        columns.addAll(Arrays.asList(rest));

        PrimaryKey.Composite key = new PrimaryKey.Composite(columns);
        this.primaryKey = key;
        return key;
    }

    protected <T> PrimaryKey.Single<T> primaryKey(Column<T> column) {
        PrimaryKey.Single<T> key = new PrimaryKey.Single<>(column);
        this.primaryKey = key;
        return key;
    }

    // TODO: check(name) { block }
    public void check(@Nonnull String name, @Nonnull Supplier<WhereStatement> supplier) {
        this.checks.add(new Check(name, supplier));
    }

    @Override
    @Nonnull
    public String toSQL() {
        validateColumns();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table if not exists %s".formatted(Quote.quoteTableName(name)));
        stringBuilder.append(" (");

        // if columns has primary key
        // if columns has foreign key
        // join with ",\n"
        String columnDeclarations = columns.stream().map(Column::toSQL).collect(Collectors.joining(",\n"));
        stringBuilder.append(columnDeclarations);

        if (primaryKey != null) {
            stringBuilder.append(",\n")
                    .append(primaryKey.toSQL());
        }

        foreignKeys.forEach(foreignKey -> {
            stringBuilder.append(",\n")
                    .append(foreignKey.toSQL());
        });

        checks.forEach(check -> {
            stringBuilder.append(",\n")
                    .append(check.toSQL());
        });

        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    private void validateColumns() {
        // 不允许重名
        if (primaryKey != null) {
            if (primaryKey instanceof PrimaryKey.Single<?>) {
                String primaryKeyName = ((PrimaryKey.Single<?>) primaryKey).fromColumn.name;
                boolean flag = columns.stream().anyMatch(column -> column.name.equals(primaryKeyName));
                if (flag) {
                    throw new SQLBuildException("duplicate column name %s".formatted(Quote.quoteColumnName(primaryKeyName)), null);
                }
            }
        } else {
            long count = columns.stream().map(column -> column.name).distinct().count();
            if (count != columns.size()) {
                throw new SQLBuildException("duplicate column name", null);
            }
        }



        // 主键在的时候，其他字段不能有 自增
        if (primaryKey != null) {
            boolean hasAutoIncrement = columns.stream().anyMatch(column -> column.isAutoIncrement);
            if (hasAutoIncrement) {
                throw new SQLBuildException("cannot be multi autoincrement column", null);
            }
        }

        // 不能有多个自增字段
        long autoIncrementCount = columns.stream().filter(column -> column.isAutoIncrement).count();
        if (autoIncrementCount >= 2) {
            throw new SQLBuildException("cannot be multi autoincrement column", null);
        }

        // 检查 外键
        foreignKeys.forEach(foreignKey -> {
            if (foreignKey.referenceColumn.fromTable == this) {
                throw new SQLBuildException("cannot reference self table", null);
            }
        });


    }

    private <T extends Column<?>> T registerColumn(T column) {
        columns.add(column);
        return column;
    }
}
