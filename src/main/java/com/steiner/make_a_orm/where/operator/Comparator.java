package com.steiner.make_a_orm.where.operator;

public enum Comparator {
    Less("<"),
    LessEq("<="),
    Greater(">"),
    GreaterEq(">=");

    public final String sign;
    Comparator(String sign) {
        this.sign = sign;
    }
}
