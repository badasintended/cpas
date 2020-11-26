package badasintended.cpas.client;

import java.util.function.Consumer;

import badasintended.cpas.api.SlotType;
import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.toast.HelpToast;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.ArmorSlotWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import badasintended.cpas.client.widget.TrinketSlotWidget;
import badasintended.cpas.config.CpasConfig;
import badasintended.cpas.mixin.AccessorHandledScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static badasintended.cpas.Utils.hasMod;

@Environment(EnvType.CLIENT)
public final class ClientUtils {

    private static final Identifier GUI_TEXTURE = new Identifier("cpas", "textures/gui/gui.png");

    public static MinecraftClient client() {
        return MinecraftClient.getInstance();
    }

    public static void bind() {
        bind(GUI_TEXTURE);
    }

    public static void bind(Identifier id) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(id);
    }

    public static void drawNinePatch(MatrixStack matrices, int x, int y, int w, int h, float u, float v, int ltrb, int cm) {
        drawNinePatch(matrices, x, y, w, h, u, v, ltrb, cm, ltrb);
    }

    public static void drawNinePatch(MatrixStack matrices, int x, int y, int w, int h, float u, float v, int lt, int cm, int rb) {
        drawNinePatch(matrices, x, y, w, h, u, v, lt, cm, rb, lt, cm, rb);
    }

    public static void drawNinePatch(MatrixStack matrices, int x, int y, int w, int h, float u, float v, int l, int c, int r, int t, int m, int b) {
        DrawableHelper.drawTexture(matrices, x, y, l, t, u, v, l, t, 256, 256);
        DrawableHelper.drawTexture(matrices, x + l, y, w - l - r, t, u + l, v, c, t, 256, 256);
        DrawableHelper.drawTexture(matrices, x + w - r, y, r, t, u + l + c, v, r, t, 256, 256);

        DrawableHelper.drawTexture(matrices, x, y + t, l, h - t - b, u, v + t, l, m, 256, 256);
        DrawableHelper.drawTexture(matrices, x + l, y + t, w - l - r, h - t - b, u + l, v + t, c, m, 256, 256);
        DrawableHelper.drawTexture(matrices, x + w - r, y + t, r, h - t - b, u + l + c, v + t, r, m, 256, 256);

        DrawableHelper.drawTexture(matrices, x, y + h - b, l, b, u, v + t + m, l, b, 256, 256);
        DrawableHelper.drawTexture(matrices, x + l, y + h - b, w - l - r, b, u + l, v + t + m, c, b, 256, 256);
        DrawableHelper.drawTexture(matrices, x + w - r, y + h - b, r, b, u + l + c, v + t + m, r, b, 256, 256);
    }

    public static void drawItem(ItemStack stack, int x, int y) {
        drawItem(stack, x, y, null);
    }

    public static void drawItem(ItemStack stack, int x, int y, String text) {
        itemRenderer().renderGuiItemIcon(stack, x, y);
        itemRenderer().renderGuiItemOverlay(client().textRenderer, stack, x, y, text);
    }

    public static ItemRenderer itemRenderer() {
        return client().getItemRenderer();
    }

    public static void drawText(MatrixStack matrices, Text text, int x, int y) {
        drawText(matrices, text, x, y, 0xFFFFFFFF);
    }

    public static void drawText(MatrixStack matrices, Text text, int x, int y, int color) {
        client().textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public static void drawText(MatrixStack matrices, String text, int x, int y) {
        drawText(matrices, text, x, y, 0xFFFFFFFF);
    }

    public static void drawText(MatrixStack matrices, String text, int x, int y, int color) {
        client().textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public static TextRenderer textRenderer() {
        return client().textRenderer;
    }

    @SuppressWarnings("ConstantConditions")
    public static void renderTooltip(MatrixStack matrices, int x, int y, ItemStack stack, PlayerEntity player) {
        if (!stack.isEmpty())
            client().currentScreen.renderTooltip(matrices, stack.getTooltip(player, () -> client().options.advancedItemTooltips), x, y);
    }

    public static void c2s(Packet<?> packet) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
    }

    public static void c2s(Identifier id, Consumer<PacketByteBuf> consumer) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        consumer.accept(buf);
        ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf);
    }

    public static ArmorSlotWidget createArmorSlot(int x, int y, PlayerInventory inventory, SlotType slot) {
        return hasMod("trinkets") ? new TrinketSlotWidget(x, y, inventory, slot) : new ArmorSlotWidget(x, y, inventory, slot);
    }

    public static String getScreenName(HandledScreen<?> screen) {
        return screen.getClass().getName();
    }

    public static void injectCpasWidget(Screen screen) {
        if (screen instanceof HandledScreen<?>) {
            if (CpasConfig.get().isShowHelp()) {
                client().getToastManager().add(new HelpToast());
                CpasConfig.get().setShowHelp(false);
                CpasConfig.save();
            }

            int scaledW = client().getWindow().getScaledWidth();
            int scaledH = client().getWindow().getScaledHeight();

            CpasTarget target = (CpasTarget) screen;
            HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
            AccessorHandledScreen accessor = (AccessorHandledScreen) handledScreen;

            if (!(handledScreen instanceof InventoryScreen || handledScreen instanceof CreativeInventoryScreen)) {
                CpasConfig.Entry entry = CpasConfig.getEntry(handledScreen);

                target.cpas$setEditorScreen(new EditorScreenWidget(scaledW, scaledH, entry, handledScreen));

                if (entry.isEnabled()) {
                    int panelX;
                    int panelY;

                    if (entry.isAuto()) {
                        panelX = accessor.getX() - 35;
                        panelY = accessor.getY() + accessor.getBackgroundHeight() - ((5 * 18) + 18);
                    } else {
                        panelX = scaledW / 2 + entry.getX();
                        panelY = scaledH / 2 + entry.getY();
                    }

                    target.cpas$setArmorPanel(new ArmorPanelWidget(panelX, panelY, 32, 5 * 18 + 18, accessor.getPlayerInventory()));
                } else {
                    target.cpas$setArmorPanel(null);
                }
            }
        }
    }

}
