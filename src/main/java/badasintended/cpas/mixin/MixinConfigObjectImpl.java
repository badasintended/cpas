package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import me.shedaniel.rei.impl.ConfigObjectImpl;
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
        CpasTarget screen = (CpasTarget) client().currentScreen;
        if (screen != null) {
            EditorScreenWidget editorScreen = screen.cpas$getEditorScreen();
            cir.setReturnValue(cir.getReturnValueZ() && (editorScreen == null || !editorScreen.visible));
        }
    }

}
