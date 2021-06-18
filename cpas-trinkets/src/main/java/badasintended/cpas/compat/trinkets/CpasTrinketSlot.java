package badasintended.cpas.compat.trinkets;

import badasintended.cpas.api.CpasSlot;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CpasTrinketSlot implements CpasSlot {

    private final String groupId;
    private final String slotId;
    private final SlotType slot;
    private final int index;

    private PlayerEntity player;

    @Nullable
    private TrinketInventory inventory;
    private SlotReference slotRef;

    CpasTrinketSlot(String groupId, String slotId, SlotType slot, int index) {
        this.groupId = groupId;
        this.slotId = slotId;
        this.slot = slot;
        this.index = index;
    }

    @Override
    public void setupContext(PlayerEntity player) {
        if (this.player != player) {
            this.player = player;
            this.inventory = null;
            this.slotRef = null;
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                inventory = component.getInventory().get(groupId).get(slotId);
                slotRef = new SlotReference(inventory, index);
            });
        }
    }

    @NotNull
    @Override
    public Identifier getTexture() {
        return slot.getIcon();
    }

    @NotNull
    @Override
    public ItemStack getStack() {
        return inventory != null ? inventory.getStack(index) : ItemStack.EMPTY;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        return inventory != null && TrinketSlot.canInsert(stack, slotRef, player);
    }

    @Override
    public @NotNull ItemStack setStack(ItemStack stack) {
        if (inventory == null) {
            return stack;
        }

        ItemStack last = getStack();
        inventory.setStack(index, stack);
        return last;
    }

}
