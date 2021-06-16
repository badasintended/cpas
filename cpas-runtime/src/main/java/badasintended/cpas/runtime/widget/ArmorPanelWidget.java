package badasintended.cpas.runtime.widget;

import badasintended.cpas.api.SlotType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

@Environment(EnvType.CLIENT)
public class ArmorPanelWidget extends AbstractPanelWidget {

    public ArmorPanelWidget(int x, int y, PlayerInventory playerInventory, ScreenHandler handler) {
        super(x, y, 32, 108);

        for (int i = 0; i < 4; i++) {
            children().add(new ArmorSlotWidget(x + 7, i * 18 + y + 7, playerInventory, handler, SlotType.of(i)));
        }

        children().add(new ArmorSlotWidget(x + 7, 4 * 18 + y + 11, playerInventory, handler, SlotType.OFFHAND));
    }

}
