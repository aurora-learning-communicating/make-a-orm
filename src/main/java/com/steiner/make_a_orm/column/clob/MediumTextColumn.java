package com.steiner.make_a_orm.column.clob;

import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MediumTextColumn extends ClobColumn {
    public MediumTextColumn(String name) {
        super(name);
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull Reader value) {
        try {
            statement.setCharacterStream(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "mediumtext";
    }
}
