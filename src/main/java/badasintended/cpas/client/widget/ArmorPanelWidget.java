package badasintended.cpas.client.widget;

import badasintended.cpas.api.SlotType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;

import static badasintended.cpas.client.ClientUtils.createArmorSlot;

@Environment(EnvType.CLIENT)
public class ArmorPanelWidget extends AbstractPanelWidget {

    public ArmorPanelWidget(int x, int y, PlayerInventory playerInventory) {
        super(x, y, 32, 5 * 18 + 18);

        for (int i = 0; i < 4; i++) {
            children().add(createArmorSlot(x + 7, i * 18 + y + 7, playerInventory, SlotType.of(i)));
        }

        children().add(createArmorSlot(x + 7, 4 * 18 + y + 11, playerInventory, SlotType.OFFHAND));
    }

}
