package com.steiner.make_a_orm.exception;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.SQLException;

// 生成 SQL 语句时的错误
public class SQLBuildException extends RuntimeException {
    public SQLBuildException(@Nonnull String message) {
        super(message);
    }

    public SQLBuildException cause(@Nonnull Throwable cause) {
        initCause(cause);
        return this;
    }
}
