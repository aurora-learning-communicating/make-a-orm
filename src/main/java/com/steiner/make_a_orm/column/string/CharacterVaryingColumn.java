package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.trait.predicate.IEqual;
import com.steiner.make_a_orm.column.trait.predicate.IInList;
import com.steiner.make_a_orm.column.trait.predicate.ILikeColumn;
import com.steiner.make_a_orm.column.trait.predicate.INullOrNot;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import java.sql.Types;

public class CharacterVaryingColumn extends StringColumn
        implements
        IEqual<String, CharacterVaryingColumn>,
        IInList<String, CharacterVaryingColumn>,
        ILikeColumn<CharacterVaryingColumn>,
        INullOrNot<String, CharacterVaryingColumn> {
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

    @Nonnull
    @Override
    public CharacterVaryingColumn self() {
        return this;
    }
}
