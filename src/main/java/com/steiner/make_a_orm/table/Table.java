package com.steiner.make_a_orm.table;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.blob.BlobColumn;
import com.steiner.make_a_orm.column.blob.LongBlobColumn;
import com.steiner.make_a_orm.column.blob.MediumBlobColumn;
import com.steiner.make_a_orm.column.bool.BooleanColumn;
import com.steiner.make_a_orm.column.clob.LongTextColumn;
import com.steiner.make_a_orm.column.clob.MediumTextColumn;
import com.steiner.make_a_orm.column.date.DateColumn;
import com.steiner.make_a_orm.column.date.TimeColumn;
import com.steiner.make_a_orm.column.date.TimestampColumn;
import com.steiner.make_a_orm.column.numeric.*;
import com.steiner.make_a_orm.column.string.CharacterColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.column.string.TextColumn;
import com.steiner.make_a_orm.column.trait.IEqualColumn;
import com.steiner.make_a_orm.column.trait.IPrimaryKeyColumn;
import com.steiner.make_a_orm.delete.DeleteStatement;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.insert.InsertStatement;
import com.steiner.make_a_orm.select.Query;
import com.steiner.make_a_orm.select.ResultRow;
import com.steiner.make_a_orm.update.UpdateStatement;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class Table {
    @Nonnull
    public String name;
    @Nonnull
    public List<Column<?>> columns;

    public Table(@Nonnull String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Table other) {
            return this.name.equals(other.name);
        } else {
            return false;
        }
    }

    @Nonnull
    public final String toSQL() {
        // check first
        long count = columns.stream().filter(column -> column.isPrimaryKey).count();
        if (count > 1) {
            throw new SQLBuildException("cannot set multi primary key in a table");
        }

        count = columns.stream().filter(column -> column.isAutoIncrement).count();
        if (count > 1) {
            throw new SQLBuildException("cannot set multi auto increment column in a table");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table if not exists `%s` (\n\t".formatted(name));

        // append fields and constraints
        stringBuilder.append(
                columns.stream()
                        .map(column -> "`%s` %s %s".formatted(column.name, column.sqlType(), column.inlineConstraints()))
                        .collect(Collectors.joining(",\n\t"))
        );

        if (!columns.isEmpty()) {
            stringBuilder.append(",\n\t");
        }

        // append standalone constraints
        for (Column<?> column: columns) {
            String constraints = String.join(",\n\t", column.standAloneConstraints());
            stringBuilder.append(constraints);
        }


        stringBuilder.append("\n) ");

        // append suffix constraint, only one
        columns.stream()
                .filter(column -> column.isAutoIncrement)
                .findFirst()
                .flatMap(Column::suffixConstraints)
                .ifPresent(stringBuilder::append);

        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    @Nonnull
    public final Query select(Column<?> column, Column<?>... otherColumns) {
        List<Column<?>> columns = new LinkedList<>();
        columns.add(column);
        columns.addAll(List.of(otherColumns));

        return new Query(this, columns.toArray(Column<?>[]::new));
    }

    @Nonnull
    public final Query selectAll() {
        return new Query(this, columns.toArray(Column<?>[]::new));
    }

    public final <T, E extends Column<T> & IPrimaryKeyColumn<T, E> & IEqualColumn<T, E>>
    E primaryKeyColumn() {
        return columns.stream()
                .filter(column -> column.isPrimaryKey && column instanceof IPrimaryKeyColumn<?,?> && column instanceof IEqualColumn<?,?>)
                .findFirst()
                .map(column -> (E) column /*maybe here*/)
                .orElseThrow(() -> new SQLBuildException("there is no primary key in the table `%s`".formatted(name)));
    }

    public final void insert(@Nonnull Consumer<InsertStatement> consumer) {
        InsertStatement insertStatement = new InsertStatement(this);
        consumer.accept(insertStatement);
        insertStatement.executeInsert();
    }

    public final ResultRow insertReturning(@Nonnull Consumer<InsertStatement> consumer, Column<?>... columns) {
        InsertStatement insertStatement = new InsertStatement(this);
        consumer.accept(insertStatement);
        return insertStatement.executeInsertReturning(columns);
    }

    public final void update(@Nonnull WhereStatement where, @Nonnull Consumer<UpdateStatement> consumer) {
        UpdateStatement updateStatement = new UpdateStatement(this);
        consumer.accept(updateStatement);
        updateStatement.where(where);
        updateStatement.executeUpdate();
    }

    public final void update(@Nonnull Supplier<WhereStatement> supplier, @Nonnull Consumer<UpdateStatement> consumer) {
        UpdateStatement updateStatement = new UpdateStatement(this);
        consumer.accept(updateStatement);
        updateStatement.where(supplier);
        updateStatement.executeUpdate();
    }

    public final void update(@Nonnull Consumer<UpdateStatement> consumer) {
        UpdateStatement updateStatement = new UpdateStatement(this);
        consumer.accept(updateStatement);
        updateStatement.executeUpdate();
    }

    public final void deleteWhere(@Nonnull WhereStatement where) {
        DeleteStatement deleteStatement = new DeleteStatement(this);
        deleteStatement.where(where);
        deleteStatement.executeDelete();
    }

    public final void deleteWhere(@Nonnull Supplier<WhereStatement> supplier) {
        DeleteStatement deleteStatement = new DeleteStatement(this);
        deleteStatement.where(supplier);
        deleteStatement.executeDelete();
    }

    public final void deleteAll() {
        DeleteStatement deleteStatement = new DeleteStatement(this);
        deleteStatement.executeDelete();
    }

    @Nonnull
    protected final BlobColumn blob(@Nonnull String name) {
        return registerColumn(new BlobColumn(name));
    }

    @Nonnull
    protected final MediumBlobColumn mediumBlob(@Nonnull String name) {
        return registerColumn(new MediumBlobColumn(name));
    }

    @Nonnull
    protected final LongBlobColumn longBlob(@Nonnull String name) {
        return registerColumn(new LongBlobColumn(name));
    }

    @Nonnull
    protected final BooleanColumn bool(@Nonnull String name) {
        return registerColumn(new BooleanColumn(name));
    }

    @Nonnull
    protected final LongTextColumn longText(@Nonnull String name) {
        return registerColumn(new LongTextColumn(name));
    }

    @Nonnull
    protected final MediumTextColumn mediumText(@Nonnull String name) {
        return registerColumn(new MediumTextColumn(name));
    }

    @Nonnull
    protected final DateColumn date(@Nonnull String name) {
        return registerColumn(new DateColumn(name));
    }

    @Nonnull
    protected final TimeColumn time(@Nonnull String name) {
        return registerColumn(new TimeColumn(name));
    }

    @Nonnull
    protected final TimestampColumn timestamp(@Nonnull String name) {
        return registerColumn(new TimestampColumn(name));
    }

    @Nonnull
    protected final BigIntColumn bigint(@Nonnull String name) {
        return registerColumn(new BigIntColumn(name));
    }

    @Nonnull
    protected final DecimalColumn decimal(@Nonnull String name, int precision, int scale) {
        return registerColumn(new DecimalColumn(name, precision, scale));
    }

    @Nonnull
    protected final DoubleColumn float64(@Nonnull String name) {
        return registerColumn(new DoubleColumn(name));
    }

    @Nonnull
    protected final FloatColumn float32(@Nonnull String name) {
        return registerColumn(new FloatColumn(name));
    }

    @Nonnull
    protected final IntColumn integer(@Nonnull String name) {
        return registerColumn(new IntColumn(name));
    }

    @Nonnull
    protected final SmallIntColumn smallint(@Nonnull String name) {
        return registerColumn(new SmallIntColumn(name));
    }

    @Nonnull
    protected final TinyIntColumn tinyint(@Nonnull String name) {
        return registerColumn(new TinyIntColumn(name));
    }

    @Nonnull
    protected final CharacterColumn character(@Nonnull String name, int length) {
        return registerColumn(new CharacterColumn(name, length));
    }

    @Nonnull
    protected final CharacterVaryingColumn characterVarying(@Nonnull String name, int length) {
        return registerColumn(new CharacterVaryingColumn(name, length));
    }

    @Nonnull
    protected final TextColumn text(@Nonnull String name) {
        return registerColumn(new TextColumn(name));
    }

    private <T extends Column<?>> T registerColumn(T column) {
        columns.add(column);
        column.setFromTable(this);
        return column;
    }
}
