package com.steiner.make_a_orm.database;

import com.steiner.make_a_orm.utils.result.Result;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        public Builder driver(@Nonnull Driver driver) {
            this.driver = driver;
            return this;
        }

        public Builder url(@Nonnull String url) {
            this.url = url;
            return this;
        }

        public Builder username(@Nonnull String username) {
            this.username = username;
            return this;
        }

        public Builder password(@Nonnull String password) {
            this.password = password;
            return this;
        }

        public Result<Database, SQLException> build() {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field: fields) {
                try {
                    Object value = field.get(this);
                    if (value == null) {
                        throw new IllegalAccessException("there is null field in the builder");
                    }
                } catch (IllegalAccessException e) {
                    SQLException sqlException = new SQLException(e);
                    return Result.Err(sqlException);
                }
            }

            Database database = new Database(driver, url, username, password);
            return Result.from(() -> {
               DriverManager.registerDriver(database.driver);
               database.connection = DriverManager.getConnection(url, username, password);
               return database;
            });
        }

    }

    @Nonnull
    public Driver driver;

    @Nonnull
    public String url;

    @Nonnull
    public String username;

    @Nonnull
    public String password;

    @Nullable
    public Connection connection;

    private Database(@Nonnull Driver driver, @Nonnull String url, @Nonnull String username, @Nonnull String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    public static Builder builder() {
        return new Builder();
    }
}
