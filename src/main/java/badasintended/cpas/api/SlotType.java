package badasintended.cpas.api;

import net.minecraft.util.Identifier;

public enum SlotType {

    HEAD("head", 5, 39, new Identifier("textures/item/empty_armor_slot_helmet.png")),
    CHEST("chest", 6, 38, new Identifier("textures/item/empty_armor_slot_chestplate.png")),
    LEGS("legs", 7, 37, new Identifier("textures/item/empty_armor_slot_leggings.png")),
    FEET("feet", 8, 36, new Identifier("textures/item/empty_armor_slot_boots.png")),
    OFFHAND("offhand", 45, 40, new Identifier("textures/item/empty_armor_slot_shield.png"));

    public static final SlotType[] VALUES = values();

    public final String id;
    public final int handlerId;
    public final int inventoryId;
    public final Identifier textureId;

    SlotType(String id, int handlerId, int inventoryId, Identifier textureId) {
        this.id = id;
        this.handlerId = handlerId;
        this.inventoryId = inventoryId;
        this.textureId = textureId;
    }

    public static SlotType of(int id) {
        return VALUES[id];
    }

}
