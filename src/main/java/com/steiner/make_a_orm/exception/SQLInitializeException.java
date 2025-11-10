package com.steiner.make_a_orm.exception;

import jakarta.annotation.Nonnull;

// 描述 创建 数据库时的错误
public class SQLInitializeException extends RuntimeException {
    public SQLInitializeException(@Nonnull String message) {
        super(message);
    }

    public SQLInitializeException cause(@Nonnull Throwable cause) {
        initCause(cause);
        return this;
    }
}
