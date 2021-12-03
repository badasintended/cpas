package badasintended.cpas.runtime.widget;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import static badasintended.cpas.runtime.CpasClient.getItemRenderer;

@Environment(EnvType.CLIENT)
public abstract class AbstractParentWidget extends FakeButtonWidget implements ParentElement {

    private final List<ClickableWidget> children = new ArrayList<>();

    private Element focused = null;
    private boolean dragging = false;

    public AbstractParentWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    abstract protected void renderBg(MatrixStack matrices, int mouseX, int mouseY, float delta);

    @Override
    final public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!visible)
            return;

        matrices.push();
        matrices.translate(0, 0, 200);
        getItemRenderer().zOffset += 200;

        renderBg(matrices, mouseX, mouseY, delta);
        children.forEach(it -> it.render(matrices, mouseX, mouseY, delta));

        getItemRenderer().zOffset -= 200;
        matrices.pop();

        renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        matrices.push();
        matrices.translate(0, 0, getItemRenderer().zOffset + 250);
        getItemRenderer().zOffset += 250;

        children.forEach(it -> it.renderTooltip(matrices, mouseX, mouseY));

        getItemRenderer().zOffset -= 250;
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ClickableWidget child : children) {
            if (child.mouseClicked(mouseX, mouseY, button))
                return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ClickableWidget child : children) {
            if (child.mouseReleased(mouseX, mouseY, button))
                return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && (super.isMouseOver(mouseX, mouseY) || hoveredElement(mouseX, mouseY).isPresent());
    }

    @Override
    final public List<ClickableWidget> children() {
        return children;
    }

    @Override
    final public boolean isDragging() {
        return dragging;
    }

    @Override
    final public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    @Override
    final public Element getFocused() {
        return focused;
    }

    @Override
    final public void setFocused(Element focused) {
        this.focused = focused;
    }

    @Override
    final public void playDownSound(SoundManager soundManager) {
    }

}
