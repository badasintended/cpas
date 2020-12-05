package badasintended.cpas.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface AccessorHandledScreen {

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Accessor
    int getBackgroundWidth();

    @Accessor
    int getBackgroundHeight();

    @Accessor
    PlayerInventory getPlayerInventory();

}
