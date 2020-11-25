package badasintended.cpas.client.widget;

import badasintended.cpas.Cpas;
import badasintended.cpas.api.SlotType;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import static badasintended.cpas.Utils.equipTrinket;
import static badasintended.cpas.client.ClientUtils.bind;
import static badasintended.cpas.client.ClientUtils.c2s;
import static badasintended.cpas.client.ClientUtils.drawItem;
import static badasintended.cpas.client.ClientUtils.drawNinePatch;
import static badasintended.cpas.client.ClientUtils.itemRenderer;
import static badasintended.cpas.client.ClientUtils.renderTooltip;

@Environment(EnvType.CLIENT)
public class TrinketSlotWidget extends ArmorSlotWidget {

    private final Int2ObjectOpenHashMap<TrinketSlots.Slot> trinketSlots = new Int2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<TrinketSlots.Slot> trinketSlotNumbers = new Object2IntOpenHashMap<>();

    private boolean wasHovered = false;

    private final int trinketX;

    private TrinketSlots.Slot hoveredSlot = null;

    private int hoveredX = 0;
    private ItemStack hoveredStack = ItemStack.EMPTY;

    public TrinketSlotWidget(int x, int y, PlayerInventory inventory, SlotType slot) {
        super(x, y, inventory, slot);

        int i = 0;
        int j = 0;
        for (TrinketSlots.SlotGroup slotGroup : TrinketSlots.slotGroups) {
            for (TrinketSlots.Slot trinketSlot : slotGroup.slots) {
                if (slotGroup.getName().equals(slot.trinketId)) {
                    trinketSlots.put(j, trinketSlot);
                    trinketSlotNumbers.put(trinketSlot, i);
                    j++;
                }
                i++;
            }
        }

        trinketX = x - trinketSlots.size() * 18;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        hoveredSlot = trinketSlots.get((x + width - mouseX) / 18 - 1);

        if (wasHovered) {
            wasHovered = trinketX <= mouseX && mouseX < x + 18 && y <= mouseY && mouseY < y + 18;
        } else {
            wasHovered = x <= mouseX && mouseX < x + 18 && y <= mouseY && mouseY < y + 18;
        }

        hoveredX = -999;
        hoveredStack = ItemStack.EMPTY;

        if (wasHovered && trinketSlots.size() > 0) {
            matrices.push();
            matrices.translate(0, 0, 200);
            itemRenderer().zOffset += 200;

            bind();
            drawNinePatch(matrices, trinketX - 4, y - 4, (trinketSlots.size() + 1) * 18 + 8, 26, 0, 0, 4, 8);

            for (Int2ObjectMap.Entry<TrinketSlots.Slot> entry : trinketSlots.int2ObjectEntrySet()) {
                int i = entry.getIntKey();
                TrinketSlots.Slot trinketSlot = entry.getValue();

                int trinketX = x - (i * 18 + 18);

                bind();
                drawNinePatch(matrices, trinketX, y, 18, 18, 16, 0, 1, 14);

                ItemStack trinketStack = TrinketsApi.getTrinketComponent(player).getStack(slot.trinketId, trinketSlot.getName());

                if (trinketSlot == hoveredSlot) {
                    hoveredStack = trinketStack;
                    hoveredX = trinketX;
                }

                if (trinketStack.isEmpty()) {
                    bind(trinketSlot.texture);
                    drawTexture(matrices, trinketX + 1, y + 1, 16, 16, 0, 0, 16, 16, 16, 16);
                } else {
                    drawItem(trinketStack, trinketX + 1, y + 1);
                }
            }

            super.renderButton(matrices, mouseX, mouseY, delta);

            itemRenderer().zOffset -= 200;
            matrices.pop();
        } else {
            super.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        if (wasHovered) {
            matrices.push();
            matrices.translate(0, 0, 200);
            itemRenderer().zOffset += 200;

            fill(matrices, hoveredX + 1, y + 1, hoveredX + 17, y + 17, 0x80ffffff);
            if (inventory.getCursorStack().isEmpty()) {
                renderTooltip(matrices, mouseX, mouseY, hoveredStack, player);
            } else {
                // when you see this, yes it redraws the cursor stack
                drawItem(inventory.getCursorStack(), mouseX - 8, mouseY - 8);
            }

            super.renderToolTip(matrices, mouseX, mouseY);

            itemRenderer().zOffset -= 200;
            matrices.pop();
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return active && visible && wasHovered;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return wasHovered;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (hoveredSlot != null) {
            ItemStack cursor = player.inventory.getCursorStack();
            if (cursor.isEmpty() || hoveredSlot.canEquip.apply(hoveredSlot, cursor)) {
                int n = trinketSlotNumbers.getInt(hoveredSlot);
                equipTrinket(player, n);
                c2s(Cpas.TRINKET_SLOT_CLICK, buf -> buf.writeVarInt(n));
            }
        } else {
            super.onClick(mouseX, mouseY);
        }
    }

}
