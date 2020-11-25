package badasintended.cpas.mixin;

import badasintended.cpas.client.CpasClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends MixinScreen {

    @Inject(method = "render", at = @At("HEAD"))
    private void checkRecipeButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HandledScreen<?> realThis = (HandledScreen<?>) (Object) this;
        if (realThis instanceof RecipeBookProvider && armorPanel != null) {
            armorPanel.visible = !((RecipeBookProvider) realThis).getRecipeBookWidget().isOpen();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCpasTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (editorScreen != null) {
            editorScreen.render(matrices, mouseX, mouseY, delta);
            if (armorPanel != null && !editorScreen.visible) {
                armorPanel.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (editorScreen != null) {
            if (editorScreen.visible) {
                editorScreen.mouseClicked(mouseX, mouseY, button);
                cir.setReturnValue(true);
            } else if (armorPanel != null && armorPanel.isMouseOver(mouseX, mouseY)) {
                armorPanel.mouseClicked(mouseX, mouseY, button);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (editorScreen != null) {
            if (editorScreen.visible) {
                editorScreen.mouseReleased(mouseX, mouseY, button);
                cir.setReturnValue(true);
            } else if (armorPanel != null && armorPanel.isMouseOver(mouseX, mouseY)) {
                armorPanel.mouseReleased(mouseX, mouseY, button);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("TAIL"), cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (editorScreen != null && CpasClient.EDIT.matchesKey(keyCode, scanCode)) {
            editorScreen.toggle();
            cir.setReturnValue(true);
        }
    }

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

}
