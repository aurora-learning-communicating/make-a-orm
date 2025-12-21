package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.column.trait.predicate.IEqual;
import com.steiner.make_a_orm.column.trait.predicate.IInList;
import com.steiner.make_a_orm.column.trait.predicate.ILikeColumn;
import com.steiner.make_a_orm.column.trait.predicate.INullOrNot;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;

import java.sql.Types;

public class CharacterColumn extends StringColumn
        implements
        IEqual<String, CharacterColumn>,
        IInList<String, CharacterColumn>,
        ILikeColumn<CharacterColumn>,
        INullOrNot<String, CharacterColumn> {
    int length;

    public CharacterColumn(@Nonnull String name, @Nonnull Table fromTable, int length) {
        super(name, fromTable);
        this.length = length;
    }

    @Override
    @Nonnull
    public String typeQuote(@Nonnull Dialect dialect) {
        return dialect.dataTypeProvider.charType(length);
    }

    @Override
    public int sqlType() {
        return Types.CHAR;
    }

    @Nonnull
    @Override
    public CharacterColumn self() {
        return this;
    }
}
