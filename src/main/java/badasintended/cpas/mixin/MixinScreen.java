package badasintended.cpas.mixin;

import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.toast.HelpToast;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import badasintended.cpas.config.CpasConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen implements CpasTarget {

    @Unique
    protected EditorScreenWidget editorScreen = null;

    @Unique
    protected ArmorPanelWidget armorPanel = null;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void addArmorSlots(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if ((Object) this instanceof HandledScreen<?>) {
            if (CpasConfig.get().isShowHelp()) {
                client.getToastManager().add(new HelpToast());
                CpasConfig.get().setShowHelp(false);
                CpasConfig.save();
            }

            int scaledW = client.getWindow().getScaledWidth();
            int scaledH = client.getWindow().getScaledHeight();
            HandledScreen<?> realThis = (HandledScreen<?>) (Object) this;
            AccessorHandledScreen accessor = (AccessorHandledScreen) realThis;
            if (!(realThis instanceof InventoryScreen || realThis instanceof CreativeInventoryScreen)) {
                CpasConfig.Entry entry = CpasConfig.getEntry(realThis.getScreenHandler().getType());

                editorScreen = new EditorScreenWidget(scaledW, scaledH, entry, realThis);

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

                    armorPanel = new ArmorPanelWidget(panelX, panelY, 32, 5 * 18 + 18, accessor.getPlayerInventory());
                } else {
                    armorPanel = null;
                }
            }
        }
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

}
