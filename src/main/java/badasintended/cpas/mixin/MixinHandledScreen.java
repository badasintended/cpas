package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen implements CpasTarget {

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Unique
    protected EditorScreenWidget editorScreen = null;

    @Unique
    protected ArmorPanelWidget armorPanel = null;

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    private void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (editorScreen != null && editorScreen.visible)
            ci.cancel();
    }

    @Inject(method = "isPauseScreen", at = @At("RETURN"), cancellable = true)
    private void isPauseScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || (editorScreen != null && editorScreen.visible));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;doDrawHoveringEffect()Z", ordinal = 1))
    private boolean checkEditor(Slot slot) {
        return (editorScreen == null || !editorScreen.visible) && slot.doDrawHoveringEffect();
    }

    @Nullable
    @Override
    public ArmorPanelWidget cpas$getArmorPanel() {
        return armorPanel;
    }

    @Nullable
    @Override
    public EditorScreenWidget cpas$getEditorScreen() {
        return editorScreen;
    }

    @Override
    public void cpas$setArmorPanel(ArmorPanelWidget armorPanel) {
        this.armorPanel = armorPanel;
    }

    @Override
    public void cpas$setEditorScreen(EditorScreenWidget editorScreen) {
        this.editorScreen = editorScreen;
    }

}
