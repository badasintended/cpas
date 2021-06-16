package badasintended.cpas.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import badasintended.cpas.api.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigImpl<T> implements Config<T> {

    private static final Gson DEFAULT_GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    static {
        try {
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("cpas"));
        } catch (IOException e) {
            throw new RuntimeException("Exception when creating config folder", e);
        }
    }

    private final Class<T> clazz;
    private final Path path;
    private final Gson gson;
    private final Supplier<T> factory;

    private T instance;

    private ConfigImpl(Class<T> clazz, String name, Gson gson, Supplier<T> factory) {
        this.clazz = clazz;
        this.path = FabricLoader.getInstance().getConfigDir().resolve("cpas/" + name + ".json");
        this.gson = gson;
        this.factory = factory;

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T get(boolean reload) {
        if (instance == null || reload) {
            try {
                if (Files.notExists(path)) {
                    instance = factory.get();
                    Files.write(path, gson.toJson(instance).getBytes());
                } else {
                    instance = gson.fromJson(String.join("\n", Files.readAllLines(path)), clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsonParseException(e);
            }
            save(instance);
        }
        return instance;
    }

    @Override
    public void save(T instance) {
        try {
            Files.write(path, gson.toJson(instance).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder<T> implements Builder0<T>, Builder1<T> {

        private final Class<T> clazz;

        private String name;
        private Gson gson;
        private Supplier<T> factory;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;

            this.gson = DEFAULT_GSON;
            this.factory = () -> {
                try {
                    return clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create new config instance", e);
                }
            };
        }

        @Override
        public Builder1<T> name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder1<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        @Override
        public Builder1<T> factory(Supplier<T> factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public Config<T> build() {
            return new ConfigImpl<>(clazz, name, gson, factory);
        }

    }

}
