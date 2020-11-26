package badasintended.cpas.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static badasintended.cpas.client.ClientUtils.injectCpasWidget;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(
        method = "openScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V",
            shift = At.Shift.AFTER
        )
    )
    private void openScreen(@Nullable Screen screen, CallbackInfo ci) {
        injectCpasWidget(screen);
    }

    @Inject(
        method = "onResolutionChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;resize(Lnet/minecraft/client/MinecraftClient;II)V",
            shift = At.Shift.AFTER
        )
    )
    private void onResolutionChanged(CallbackInfo ci) {
        injectCpasWidget(currentScreen);
    }

}
