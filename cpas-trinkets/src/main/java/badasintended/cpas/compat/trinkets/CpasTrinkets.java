package badasintended.cpas.compat.trinkets;

import badasintended.cpas.api.Config;
import badasintended.cpas.api.CpasPlugin;
import badasintended.cpas.api.CpasRegistrar;
import badasintended.cpas.api.SlotType;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.Collections;
import java.util.Map;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CpasTrinkets implements CpasPlugin {

    private static final Logger LOGGER = LogManager.getLogger("cpas-trinkets");
    private static final Config<CpasTrinketsConfig> CONFIG = Config
        .builder(CpasTrinketsConfig.class)
        .name("trinkets")
        .build();

    @Override
    public void register(CpasRegistrar registry) {
        LOGGER.info("[cpas] Loading Trinkets compatibility");
        Map<String, SlotGroup> groups = TrinketsApi.getPlayerSlots();
        CpasTrinketsConfig config = CONFIG.get(true);
        for (SlotType type : SlotType.VALUES) {
            config.slotGroups.getOrDefault(type.id, Collections.emptySet()).forEach(groupId -> {
                SlotGroup group = groups.get(groupId);
                if (group != null) {
                    group.getSlots().forEach((slotId, slot) -> {
                        for (int i = 0; i < slot.getAmount(); i++) {
                            registry.addSlot(type, new Identifier("trinkets", groupId + "/" + slotId + "/" + i), new CpasTrinketSlot(groupId, slotId, slot, i));
                        }
                    });
                }
            });
        }
    }

}
