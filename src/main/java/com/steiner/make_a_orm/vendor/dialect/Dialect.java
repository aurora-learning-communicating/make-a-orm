package com.steiner.make_a_orm.vendor.dialect;

import com.steiner.make_a_orm.vendor.DataTypeProvider;
import jakarta.annotation.Nonnull;

public abstract class Dialect {
    @Nonnull
    public String name;

    @Nonnull
    public DataTypeProvider dataTypeProvider;

    public Dialect(@Nonnull String name, @Nonnull DataTypeProvider dataTypeProvider) {
        this.name = name;
        this.dataTypeProvider = dataTypeProvider;
    }
}
