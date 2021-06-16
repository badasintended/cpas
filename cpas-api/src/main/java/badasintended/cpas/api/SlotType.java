package badasintended.cpas.api;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public enum SlotType {

    HEAD("head", 5, 39, EquipmentSlot.HEAD, "textures/item/empty_armor_slot_helmet.png"),
    CHEST("chest", 6, 38, EquipmentSlot.CHEST, "textures/item/empty_armor_slot_chestplate.png"),
    LEGS("legs", 7, 37, EquipmentSlot.LEGS, "textures/item/empty_armor_slot_leggings.png"),
    FEET("feet", 8, 36, EquipmentSlot.FEET, "textures/item/empty_armor_slot_boots.png"),
    OFFHAND("offhand", 45, 40, null, "textures/item/empty_armor_slot_shield.png");

    public static final SlotType[] VALUES = values();

    public final String id;
    public final int handlerId;
    public final int inventoryId;
    public final Identifier textureId;

    @Nullable
    public final EquipmentSlot equipmentSlot;

    SlotType(String id, int handlerId, int inventoryId, @Nullable EquipmentSlot equipmentSlot, String textureId) {
        this.id = id;
        this.handlerId = handlerId;
        this.inventoryId = inventoryId;
        this.equipmentSlot = equipmentSlot;
        this.textureId = new Identifier(textureId);
    }

    public static SlotType of(int id) {
        return VALUES[id];
    }

}
