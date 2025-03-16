package com.steiner.make_a_orm.update;

import com.steiner.make_a_orm.column.Column;
import jakarta.annotation.Nonnull;

public final class ReassignExpression extends UpdateExpression {
    @Nonnull
    public Column<?> column;

    @Nonnull
    public String expression;

    public ReassignExpression(@Nonnull Column<?> column, @Nonnull String expression) {
        super(UpdateExpression.Type.Reassign);
        this.column = column;
        this.expression = expression;
    }

    @Override
    public String toSQL() {
        if (column.isNullable) {
            return "case when `%s` is not null then %s else `%s` end"
                    .formatted(column.name, expression, column.name);
        } else {
            return expression;
        }

    }
}
