package badasintended.cpas.runtime;

import java.util.function.BiFunction;

import badasintended.cpas.runtime.config.CpasConfig;
import badasintended.cpas.runtime.duck.CpasTarget;
import badasintended.cpas.runtime.mixin.AccessorHandledScreen;
import badasintended.cpas.runtime.mixin.AccessorScreen;
import badasintended.cpas.runtime.toast.HelpToast;
import badasintended.cpas.runtime.widget.AbstractPanelWidget;
import badasintended.cpas.runtime.widget.ArmorPanelWidget;
import badasintended.cpas.runtime.widget.EditorScreenWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Environment(EnvType.CLIENT)
public class CpasClient implements ClientModInitializer {

    public static final KeyBinding EDIT = new KeyBinding("key.cpas.editor", GLFW.GLFW_KEY_J, "key.cpas");

    private static final Identifier GUI_TEXTURE = new Identifier("cpas", "textures/gui/gui.png");

    private static boolean pluginLoaded = false;

    @Nullable
    public static AutoPlacer autoPlacer = null;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(EDIT);

        ClientPlayConnectionEvents.INIT.register((handler, client) -> pluginLoaded = false);

        ClientPlayNetworking.registerGlobalReceiver(Cpas.CLEAR_REGISTRY, (client, handler, buf, responseSender) ->
            client.execute(() -> pluginLoaded = false));

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof CpasTarget target) {
                ScreenKeyboardEvents.allowKeyPress(screen).register(CpasClient::onKey);
                ((AccessorScreen) screen).callAddDrawable((matrices, mouseX, mouseY, delta) -> renderCpas(target, matrices, mouseX, mouseY, delta));
                injectCpasWidget(screen, scaledWidth, scaledHeight);
            }
        });
    }

    public static void lazyInitPlugins() {
        if (!pluginLoaded) {
            Cpas.initPlugins();
            pluginLoaded = true;
        }
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static ItemRenderer getItemRenderer() {
        return getClient().getItemRenderer();
    }

    public static void bindTexture() {
        bindTexture(GUI_TEXTURE);
    }

    public static void bindTexture(Identifier id) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, id);
    }

    public static void drawNinePatch(MatrixStack matrices, int x, int y, int w, int h, float u, float v, int s, int c) {
        drawTexture(matrices, x, y, s, s, u, v, s, s, 256, 256);
        drawTexture(matrices, x + s, y, w - s - s, s, u + s, v, c, s, 256, 256);
        drawTexture(matrices, x + w - s, y, s, s, u + s + c, v, s, s, 256, 256);

        drawTexture(matrices, x, y + s, s, h - s - s, u, v + s, s, c, 256, 256);
        drawTexture(matrices, x + s, y + s, w - s - s, h - s - s, u + s, v + s, c, c, 256, 256);
        drawTexture(matrices, x + w - s, y + s, s, h - s - s, u + s + c, v + s, s, c, 256, 256);

        drawTexture(matrices, x, y + h - s, s, s, u, v + s + c, s, s, 256, 256);
        drawTexture(matrices, x + s, y + h - s, w - s - s, s, u + s, v + s + c, c, s, 256, 256);
        drawTexture(matrices, x + w - s, y + h - s, s, s, u + s + c, v + s + c, s, s, 256, 256);
    }

    public static void drawItem(ItemStack stack, int x, int y) {
        DiffuseLighting.enableGuiDepthLighting();

        getItemRenderer().renderGuiItemIcon(stack, x, y);
        getItemRenderer().renderGuiItemOverlay(getTextRenderer(), stack, x, y);

        DiffuseLighting.disableGuiDepthLighting();
    }

    @SuppressWarnings("ConstantConditions")
    public static void renderTooltip(MatrixStack matrices, int x, int y, ItemStack stack, PlayerEntity player) {
        if (!stack.isEmpty())
            getClient().currentScreen.renderTooltip(matrices, stack.getTooltip(player, () -> getClient().options.advancedItemTooltips), x, y);
    }

    public static void drawText(MatrixStack matrices, Text text, int x, int y) {
        drawText(matrices, text, x, y, 0xFFFFFFFF);
    }

    public static void drawText(MatrixStack matrices, Text text, int x, int y, int color) {
        getClient().textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public static void drawText(MatrixStack matrices, String text, int x, int y) {
        drawText(matrices, text, x, y, 0xFFFFFFFF);
    }

    public static void drawText(MatrixStack matrices, String text, int x, int y, int color) {
        getClient().textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public static TextRenderer getTextRenderer() {
        return getClient().textRenderer;
    }

    public static void injectCpasWidget(Screen s, int scaledWidth, int scaledHeight) {
        if (s instanceof HandledScreen<?> screen) {
            CpasTarget target = (CpasTarget) screen;
            if (!(screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen)) {
                if (CpasConfig.get().isShowHelp()) {
                    getClient().getToastManager().add(new HelpToast());
                    CpasConfig.get().setShowHelp(false);
                    CpasConfig.save();
                }

                CpasConfig.Entry entry = CpasConfig.getEntry(screen);
                target.cpas$setEditorScreen(new EditorScreenWidget(scaledWidth, scaledHeight, entry, screen));
                if (entry.isEnabled()) {
                    target.cpas$setArmorPanel(panel(screen, entry, (x, y) -> new ArmorPanelWidget(x, y, getClient().player.getInventory(), screen.getScreenHandler())));
                } else {
                    target.cpas$setArmorPanel(null);
                }
            }
        }
    }

    public static <T extends AbstractPanelWidget> T panel(HandledScreen<?> screen, CpasConfig.Entry entry, BiFunction<Integer, Integer, T> func) {
        AccessorHandledScreen accessor = (AccessorHandledScreen) screen;

        int scaledW = getClient().getWindow().getScaledWidth(),
            scaledH = getClient().getWindow().getScaledHeight();
        int panelX, panelY;

        if (entry.isAuto()) {
            panelX = accessor.getX() - 35;
            panelY = accessor.getY() + accessor.getBackgroundHeight() - ((5 * 18) + 18);

            if (autoPlacer != null) {
                panelX = autoPlacer.getX(screen, panelX, panelY);
            }
        } else {
            panelX = scaledW / 2 + entry.getX();
            panelY = scaledH / 2 + entry.getY();
        }

        return func.apply(panelX, panelY);
    }

    public static void renderCpas(CpasTarget target, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        EditorScreenWidget editorScreen = target.cpas$getEditorScreen();
        ArmorPanelWidget armorPanel = target.cpas$getArmorPanel();

        if (armorPanel != null && target instanceof RecipeBookProvider provider) {
            armorPanel.visible = !provider.getRecipeBookWidget().isOpen();
        }

        if (editorScreen != null) {
            editorScreen.render(matrices, mouseX, mouseY, tickDelta);
            if (armorPanel != null && !editorScreen.visible) {
                armorPanel.render(matrices, mouseX, mouseY, tickDelta);
            }
        }
    }

    public static boolean onKey(Screen screen, int key, int scancode, int modifier) {
        EditorScreenWidget editor = ((CpasTarget) screen).cpas$getEditorScreen();
        if (editor != null && CpasClient.EDIT.matchesKey(key, scancode) && (editor.visible || Screen.hasControlDown())) {
            editor.toggle();
            return false;
        }
        return true;
    }

    @FunctionalInterface
    public interface AutoPlacer {

        int getX(Screen screen, int x, int y);

    }

}
