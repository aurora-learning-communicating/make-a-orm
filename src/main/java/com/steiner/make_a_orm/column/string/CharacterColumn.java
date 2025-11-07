package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import java.sql.Types;

public class CharacterColumn extends StringColumn {
    int length;

    public CharacterColumn(@Nonnull String name, @Nonnull Table fromTable, int length) {
        super(name, fromTable);
        this.length = length;
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "char(%s)".formatted(length);
    }

    @Override
    public int sqlType() {
        return Types.CHAR;
    }
}
