package badasintended.cpas;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import static badasintended.cpas.Utils.equipTrinket;
import static badasintended.cpas.Utils.hasMod;
import static badasintended.cpas.Utils.slotClick;

@Environment(EnvType.CLIENT)
public class Cpas implements ModInitializer {

    public static final String MOD_ID = "cpas";

    private static String version = "DEV";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final Identifier
        ARMOR_SLOT_CLICK = id("armor_slot_click"),
        TRINKET_SLOT_CLICK = id("trinket_slot_click");

    public static String getVersion() {
        return version;
    }

    @Override
    public void onInitialize() {
        s(ARMOR_SLOT_CLICK, (ctx, buf) -> {
            int slot = buf.readVarInt();

            ctx.getTaskQueue().execute(() -> slotClick(ctx.getPlayer(), slot));
        });

        if (hasMod("trinkets")) {
            s(TRINKET_SLOT_CLICK, (ctx, buf) -> {
                int slot = buf.readVarInt();

                ctx.getTaskQueue().execute(() -> equipTrinket(ctx.getPlayer(), slot));
            });
        }

        FabricLoader
            .getInstance()
            .getModContainer("cpas")
            .ifPresent(mod -> version = mod.getMetadata().getVersion().getFriendlyString());
    }

    private void s(Identifier id, PacketConsumer consumer) {
        ServerSidePacketRegistry.INSTANCE.register(id, consumer);
    }

}
