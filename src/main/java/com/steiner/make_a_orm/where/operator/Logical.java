package com.steiner.make_a_orm.where.operator;

public enum Logical {
    And("and"),
    Or("or");

    public final String sign;
    Logical(String sign) {
        this.sign = sign;
    }
}
