package badasintended.cpas.runtime;

import java.util.List;

import badasintended.cpas.api.CpasPlugin;
import badasintended.cpas.api.CpasSlot;
import badasintended.cpas.api.CpasUtils;
import badasintended.cpas.api.SlotType;
import badasintended.cpas.impl.CpasRegistrarImpl;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class Cpas implements ModInitializer {

    public static final Identifier SLOT_CLICK = CpasUtils.id("slot_click");
    public static final Identifier REGULAR_SLOT = CpasUtils.id("regular_slot");
    public static final Identifier CLEAR_REGISTRY = CpasUtils.id("clear_registry");

    private static final List<CpasPlugin> PLUGINS = FabricLoader.getInstance().getEntrypoints("cpas", CpasPlugin.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> initPlugins());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            initPlugins();
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            server.getPlayerManager().getPlayerList().forEach(player -> {
                if (ServerPlayNetworking.canSend(player, CLEAR_REGISTRY)) {
                    ServerPlayNetworking.send(player, CLEAR_REGISTRY, buf);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SLOT_CLICK, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readVarInt();
            SlotType type = buf.readEnumConstant(SlotType.class);
            Identifier slotId = buf.readIdentifier();

            server.execute(() -> {
                ScreenHandler currentScreenHandler = player.currentScreenHandler;
                if (currentScreenHandler.syncId == syncId) {
                    PlayerScreenHandler playerScreenHandler = player.playerScreenHandler;
                    PlayerInventory inventory = player.getInventory();
                    ItemStack cursor = currentScreenHandler.getCursorStack();
                    if (slotId.equals(REGULAR_SLOT)) {
                        if (cursor.isEmpty() || type.equipmentSlot == null || type.equipmentSlot == MobEntity.getPreferredEquipmentSlot(cursor)) {
                            currentScreenHandler.setCursorStack(player.getInventory().getStack(type.inventoryId).copy());
                            inventory.setStack(type.inventoryId, cursor.copy());
                            playerScreenHandler.syncState();
                        }
                    } else {
                        CpasSlot slot = CpasRegistrarImpl.REGISTRY.get(type).get(slotId);
                        if (slot != null && (cursor.isEmpty() || slot.canEquip(cursor))) {
                            slot.setupContext(player);
                            currentScreenHandler.setCursorStack(slot.setStack(cursor.copy()).copy());
                            playerScreenHandler.syncState();
                        }
                    }
                }
            });
        });
    }

    public static void initPlugins() {
        CpasRegistrarImpl.REGISTRY.forEach((k, v) -> v.clear());
        PLUGINS.forEach(plugin -> plugin.register(CpasRegistrarImpl.INSTANCE));
    }

}
