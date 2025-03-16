package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

public abstract class StringColumn extends Column<String> {
    public StringColumn(String name) {
        super(name);
    }

    @Nonnull
    @Override
    public String format(@Nonnull String value) {
        String replacement = value.replace("'", "\\'");
        return "'%s'".formatted(replacement);
    }
}
