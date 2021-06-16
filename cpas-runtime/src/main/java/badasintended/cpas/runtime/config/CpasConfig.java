package badasintended.cpas.runtime.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import badasintended.cpas.api.Config;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public final class CpasConfig {

    private static final Config<CpasConfig> CONFIG = Config
        .builder(CpasConfig.class)
        .name("cpas")
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Entry.class, new Entry.Serializer())
            .create())
        .build();

    public static CpasConfig get() {
        return CONFIG.get();
    }

    public static Entry getEntry(HandledScreen<?> screen) {
        String name = screen.getClass().getName();
        CpasConfig config = get();
        if (!config.entries.containsKey(name)) {
            config.entries.put(name, new Entry());
        }
        return config.entries.get(name);
    }

    public static void save() {
        CONFIG.save();
    }

    private boolean showHelp = true;

    public Map<String, Entry> entries = new HashMap<>();

    public boolean isShowHelp() {
        return showHelp;
    }

    public void setShowHelp(boolean showHelp) {
        this.showHelp = showHelp;
    }

    public static class Entry {

        private boolean enabled = true;
        private boolean auto = true;
        private int x = 0;
        private int y = 0;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAuto() {
            return auto;
        }

        public void setAuto(boolean auto) {
            this.auto = auto;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public static class Serializer implements JsonDeserializer<Entry>, JsonSerializer<Entry> {

            @Override
            public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Entry result = new Entry();

                if (json.isJsonPrimitive()) {
                    boolean b = json.getAsBoolean();
                    result.enabled = result.auto = b;
                } else {
                    JsonArray array = json.getAsJsonArray();
                    if (array.size() != 2) {
                        throw new JsonParseException("invalid array size of " + array.size() + ", 2 is required", new IllegalArgumentException());
                    }
                    result.enabled = true;
                    result.auto = false;
                    result.x = array.get(0).getAsInt();
                    result.y = array.get(1).getAsInt();
                }
                return result;
            }

            @Override
            public JsonElement serialize(Entry src, Type typeOfSrc, JsonSerializationContext context) {
                if (!src.enabled) {
                    return new JsonPrimitive(false);
                } else if (src.auto) {
                    return new JsonPrimitive(true);
                } else {
                    JsonArray array = new JsonArray();
                    array.add(src.x);
                    array.add(src.y);
                    return array;
                }
            }

        }

    }

}
