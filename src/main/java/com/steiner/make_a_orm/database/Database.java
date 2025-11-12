package com.steiner.make_a_orm.database;

import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLInitializeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public class Database {
    public static class Builder {
        @Nullable
        public Driver driver;
        @Nullable
        public String url;
        @Nullable
        public String username;
        @Nullable
        public String password;

        public Builder() {
            this.driver = null;
            this.url = null;
            this.username = null;
            this.password = null;
        }

        public Database build() {
            Objects.requireNonNull(driver, "driver cannot be null");
            Objects.requireNonNull(url, "url cannot be null");

            Database database = new Database(
                    Objects.requireNonNull(driver),
                    Objects.requireNonNull(url),
                    username,
                    password);

            try {
                DriverManager.registerDriver(database.driver);
                database.connection = DriverManager.getConnection(url, username, password);
                return database;
            } catch (SQLException exception) {
                throw new SQLInitializeException("get connection error").cause(exception);
            }
        }
    }

    public static Database builder(Consumer<Builder> consumer) {
        Builder builder = new Builder();
        consumer.accept(builder);
        return builder.build();
    }

    @Nonnull
    public Driver driver;
    @Nonnull
    public String url;
    @Nullable
    public String username;
    @Nullable
    public String password;
    @Nullable
    public Connection connection;

    private Database(@Nonnull Driver driver, @Nonnull String url, @Nullable String username, @Nullable String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = null;
    }
}
