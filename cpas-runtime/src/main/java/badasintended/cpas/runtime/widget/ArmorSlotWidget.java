package badasintended.cpas.runtime.widget;

import badasintended.cpas.api.CpasSlot;
import badasintended.cpas.api.SlotType;
import badasintended.cpas.impl.CpasRegistrarImpl;
import badasintended.cpas.runtime.Cpas;
import badasintended.cpas.runtime.CpasClient;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

import static badasintended.cpas.runtime.CpasClient.bindTexture;
import static badasintended.cpas.runtime.CpasClient.drawItem;
import static badasintended.cpas.runtime.CpasClient.drawNinePatch;
import static badasintended.cpas.runtime.CpasClient.getItemRenderer;
import static badasintended.cpas.runtime.CpasClient.renderTooltip;

@Environment(EnvType.CLIENT)
public class ArmorSlotWidget extends FakeButtonWidget {

    private final ScreenHandler handler;
    protected final PlayerEntity player;
    protected final PlayerInventory inventory;
    protected final SlotType type;

    private final CpasRegistrarImpl.Registry<CpasSlot> slots;

    private final int minX;
    private boolean wasHovered = false;
    private CpasSlot hoveredSlot = null;
    private int hoveredX = 0;
    private ItemStack hoveredStack = ItemStack.EMPTY;


    public ArmorSlotWidget(int x, int y, PlayerInventory inventory, ScreenHandler handler, SlotType type) {
        super(x, y, 18, 18);

        CpasClient.lazyInitPlugins();

        this.handler = handler;
        this.player = inventory.player;
        this.inventory = inventory;
        this.type = type;

        this.slots = CpasRegistrarImpl.REGISTRY.get(type);
        slots.int2ObjectEntrySet().fastForEach(e -> e.getValue().setupContext(player));

        minX = x - slots.size() * 18;
    }

    private void renderRegularSlot(MatrixStack matrices) {
        bindTexture();
        drawNinePatch(matrices, x, y, width, height, 16, 0, 1, 14);

        ItemStack stack = inventory.getStack(type.inventoryId);
        if (stack.isEmpty()) {
            bindTexture(type.textureId);
            drawTexture(matrices, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
        } else {
            drawItem(inventory.getStack(type.inventoryId), x + 1, y + 1);
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        hoveredSlot = slots.get((x + width - (mouseX + 1)) / 18 - 1);

        if (wasHovered) {
            wasHovered = minX <= mouseX && mouseX < x + 18 && y <= mouseY && mouseY < y + 18;
        } else {
            wasHovered = x <= mouseX && mouseX < x + 18 && y <= mouseY && mouseY < y + 18;
        }

        hoveredX = -999;
        hoveredStack = ItemStack.EMPTY;

        if (wasHovered && slots.size() > 0) {
            matrices.push();
            matrices.translate(0, 0, 200);
            getItemRenderer().zOffset += 200;

            bindTexture();
            drawNinePatch(matrices, minX - 4, y - 4, (slots.size() + 1) * 18 + 8, 26, 0, 0, 4, 8);

            for (Int2ObjectMap.Entry<CpasSlot> entry : slots.int2ObjectEntrySet()) {
                int i = entry.getIntKey();
                CpasSlot slot = entry.getValue();

                int trinketX = x - (i * 18 + 18);

                bindTexture();
                drawNinePatch(matrices, trinketX, y, 18, 18, 16, 0, 1, 14);

                ItemStack stack = slot.getStack();

                if (slot == hoveredSlot) {
                    hoveredStack = stack;
                    hoveredX = trinketX;
                }

                if (stack.isEmpty()) {
                    bindTexture(slot.getTexture());
                    drawTexture(matrices, trinketX + 1, y + 1, 16, 16, 0, 0, 16, 16, 16, 16);
                } else {
                    drawItem(stack, trinketX + 1, y + 1);
                }
            }

            renderRegularSlot(matrices);

            getItemRenderer().zOffset -= 200;
            matrices.pop();
        } else {
            renderRegularSlot(matrices);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        if (wasHovered) {
            matrices.push();
            matrices.translate(0, 0, 300);
            getItemRenderer().zOffset += 200;

            fill(matrices, hoveredX + 1, y + 1, hoveredX + 17, y + 17, 0x80ffffff);
            if (handler.getCursorStack().isEmpty()) {
                renderTooltip(matrices, mouseX, mouseY, hoveredStack, player);
            } else {
                // when you see this, yes it redraws the cursor stack
                getItemRenderer().zOffset += 100;
                drawItem(handler.getCursorStack(), mouseX - 8, mouseY - 8);
                getItemRenderer().zOffset -= 100;
            }

            boolean hovered = x <= mouseX && mouseX < x + width && y <= mouseY && mouseY < y + height;
            if (hovered) {
                fill(matrices, x + 1, y + 1, x + 17, y + 17, 0x80ffffff);
                if (handler.getCursorStack().isEmpty()) {
                    renderTooltip(matrices, mouseX, mouseY, inventory.getStack(type.inventoryId), player);
                }
            }

            getItemRenderer().zOffset -= 200;
            matrices.pop();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(handler.syncId);
        buf.writeEnumConstant(type);
        buf.writeIdentifier(hoveredSlot == null ? Cpas.REGULAR_SLOT : slots.getId(hoveredSlot));
        ClientPlayNetworking.send(Cpas.SLOT_CLICK, buf);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return wasHovered;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return wasHovered;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

}
