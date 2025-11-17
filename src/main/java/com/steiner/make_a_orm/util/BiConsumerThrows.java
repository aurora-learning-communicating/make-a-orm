package com.steiner.make_a_orm.util;

import jakarta.annotation.Nonnull;

import java.sql.SQLException;

@FunctionalInterface
public interface BiConsumerThrows<T, E> {
    void accept(@Nonnull T left, @Nonnull E right) throws SQLException;
}
