package badasintended.cpas.runtime.duck;

import badasintended.cpas.runtime.widget.ArmorPanelWidget;
import badasintended.cpas.runtime.widget.EditorScreenWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface CpasTarget {

    @Nullable
    EditorScreenWidget cpas$getEditorScreen();

    @Nullable
    ArmorPanelWidget cpas$getArmorPanel();

    void cpas$setEditorScreen(EditorScreenWidget editorScreen);

    void cpas$setArmorPanel(ArmorPanelWidget armorPanel);

}

