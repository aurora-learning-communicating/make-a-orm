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
import com.steiner.make_a_orm.statement.insert.InsertStatement;
import com.steiner.make_a_orm.statement.select.ResultRow;
import com.steiner.make_a_orm.statement.select.SelectStatement;
import com.steiner.make_a_orm.util.Quote;
import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.util.StreamExtension;
import com.steiner.make_a_orm.where.WhereTopStatement;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Table implements IToSQL {
    @Nonnull
    public String name;

    @Nullable
    private PrimaryKey primaryKey;

    @Nonnull
    public List<ForeignKey<?>> foreignKeys;

    @Nonnull
    public List<Column<?>> columns;

    @Nonnull
    public List<Check> checks;

    public Table(@Nonnull String name) {
        this.name = name;
        this.foreignKeys = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.checks = new ArrayList<>();
    }

    @Nullable
    public abstract PrimaryKey primaryKey();

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
        return registerColumn(new CharacterColumn(name, this, length));
    }

    protected CharacterVaryingColumn characterVarying(@Nonnull String name, int length) {
        return registerColumn(new CharacterVaryingColumn(name, this, length));
    }

    protected TextColumn text(@Nonnull String name) {
        return registerColumn(new TextColumn(name, this));
    }

    // TODO: reference
    protected <T extends Column<?>> ForeignKey<T> reference(@Nonnull String name, @Nonnull T fromColumn) {
        if (!fromColumn.isUnique && !fromColumn.isPrimaryKey) {
            throw Errors.ForeignKeyError;
        }

        ForeignKey<T> key = new ForeignKey<>(name, fromColumn);
        foreignKeys.add(key);
        return key;
    }


    // For Query
    @Nonnull
    public SelectStatement select(@Nonnull Column<?> column, @Nonnull Column<?>... otherColumns) {
        if (column.fromTable != this) {
            throw Errors.TableNotTheSame;
        }

        boolean flag = Arrays.stream(otherColumns).anyMatch(col -> !col.fromTable.equals(this));
        if (flag) {
            throw Errors.TableNotTheSame;
        }

        List<Column<?>> columns = new ArrayList<>();
        columns.add(column);
        columns.addAll(List.of(otherColumns));

        return new SelectStatement(this, columns.toArray(Column<?>[]::new));
    }

    @Nonnull
    public SelectStatement selectAll() {
        return new SelectStatement(this, columns.toArray(Column<?>[]::new));
    }

    public void insert(@Nonnull Consumer<InsertStatement> consumer) {
        InsertStatement insertStatement = new InsertStatement(this, null);
        consumer.accept(insertStatement);
        insertStatement.executeInsert();
    }

    @Nonnull
    public ResultRow insertReturning(@Nonnull Consumer<InsertStatement> consumer, @Nonnull Column<?>... columns) {
        InsertStatement insertStatement = new InsertStatement(this, columns);
        consumer.accept(insertStatement);
        return insertStatement.executeInsertReturning();
    }

    public void check(@Nonnull String name, @Nonnull WhereStatement whereStatement) {
        WhereTopStatement topStatement = new WhereTopStatement(whereStatement);
        this.checks.add(new Check(name, topStatement));
    }

    @Override
    @Nonnull
    public String toSQL() {
        this.primaryKey = primaryKey();
        validateColumns();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table if not exists %s".formatted(Quote.quoteTable(this)));
        stringBuilder.append(" (\n\t");

        // if columns has primary key
        // if columns has foreign key
        // join with ",\n"
        String columnDeclarations = columns.stream().map(Column::toSQL).collect(Collectors.joining(",\n\t"));
        stringBuilder.append(columnDeclarations);

        foreignKeys.forEach(foreignKey -> {
            stringBuilder.append(",\n\t")
                    .append(foreignKey.toSQL());
        });

        if (primaryKey != null) {
            stringBuilder.append(",\n\t")
                    .append(primaryKey.toSQL());
        }



        checks.forEach(check -> {
            stringBuilder.append(",\n\t")
                    .append(check.toSQL());
        });

        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    private void validateColumns() {
        // 不允许重名
        if (primaryKey != null) {
            if (primaryKey instanceof PrimaryKey.Single<?> key) {
                long count = columns.stream().filter(column -> column.equals(key.fromColumn)).count();
                if (count >= 2) {
                    throw new SQLBuildException("duplicate column name %s".formatted(Quote.quoteColumnStandalone(key.fromColumn)));
                }
            }
        } else {
            long count = columns.stream().map(column -> column.name).distinct().count();
            if (count != columns.size()) {
                throw new SQLBuildException("duplicate column name");
            }
        }



        // 主键在的时候，其他字段不能有 自增
        if (primaryKey != null) {
            Optional<Column<?>> autoIncrementColumn = columns.stream().filter(column -> column.isAutoIncrement && !column.isPrimaryKey).findFirst();
            if (autoIncrementColumn.isPresent()) {
                throw Errors.MultiAutoIncrement;
            }
        }

        // 不能有多个自增字段
        long autoIncrementCount = columns.stream().filter(column -> column.isAutoIncrement).count();
        if (autoIncrementCount >= 2) {
            throw Errors.MultiAutoIncrement;
        }

        // 检查 外键
        foreignKeys.forEach(foreignKey -> {
            if (foreignKey.referenceColumn.fromTable == this) {
                throw Errors.ReferenceSelf;
            }
        });
    }

    private <T extends Column<?>> T registerColumn(T column) {
        columns.add(column);
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Table table) {
            boolean theSameName = this.name.equals(table.name);
            boolean theSameKey = false;

            if (this.primaryKey == null && table.primaryKey == null) {
                theSameKey = true;
            } else if (this.primaryKey != null) {
                theSameKey = this.primaryKey.equals(table.primaryKey);
            }

            // boolean theSameColumns = Stream.(columns.stream(), table.columns.stream()).allMatch((left, right) -> left.equals(right));
            boolean theSameColumns = StreamExtension.zip(columns.stream(), table.columns.stream(), Object::equals).allMatch(flag -> flag);

            return theSameName && theSameKey && theSameColumns;
        } else {
            return false;
        }
    }
}
