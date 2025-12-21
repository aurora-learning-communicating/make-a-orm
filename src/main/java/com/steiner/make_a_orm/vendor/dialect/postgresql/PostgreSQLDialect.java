package com.steiner.make_a_orm.vendor.dialect.postgresql;

import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.vendor.DataTypeProvider;
import jakarta.annotation.Nonnull;

public class PostgreSQLDialect extends Dialect {
    public PostgreSQLDialect() {
        super("postgresql", new PostgreSQLDataTypeProvider());
    }
}
