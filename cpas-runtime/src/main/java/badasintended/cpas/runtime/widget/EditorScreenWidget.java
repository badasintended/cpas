package badasintended.cpas.runtime.widget;

import badasintended.cpas.runtime.CpasClient;
import badasintended.cpas.runtime.config.CpasConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;

import static badasintended.cpas.runtime.CpasClient.drawText;
import static badasintended.cpas.runtime.CpasClient.getTextRenderer;
import static badasintended.cpas.runtime.CpasClient.injectCpasWidget;
import static badasintended.cpas.runtime.CpasClient.panel;

@Environment(EnvType.CLIENT)
public class EditorScreenWidget extends AbstractParentWidget {

    private final int scaledW, scaledH;
    private final CpasConfig.Entry entry;
    private final HandledScreen<?> screen;
    private final String screenName;

    private boolean enabled;
    private boolean auto;
    private MovablePanelWidget panel;

    public EditorScreenWidget(int scaledW, int scaledH, CpasConfig.Entry entry, HandledScreen<?> screen) {
        super(0, 0, scaledW, scaledH);
        visible = false;

        this.scaledW = scaledW;
        this.scaledH = scaledH;
        this.entry = entry;
        this.screen = screen;
        this.screenName = screen.getClass().getName();
    }

    public void toggle() {
        visible = !visible;

        children().clear();

        if (visible) {
            int minX = 20,
                minY = 20;

            enabled = entry.isEnabled();
            auto = entry.isAuto();

            panel = panel(screen, entry, MovablePanelWidget::new);

            ToggleButtonWidget autoButton = new ToggleButtonWidget(minX, minY + 50, b -> {
                auto = b;
                panel.enabled = !b;
            }, "editor.cpas.auto");

            ToggleButtonWidget enableButton = new ToggleButtonWidget(minX, minY + 30, b -> {
                autoButton.canBeToggled = b;
                if (b) {
                    if (!enabled)
                        auto = true;
                    panel.enabled = !auto;
                } else {
                    panel.enabled = false;
                }
                enabled = b;
                autoButton.setEnabled(auto);
            }, "editor.cpas.enabled");

            enableButton.setEnabled(enabled);
            autoButton.setEnabled(auto);
            panel.enabled = !auto;

            children().add(panel);
            children().add(enableButton);
            children().add(autoButton);
        } else {
            entry.setEnabled(enabled);
            entry.setAuto(auto);
            entry.setX(panel.x - scaledW / 2);
            entry.setY(panel.y - scaledH / 2);
            CpasConfig.save();
            injectCpasWidget(screen, scaledW, scaledH);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + height, 0xc0101010);

        int minX = 20,
            minY = 20,
            maxX = scaledW - 20,
            maxY = scaledH - 20 - getTextRenderer().fontHeight;

        drawText(matrices, I18n.translate("editor.cpas.title"), minX, minY);
        drawText(matrices, I18n.translate("editor.cpas.id", screenName), minX, minY + 15);

        int color = !enabled || auto ? 0xFFAAAAAA : 0xFFFFFFFF;

        drawText(matrices, "X: " + (panel.x - scaledW / 2), minX, maxY - 15, color);
        drawText(matrices, "Y: " + (panel.y - scaledH / 2), minX, maxY, color);

        String text = I18n.translate("editor.cpas.exit", CpasClient.EDIT.getBoundKeyLocalizedText().getString().toUpperCase());
        drawText(matrices, text, maxX - getTextRenderer().getWidth(text), maxY);
    }

}
