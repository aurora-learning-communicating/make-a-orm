package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.aggregate.Aggregate;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.date.DateColumn;
import com.steiner.make_a_orm.column.date.TimeColumn;
import com.steiner.make_a_orm.column.date.TimestampColumn;
import com.steiner.make_a_orm.column.number.*;
import com.steiner.make_a_orm.column.string.CharacterColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.column.string.TextColumn;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.key.ForeignKey;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.statement.delete.DeleteStatement;
import com.steiner.make_a_orm.statement.insert.InsertStatement;
import com.steiner.make_a_orm.statement.select.GroupByStatement;
import com.steiner.make_a_orm.statement.select.JoinType;
import com.steiner.make_a_orm.statement.select.ResultRow;
import com.steiner.make_a_orm.statement.select.SelectStatement;
import com.steiner.make_a_orm.statement.update.UpdateStatement;
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
    private final String name;
    // 不要修改 primaryKey 的获取 顺序，不然就会 因为 初始顺序不对，导致 `idName` 为空
    // 最烦的就是这玩意了，不要再构造函数中调用这些 abstract 方法
    @Nullable
    private PrimaryKey primaryKey;
    private boolean primaryKeyAlreadySet;
    @Nonnull
    public List<ForeignKey<?>> foreignKeys;
    @Nonnull
    public List<Column<?>> columns;
    @Nonnull
    public List<Check> checks;
    @Nonnull
    public Dialect dialect;


    public Table(@Nonnull String name, @Nonnull Dialect dialect) {
        this.name = name;
        this.dialect = dialect;
        this.foreignKeys = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.checks = new ArrayList<>();
        this.primaryKeyAlreadySet = false;
    }

    @Nullable
    public abstract PrimaryKey primaryKey();

    // register column
    @Nonnull
    protected BigIntColumn bigint(@Nonnull String name) {
        return registerColumn(new BigIntColumn(name, this));
    }

    @Nonnull
    protected DoubleColumn float64(@Nonnull String name) {
        return registerColumn(new DoubleColumn(name, this));
    }

    @Nonnull
    protected FloatColumn float32(@Nonnull String name) {
        return registerColumn(new FloatColumn(name, this));
    }

    @Nonnull
    protected DecimalColumn decimal(@Nonnull String name, int precision, int scale) {
        return registerColumn(new DecimalColumn(name, this, precision, scale));
    }

    @Nonnull
    protected IntegerColumn integer(@Nonnull String name) {
        return registerColumn(new IntegerColumn(name, this));
    }

    @Nonnull
    protected SmallIntColumn smallint(@Nonnull String name) {
        return registerColumn(new SmallIntColumn(name, this));
    }

    @Nonnull
    protected TinyIntColumn tinyint(@Nonnull String name) {
        return registerColumn(new TinyIntColumn(name, this));
    }

    @Nonnull
    protected CharacterColumn character(@Nonnull String name, int length) {
        return registerColumn(new CharacterColumn(name, this, length));
    }

    @Nonnull
    protected CharacterVaryingColumn characterVarying(@Nonnull String name, int length) {
        return registerColumn(new CharacterVaryingColumn(name, this, length));
    }

    @Nonnull
    protected TextColumn text(@Nonnull String name) {
        return registerColumn(new TextColumn(name, this));
    }

    @Nonnull
    protected DateColumn date(@Nonnull String name) {
        return registerColumn(new DateColumn(name, this));
    }

    @Nonnull
    protected TimeColumn time(@Nonnull String name) {
        return registerColumn(new TimeColumn(name, this));
    }

    @Nonnull
    protected TimestampColumn timestamp(@Nonnull String name) {
        return registerColumn(new TimestampColumn(name, this));
    }

    @Nonnull
    protected <T extends Column<?>> ForeignKey<T> reference(@Nonnull String name, @Nonnull T fromColumn) {
        if (!fromColumn.isUnique && !fromColumn.isPrimaryKey) {
            throw Errors.ForeignKeyError;
        }

        ForeignKey<T> key = new ForeignKey<>(name, fromColumn);
        foreignKeys.add(key);
        return key;
    }

    // Join Table
    @Nonnull
    private JoinTable join(@Nonnull JoinType joinType, @Nonnull Table other, @Nonnull Column<?> onColumn, @Nonnull Column<?> otherColumn) {
        return new JoinTable(joinType, this, onColumn, other, otherColumn);
    }

    @Nonnull
    public JoinTable leftJoin(@Nonnull Table other, @Nonnull Column<?> onColumn, @Nonnull Column<?> otherColumn) {
        return this.join(JoinType.Left, other, onColumn, otherColumn);
    }

    @Nonnull
    public JoinTable rightJoin(@Nonnull Table other, @Nonnull Column<?> onColumn, @Nonnull Column<?> otherColumn) {
        return this.join(JoinType.Right, other, onColumn, otherColumn);
    }

    @Nonnull
    public JoinTable innerJoin(@Nonnull Table other, @Nonnull Column<?> onColumn, @Nonnull Column<?> otherColumn) {
        return this.join(JoinType.Inner, other, onColumn, otherColumn);
    }

    @Nonnull
    public JoinTable outerJoin(@Nonnull Table other, @Nonnull Column<?> onColumn, @Nonnull Column<?> otherColumn) {
        return this.join(JoinType.Outer, other, onColumn, otherColumn);
    }


    // For Query
    @Nonnull
    public SelectStatement select(@Nonnull Column<?> column, @Nonnull Column<?>... otherColumns) {
        if (!column.fromTable.equals(this)) {
            throw Errors.TableNotTheSame;
        }

        boolean flag = Arrays.stream(otherColumns).anyMatch(col -> !col.fromTable.equals(this));
        if (flag) {
            throw Errors.TableNotTheSame;
        }

        List<Column<?>> columns = new ArrayList<>();
        columns.add(column);
        columns.addAll(List.of(otherColumns));

        return new SelectStatement(this, columns);
    }

    // group by
    // 1. select(aggregates...)
    // 2. select(column, aggregates...)
    @Nonnull
    public GroupByStatement select(@Nonnull Aggregate<?>... aggregates) {
        boolean flag = Arrays.stream(aggregates).anyMatch(aggregate -> !aggregate.column.fromTable.equals(this));
        if (flag) {
            throw Errors.TableNotTheSame;
        }

        return new GroupByStatement(this, List.of(aggregates));
    }


    @Nonnull
    public GroupByStatement select(@Nonnull Column<?> column, @Nonnull Aggregate<?>... aggregates) {
        if (!column.fromTable.equals(this)) {
            throw Errors.TableNotTheSame;
        }

        boolean flag = Arrays.stream(aggregates).anyMatch(aggregate -> !aggregate.column.fromTable.equals(this));
        if (flag) {
            throw Errors.TableNotTheSame;
        }

        return new GroupByStatement(this, List.of(aggregates)).groupBy(column);
    }

    @Nonnull
    public SelectStatement selectAll() {
        List<Column<?>> slices = new ArrayList<>();

        if (!this.primaryKeyAlreadySet) {
            this.primaryKey = primaryKey();
            this.primaryKeyAlreadySet = true;
        }

        if (this.primaryKey != null && this.primaryKey instanceof PrimaryKey.Single<?> key) {
            slices.add(key.fromColumn);
        }

        slices.addAll(columns);
        return new SelectStatement(this, slices);
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

    public void update(@Nonnull Consumer<UpdateStatement> consumer) {
        UpdateStatement updateStatement = new UpdateStatement(this);
        consumer.accept(updateStatement);
        updateStatement.executeUpdate();
    }

    public void delete(@Nonnull Consumer<DeleteStatement> consumer) {
        DeleteStatement deleteStatement = new DeleteStatement(this);
        consumer.accept(deleteStatement);
        deleteStatement.executeDelete();
    }


    public void check(@Nonnull String name, @Nonnull WhereStatement whereStatement) {
        WhereTopStatement topStatement = new WhereTopStatement(whereStatement);
        this.checks.add(new Check(name, topStatement));
    }

    @Override
    @Nonnull
    public String toSQL() {
        if (!this.primaryKeyAlreadySet) {
            this.primaryKey = primaryKey();
            this.primaryKeyAlreadySet = true;
        }

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

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Nonnull
    public String getName() {
        return this.name;
    }
}
