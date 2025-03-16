package com.steiner.make_a_orm.update;

public abstract class UpdateExpression {
    public enum Type {
        Set,
        Reassign
    }

    public Type type;

    public UpdateExpression(Type type) {
        this.type = type;
    }

    public abstract String toSQL();
}
