package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

import java.sql.SQLException;

public class Errors {
    public static SQLBuildException BothDefaultAndAutoIncrement = new SQLBuildException("cannot both set default and autoincrement");
    public static SQLBuildException NotNullable = new SQLBuildException("cannot set default null while it is not nullable");
    public static SQLBuildException SetOnPrimary = new SQLBuildException("do not set value on a primary key and autoincrement column");

    public static SQLBuildException WriteUnable = new SQLBuildException("not writable");
    public static SQLBuildException SetIndex =  new SQLBuildException("cannot set index with -1");
    public static SQLBuildException MinusIndex = new SQLBuildException("write index is less than 0");

    public static SQLBuildException ForeignKeyError = new SQLBuildException("cannot create foreign key on a non-primary or non-unique column");
    public static SQLBuildException MultiAutoIncrement = new SQLBuildException("cannot be multi autoincrement column");
    public static SQLBuildException ReferenceSelf = new SQLBuildException("cannot reference self table");

    public static SQLBuildException PrimaryNotNull = new SQLBuildException("consider that value of primary key cannot be null, so I forbid this case, cannot set the primary key with null");
    public static SQLBuildException CompositeKeyDifferent = new SQLBuildException("in composite key, all the column must from the same table");
    public static SQLBuildException SetNull = new SQLBuildException("cannot set the non-null column with null");
    public static SQLBuildException DivByZero = new SQLBuildException("div by zero");
    public static SQLBuildException TableNotTheSame = new SQLBuildException("columns not from the same table");
    public static SQLBuildException NoDefaultValueSet(@Nonnull Column<?> column) {
        return new SQLBuildException("there is no default value set in the %s".formatted(column.name));
    }

    public static SQLBuildException ColumnsNotInclude(@Nonnull Table table) {
        return new SQLBuildException("some column not belong to table: %s".formatted(Quote.quoteTable(table)));
    }

    public static SQLBuildException ColumnNotExists(@Nonnull Column<?> column, @Nonnull Table table) {
        return new SQLBuildException("column %s not exist in table %s".formatted(Quote.quoteColumnStandalone(column), Quote.quoteTable(table)));
    }

    public static SQLBuildException NoReturningColumn = new SQLBuildException("you don't specify columns to return yet");

    public static SQLRuntimeException GetNull = new SQLRuntimeException("the result is null, but using the nonnull constraint");

    public static SQLRuntimeException UnExpectedValueType(@Nonnull Object value) {
        return new SQLRuntimeException("unexpected value of type Integer %s to %s".formatted(value, value.getClass().getTypeName()));
    }

    public static SQLRuntimeException ReadError(@Nonnull SQLException cause) {
        return new SQLRuntimeException("read error").cause(cause);
    }

    public static SQLRuntimeException InsertError(@Nonnull SQLException cause) {
        return new SQLRuntimeException("insert error").cause(cause);
    }

    public static SQLRuntimeException CreateStatementFailed = new SQLRuntimeException("create statement failed");
    public static SQLRuntimeException ExecuteInsertFailed = new SQLRuntimeException("insert failed");
    public static SQLRuntimeException ExecuteUpdateFailed = new SQLRuntimeException("update failed");
    public static SQLRuntimeException ExecuteDeleteFailed = new SQLRuntimeException("delete failed");
}
