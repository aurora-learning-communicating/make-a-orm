package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import java.sql.Types;

public class TextColumn extends StringColumn {
    public TextColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String typeQuote() {
        return "text";
    }

    @Override
    public int sqlType() {
        return Types.LONGNVARCHAR;
    }
}
