package badasintended.cpas.client.widget;

import badasintended.cpas.api.SlotType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;

import static badasintended.cpas.client.ClientUtils.createArmorSlot;

@Environment(EnvType.CLIENT)
public class ArmorPanelWidget extends AbstractPanelWidget {

    public ArmorPanelWidget(int x, int y, int width, int height, PlayerInventory playerInventory) {
        super(x, y, width, height);

        for (int i = 0; i < 4; i++) {
            children().add(createArmorSlot(x + 7, i * 18 + y + 7, playerInventory, SlotType.of(i)));
        }

        children().add(createArmorSlot(x + 7, 4 * 18 + y + 11, playerInventory, SlotType.OFFHAND));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (Screen.hasControlDown()) {
            x = (int) Math.ceil(mouseX);
            y = (int) Math.ceil(mouseY);
        }
        return false;
    }

}