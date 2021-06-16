package badasintended.cpas.impl;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import badasintended.cpas.api.CpasRegistrar;
import badasintended.cpas.api.CpasSlot;
import badasintended.cpas.api.SlotType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public enum CpasRegistrarImpl implements CpasRegistrar {

    INSTANCE;

    public static final Map<SlotType, Registry<CpasSlot>> REGISTRY = Util.make(new EnumMap<>(SlotType.class), map -> {
        for (SlotType type : SlotType.VALUES) {
            map.put(type, new Registry<>());
        }
    });

    @Override
    public void addSlot(SlotType type, Identifier slotId, CpasSlot slot) {
        REGISTRY.get(type).put(slotId, slot);
    }

    public static class Registry<T> {

        private final Int2ObjectOpenHashMap<T> int2Entry = new Int2ObjectOpenHashMap<>();
        private final Map<Identifier, T> id2Entry = new LinkedHashMap<>();
        private final Map<T, Identifier> entry2Id = new LinkedHashMap<>();

        private int rawId = 0;

        void put(Identifier id, T entry) {
            int2Entry.put(rawId++, entry);
            id2Entry.put(id, entry);
            entry2Id.put(entry, id);
        }

        public void clear() {
            rawId = 0;
            int2Entry.clear();
            id2Entry.clear();
            entry2Id.clear();
        }

        @Nullable
        public T get(int rawId) {
            return int2Entry.get(rawId);
        }

        @Nullable
        public T get(Identifier id) {
            return id2Entry.get(id);
        }

        @Nullable
        public Identifier getId(T entry) {
            return entry2Id.get(entry);
        }

        public int size() {
            return id2Entry.size();
        }

        public Int2ObjectMap.FastEntrySet<T> int2ObjectEntrySet() {
            return int2Entry.int2ObjectEntrySet();
        }

    }

}
