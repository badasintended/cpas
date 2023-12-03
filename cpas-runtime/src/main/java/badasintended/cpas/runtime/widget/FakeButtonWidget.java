package badasintended.cpas.runtime.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class FakeButtonWidget extends ClickableWidget {

    private final boolean sound;

    public FakeButtonWidget(int x, int y, int width, int height) {
        this(x, y, width, height, false);
    }

    public FakeButtonWidget(int x, int y, int width, int height, boolean sound) {
        super(x, y, width, height, Text.empty());
        this.sound = sound;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (sound) {
            super.playDownSound(soundManager);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

}
