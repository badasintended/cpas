package badasintended.cpas.client;

import badasintended.cpas.config.CpasConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CpasClient implements ClientModInitializer {

    public static final KeyBinding EDIT = new KeyBinding("key.cpas.editor", GLFW.GLFW_KEY_J, "key.cpas");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(EDIT);
        CpasConfig.get();
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ClientUtils.injectCpasWidget(screen, scaledWidth, scaledHeight);
        });
    }


}
