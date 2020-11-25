package badasintended.cpas.client.rei;

import java.util.Collections;

import badasintended.cpas.Cpas;
import badasintended.cpas.client.api.CpasTarget;
import badasintended.cpas.client.widget.ArmorPanelWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CpasReiPlugin implements REIPluginV0 {

    private static final Identifier PLUGIN_ID = Cpas.id("rei_plugin");

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN_ID;
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler handler = BaseBoundsHandler.getInstance();
        handler.registerExclusionZones(HandledScreen.class, () -> {
            CpasTarget target = (CpasTarget) REIHelper.getInstance().getPreviousContainerScreen();
            if (target != null) {
                ArmorPanelWidget panel = target.cpas$getArmorPanel();
                if (panel != null && panel.visible) {
                    return Collections.singletonList(new Rectangle(panel.x, panel.y, panel.getWidth(), panel.getHeight()));
                }
            }
            return Collections.emptyList();
        });
    }

}
