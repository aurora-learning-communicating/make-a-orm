package com.steiner.make_a_orm;

import jakarta.annotation.Nonnull;

public interface IToSQL {
    @Nonnull
    String toSQL();
}
