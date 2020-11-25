package badasintended.cpas.client.widget;

import net.minecraft.client.util.math.MatrixStack;

import static badasintended.cpas.client.ClientUtils.bind;
import static badasintended.cpas.client.ClientUtils.drawNinePatch;

public abstract class AbstractPanelWidget extends AbstractParentWidget {

    public AbstractPanelWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected void renderBg(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        bind();
        drawNinePatch(matrices, x, y, width, height, 0, 0, 4, 8);
    }

}
