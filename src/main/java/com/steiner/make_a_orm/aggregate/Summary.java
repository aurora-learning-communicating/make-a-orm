package com.steiner.make_a_orm.aggregate;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;

/**
 *
 * 要处理的类型太多了，不好用 泛型处理，直接 声明为 抽象类，让用户 自己来写 `read` 方法？
 * int -> bigint
 * numeric -> numeric
 * float -> double
 */
public abstract class Summary<T extends Number & Comparable<T>, R extends Number & Comparable<R>> extends Aggregate<R> {
    public Summary(@Nonnull Column<T> column) {
        super(column);
    }

    @Nonnull
    @Override
    public String alias() {
        return Quote.quoteAggregate("sum", column);
    }

    @Nonnull
    @Override
    public String toSQL() {
        return "sum(%s) as %s".formatted(Quote.quoteColumnStandalone(column), alias());
    }
}
