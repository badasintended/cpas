package badasintended.cpas.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class CpasUtils {

    public static final String MOD_ID = "cpas";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
