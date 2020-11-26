package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import me.shedaniel.rei.impl.ConfigObjectImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static badasintended.cpas.client.ClientUtils.client;

/**
 * it's not badasintended if there's no cursed code
 */
@Mixin(ConfigObjectImpl.class)
public abstract class MixinConfigObjectImpl {

    @Inject(method = "isOverlayVisible", at = @At("RETURN"), cancellable = true, remap = false)
    private void isOverlayVisible(CallbackInfoReturnable<Boolean> cir) {
        Screen screen = client().currentScreen;
        if (screen instanceof HandledScreen<?>) {
            EditorScreenWidget editorScreen = ((CpasTarget) screen).cpas$getEditorScreen();
            cir.setReturnValue(cir.getReturnValueZ() && (editorScreen == null || !editorScreen.visible));
        }
    }

}
