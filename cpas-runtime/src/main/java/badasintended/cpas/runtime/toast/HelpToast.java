package badasintended.cpas.runtime.toast;

import java.util.List;

import badasintended.cpas.runtime.CpasClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

import static badasintended.cpas.runtime.CpasClient.bindTexture;
import static badasintended.cpas.runtime.CpasClient.getTextRenderer;

@Environment(EnvType.CLIENT)
public class HelpToast extends DrawableHelper implements Toast {

    private final TranslatableText text;
    private final List<OrderedText> warpedText;

    public HelpToast() {
        text = new TranslatableText("tutorial.cpas", CpasClient.EDIT.getBoundKeyLocalizedText().getString().toUpperCase());
        warpedText = getTextRenderer().wrapLines(text, getWidth() - 32);
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        bindTexture(TEXTURE);
        drawTexture(matrices, 0, 0, 0, 96, this.getWidth(), this.getHeight());
        drawTexture(matrices, 6, 6, 176, 0, 20, 20);

        if (warpedText.size() == 1) {
            getTextRenderer().draw(matrices, text, 30, 12, 0xff000000);
        } else {
            getTextRenderer().drawTrimmed(text, 30, 7, getWidth() - 32, 0xff000000);
        }

        return startTime >= 5000 ? Visibility.HIDE : Visibility.SHOW;
    }

}
