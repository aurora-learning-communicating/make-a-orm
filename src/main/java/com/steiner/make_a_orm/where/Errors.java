package com.steiner.make_a_orm.where;

import com.steiner.make_a_orm.exception.SQLBuildException;

public class Errors {
    public static SQLBuildException WriteUnable = new SQLBuildException("not writable", null);
    public static SQLBuildException SetIndex =  new SQLBuildException("cannot set index with -1", null);
    public static SQLBuildException MinusIndex = new SQLBuildException("write index is less than 0", null);
}
