package badasintended.cpas.client.widget;

import badasintended.cpas.Cpas;
import badasintended.cpas.api.SlotType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

import static badasintended.cpas.Utils.slotClick;
import static badasintended.cpas.client.ClientUtils.bind;
import static badasintended.cpas.client.ClientUtils.c2s;
import static badasintended.cpas.client.ClientUtils.drawItem;
import static badasintended.cpas.client.ClientUtils.drawNinePatch;
import static badasintended.cpas.client.ClientUtils.renderTooltip;

@Environment(EnvType.CLIENT)
public class ArmorSlotWidget extends AbstractButtonWidget {

    protected final PlayerEntity player;
    protected final PlayerInventory inventory;
    protected final SlotType slot;

    public ArmorSlotWidget(int x, int y, PlayerInventory inventory, SlotType slot) {
        super(x, y, 18, 18, LiteralText.EMPTY);

        this.player = inventory.player;
        this.inventory = inventory;
        this.slot = slot;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        bind();
        drawNinePatch(matrices, x, y, width, height, 16, 0, 1, 14);

        ItemStack stack = inventory.getStack(slot.inventoryId);
        if (stack.isEmpty()) {
            bind(slot.textureId);
            drawTexture(matrices, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
        } else {
            drawItem(inventory.getStack(slot.inventoryId), x + 1, y + 1);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        boolean hovered = x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height;
        if (hovered) {
            fill(matrices, x + 1, y + 1, x + 17, y + 17, 0x80ffffff);
            if (inventory.getCursorStack().isEmpty()) {
                renderTooltip(matrices, mouseX, mouseY, inventory.getStack(slot.inventoryId), player);
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        slotClick(player, slot.handlerId);
        c2s(Cpas.ARMOR_SLOT_CLICK, buf -> buf.writeVarInt(slot.handlerId));
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

}
