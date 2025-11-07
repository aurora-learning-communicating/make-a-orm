package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import java.sql.Types;

public class CharacterVaryingColumn extends StringColumn {
    int length;

    public CharacterVaryingColumn(@Nonnull String name, @Nonnull Table fromTable, int length) {
        super(name, fromTable);
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
