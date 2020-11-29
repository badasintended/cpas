package badasintended.cpas.mixin;

import badasintended.cpas.client.CpasClient;
import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "onKey",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci, ParentElement parentElement, boolean[] bls) {
        Screen screen = client.currentScreen;
        if (screen instanceof HandledScreen<?> && i != 0 && bls[0]) {
            EditorScreenWidget editor = ((CpasTarget) screen).cpas$getEditorScreen();
            if (editor != null && CpasClient.EDIT.matchesKey(key, scancode) && (editor.visible || Screen.hasControlDown())) {
                editor.toggle();
                ci.cancel();
            }
        }
    }

}
