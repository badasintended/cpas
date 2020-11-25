package badasintended.cpas;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public final class Utils {

    public static boolean hasMod(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static void slotClick(PlayerEntity player, int slot) {
        player.playerScreenHandler.onSlotClick(slot, 0, SlotActionType.PICKUP, player);
    }

    public static void equipTrinket(PlayerEntity player, int slot) {
        PlayerInventory playerInv = player.inventory;
        Inventory inv = TrinketsApi.getTrinketsInventory(player);
        ItemStack stack = inv.getStack(slot).copy();
        inv.setStack(slot, playerInv.getCursorStack().copy());
        playerInv.setCursorStack(stack);
    }

}
