package com.steiner.make_a_orm.exception;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.SQLException;

public class SQLBuildException extends RuntimeException {
    public SQLBuildException(@Nonnull String message, @Nullable SQLException cause) {
        super(message, cause);
    }
}
