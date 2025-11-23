package com.steiner.make_a_orm;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Environment {
    private static final Yaml yaml = new Yaml();

    @Nonnull
    public static Environment loadFrom(@Nonnull InputStream input) {
        Map<String, Object> topLevelData = yaml.load(input);
        Map<String, Object> data = (Map<String, Object>) topLevelData.getOrDefault("database", null);

        @Nullable String url = (String) data.getOrDefault("url", null);
        @Nullable String username = (String) data.getOrDefault("username", null);
        @Nullable String password = (String) data.getOrDefault("password", null);

        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }

        return new Environment(url, username, password);
    }


    @Nonnull
    public String url;

    @Nullable
    public String username;

    @Nullable
    public String password;

    public Environment(@Nonnull String url, @Nullable String username, @Nullable String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
