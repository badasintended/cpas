package badasintended.cpas.api;

import java.util.function.Supplier;

import badasintended.cpas.impl.ConfigImpl;
import com.google.gson.Gson;

public interface Config<T> {

    static <T> Builder0<T> builder(Class<T> clazz) {
        return new ConfigImpl.Builder<>(clazz);
    }

    T get(boolean reload);

    void save(T instance);

    default T get() {
        return get(false);
    }

    default void save() {
        save(get());
    }

    interface Builder0<T> {

        Builder1<T> name(String name);

    }

    interface Builder1<T> {

        Builder1<T> gson(Gson gson);

        Builder1<T> factory(Supplier<T> factory);

        Config<T> build();

    }

}
