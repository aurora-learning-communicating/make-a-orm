package com.steiner.make_a_orm.where;

import com.steiner.make_a_orm.IToSQL;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import jakarta.annotation.Nonnull;
import java.util.stream.Collectors;

public class WhereTopStatement implements IToSQL {
    @Nonnull
    WhereStatement statement;

    public WhereTopStatement(@Nonnull WhereStatement statement) {
        this.statement = statement;
    }

    @Nonnull
    @Override
    public String toSQL() {
        StringBuilder stringBuilder = new StringBuilder();
        String firstSQL = statement.toSQL();
        stringBuilder.append(firstSQL).append(" ");

        if (statement.otherStatements != null) {
            String others = statement.otherStatements
                    .stream()
                    .map(WhereStatement::toSQL)
                    .collect(Collectors.joining(" "));

            stringBuilder.append(others);
        }

        return stringBuilder.toString();
    }
}
