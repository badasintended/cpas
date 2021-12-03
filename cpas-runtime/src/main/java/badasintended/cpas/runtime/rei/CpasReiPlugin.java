package badasintended.cpas.runtime.rei;

import java.util.Collections;

import badasintended.cpas.runtime.CpasClient;
import badasintended.cpas.runtime.duck.CpasTarget;
import badasintended.cpas.runtime.widget.ArmorPanelWidget;
import badasintended.cpas.runtime.widget.EditorScreenWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;

public class CpasReiPlugin implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(CpasTarget.class, screen -> {
            ArmorPanelWidget panel = screen.cpas$getArmorPanel();
            if (panel != null && panel.visible) {
                return Collections.singletonList(new Rectangle(panel.x, panel.y, panel.getWidth(), panel.getHeight()));
            }
            return Collections.emptyList();
        });

        CpasClient.autoPlacer = (screen, x, y) -> {
            for (Rectangle zone : zones.getExclusionZones(screen.getClass())) {
                if (((zone.getMinX() < x && x < zone.getMaxX()) || (x < zone.getMinX() && zone.getMinX() < x + 32)) && ((zone.getMinY() < y && y < zone.getMaxY()) || (y < zone.getMinY() && zone.getMinY() < y + 108))) {
                    x = zone.getMinX() - 35;
                }
            }
            return x;
        };
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        boolean[] previouslyVisible = new boolean[1];
        EditorScreenWidget.onVisibilityChanged = visible -> {
            ConfigObject config = ConfigObject.getInstance();
            if (visible) {
                previouslyVisible[0] = config.isOverlayVisible();
                config.setOverlayVisible(false);
            } else {
                config.setOverlayVisible(previouslyVisible[0]);
            }
        };
    }

}
