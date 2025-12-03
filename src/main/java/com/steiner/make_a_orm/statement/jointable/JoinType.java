package com.steiner.make_a_orm.statement.jointable;

import jakarta.annotation.Nonnull;

public enum JoinType {
    Left("left join"),
    Right("right join"),
    Inner("inner join"),
    Outer("full outer join");

    public final String sign;
    JoinType(@Nonnull String sign) {
        this.sign = sign;
    }
}
