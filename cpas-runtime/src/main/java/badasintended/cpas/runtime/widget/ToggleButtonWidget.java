package badasintended.cpas.runtime.widget;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import static badasintended.cpas.runtime.CpasClient.bindTexture;
import static badasintended.cpas.runtime.CpasClient.drawNinePatch;
import static badasintended.cpas.runtime.CpasClient.drawText;
import static badasintended.cpas.runtime.CpasClient.getTextRenderer;

@Environment(EnvType.CLIENT)
public class ToggleButtonWidget extends ButtonWidget {

    private final Consumer<Boolean> consumer;

    public boolean canBeToggled = true;
    private boolean enabled = false;

    public ToggleButtonWidget(int x, int y, Consumer<Boolean> consumer, String tlKey, Object... args) {
        super(x, y, 22, 13, new TranslatableText(tlKey, args), w -> {
        });

        this.consumer = consumer;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (canBeToggled) {
            this.enabled = enabled;
            consumer.accept(enabled);
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        bindTexture();
        drawNinePatch(matrices, x, y, 22, 13, enabled ? 64 : 16, 0, 1, 14);
        drawTexture(matrices, x + (enabled ? 10 : 1), y + 1, canBeToggled ? 0 : 11, 16, 11, 11);

        drawText(matrices, getMessage(), x + 30, y + (height - getTextRenderer().fontHeight) / 2);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        toggle();
    }

}
