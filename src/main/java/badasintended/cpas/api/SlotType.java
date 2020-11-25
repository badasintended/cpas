package badasintended.cpas.api;

import net.minecraft.util.Identifier;

public enum SlotType {

    HEAD(5, 39, new Identifier("textures/item/empty_armor_slot_helmet.png"), "head"),
    CHEST(6, 38, new Identifier("textures/item/empty_armor_slot_chestplate.png"), "chest"),
    LEGS(7, 37, new Identifier("textures/item/empty_armor_slot_leggings.png"), "legs"),
    FEET(8, 36, new Identifier("textures/item/empty_armor_slot_boots.png"), "feet"),
    OFFHAND(45, 40, new Identifier("textures/item/empty_armor_slot_shield.png"), "offhand");

    public static final SlotType[] VALUES = values();

    public final int handlerId;
    public final int inventoryId;
    public final Identifier textureId;
    public final String trinketId;

    SlotType(int handlerId, int inventoryId, Identifier textureId, String trinketId) {
        this.handlerId = handlerId;
        this.inventoryId = inventoryId;
        this.textureId = textureId;
        this.trinketId = trinketId;
    }

    public static SlotType of(int id) {
        return VALUES[id];
    }

}
