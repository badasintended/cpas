package badasintended.cpas.client.api;

import badasintended.cpas.client.widget.ArmorPanelWidget;
import badasintended.cpas.client.widget.EditorScreenWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface CpasTarget {

    @Nullable
    EditorScreenWidget cpas$getEditorScreen();

    @Nullable
    ArmorPanelWidget cpas$getArmorPanel();

}
