package com.steiner.make_a_orm.where;

import com.steiner.make_a_orm.exception.SQLBuildException;

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

}
