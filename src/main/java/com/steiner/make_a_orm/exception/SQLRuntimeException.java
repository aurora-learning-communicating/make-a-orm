package com.steiner.make_a_orm.exception;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {
    public SQLRuntimeException(@Nonnull String message) {
        super(message);
    }

    public SQLRuntimeException cause(@Nonnull SQLException cause) {
        initCause(cause);
        return this;
    }
}
