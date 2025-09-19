package com.steiner.make_a_orm.column.string;

import jakarta.annotation.Nonnull;
import java.sql.Types;

public class CharacterVaryingColumn extends StringColumn {
    int length;

    public CharacterVaryingColumn(@Nonnull String name, int length) {
        super(name);
        this.length = length;
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "varchar(%s)".formatted(length);
    }

    @Override
    public int sqlType() {
        return Types.VARCHAR;
    }
}
