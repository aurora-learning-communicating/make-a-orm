package com.steiner.make_a_orm.column.string;

import com.steiner.make_a_orm.Errors;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.util.Quote;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class StringColumn extends Column<String> {
    public StringColumn(@Nonnull String name, @Nonnull Table fromTable) {
        super(name, fromTable);
    }

    @Nonnull
    @Override
    public String format(@Nonnull String value) {
        String replacement = value.replace("'", "\\'");
        return Quote.quoteString(replacement);
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull String value) throws SQLException {
        statement.setString(index, value);
    }

    @Override
    public @Nullable String read(@NotNull ResultSet resultSet) throws SQLException {
        @Nullable Object value = resultSet.getObject(name);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case String result -> result;
            case java.sql.Clob result -> {
                Reader reader = result.getCharacterStream();
                StringWriter writer = new StringWriter();

                try {
                    int bufferSize = 8 * 1024;
                    char[] buffer = new char[bufferSize];
                    int chars = reader.read(buffer);
                    while (chars >= 0) {
                        writer.write(buffer, 0, chars);
                        chars = reader.read(buffer);
                    }

                } catch (IOException exception) {
                    throw new SQLRuntimeException("io exception").cause(exception);
                }

                yield writer.toString();
            }

            default -> throw Errors.UnExpectedValueType(value);
        };
    }
}
