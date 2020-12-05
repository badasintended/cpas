package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import badasintended.cpas.config.CpasConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int i, int j, MatrixStack matrixStack) {
        Screen screen = client.currentScreen;
        if (screen instanceof HandledScreen<?>) {
            AccessorHandledScreen a = (AccessorHandledScreen) screen;
            CpasTarget target = (CpasTarget) screen;
            EditorScreenWidget editorScreen = target.cpas$getEditorScreen();
            ArmorPanelWidget armorPanel = target.cpas$getArmorPanel();

            if (screen instanceof RecipeBookProvider && armorPanel != null) {
                armorPanel.visible = !((RecipeBookProvider) screen).getRecipeBookWidget().isOpen();
            }

            float delta = client.getLastFrameDuration();
            if (editorScreen != null) {
                editorScreen.render(matrixStack, i, j, delta);
                if (armorPanel != null && !editorScreen.visible) {
                    armorPanel.render(matrixStack, i, j, delta);
                }
            }
            if (CpasConfig.get().debug.isBackgroundOverlay())
                fill(matrixStack, a.getX(), a.getY(), a.getX() + a.getBackgroundWidth(), a.getY() + a.getBackgroundHeight(), 0x44FF00FF);
        }
    }

}
