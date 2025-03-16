package com.steiner.make_a_orm.column.trait;

import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

/*
这玩意要注意 character 类型的长度限制
 */
public interface IPlusColumn<T, E extends Column<T>> {
    @Nonnull
    E self();
}
