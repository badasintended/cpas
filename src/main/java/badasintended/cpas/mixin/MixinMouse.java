package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double x;

    @Shadow
    private double y;

    @Inject(
        method = "onMouseButton",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/Window;getScaledWidth()I"
        ),
        cancellable = true
    )
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof HandledScreen<?>) {
            EditorScreenWidget editor = ((CpasTarget) screen).cpas$getEditorScreen();
            ArmorPanelWidget panel = ((CpasTarget) screen).cpas$getArmorPanel();

            if (editor != null) {
                double d = x * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                double e = y * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
                boolean bl = action == 1;

                if (editor.visible) {
                    if (bl) {
                        editor.mouseClicked(d, e, button);
                    } else {
                        editor.mouseReleased(d, e, button);
                    }
                    ci.cancel();
                } else if (panel != null && panel.isMouseOver(d, e)) {
                    if (bl) {
                        panel.mouseClicked(d, e, button);
                    } else {
                        panel.mouseReleased(d, e, button);
                    }
                    ci.cancel();
                }
            }
        }
    }

}
