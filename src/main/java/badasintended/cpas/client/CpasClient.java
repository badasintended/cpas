package badasintended.cpas.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class CpasClient implements ClientModInitializer {

    public static final KeyBinding EDIT = new KeyBinding("key.cpas.editor", GLFW.GLFW_KEY_J, "key.cpas");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(EDIT);
    }

}
