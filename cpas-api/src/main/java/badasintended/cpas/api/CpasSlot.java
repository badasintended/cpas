package badasintended.cpas.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface CpasSlot {

    default void setupContext(PlayerEntity player) {
    }

    @NotNull
    Identifier getTexture();

    @NotNull
    ItemStack getStack();

    @NotNull
    ItemStack setStack(ItemStack stack);

    boolean canEquip(ItemStack stack);

    default boolean canTakeItem() {
        return true;
    }

}
