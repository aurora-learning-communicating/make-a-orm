package com.steiner.make_a_orm.where.operator;

public enum Equality {
    Eq("="),
    NotEq("<>");

    public final String sign;
    Equality(String sign) {
        this.sign = sign;
    }
}
