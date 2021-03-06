package badasintended.cpas.runtime.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

import static badasintended.cpas.runtime.CpasClient.bindTexture;
import static badasintended.cpas.runtime.CpasClient.drawNinePatch;

@Environment(EnvType.CLIENT)
public class MovablePanelWidget extends AbstractPanelWidget {

    public boolean enabled = false;

    private boolean wasClicked = false;
    private int distanceX = 0;
    private int distanceY = 0;

    public MovablePanelWidget(int x, int y) {
        super(x, y, 32, 5 * 18 + 18);
    }

    @Override
    protected void renderBg(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (wasClicked && enabled) {
            x = mouseX - distanceX;
            y = mouseY - distanceY;
        }

        bindTexture();
        drawNinePatch(matrices, x, y, width, height, enabled ? 0 : 48, 0, 4, 8);
        drawTexture(matrices, x + width / 2 - 8, y + height / 2 - 8, 32, 0, 16, 16);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            wasClicked = true;
            distanceX = (int) mouseX - x;
            distanceY = (int) mouseY - y;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return wasClicked = false;
    }

}
