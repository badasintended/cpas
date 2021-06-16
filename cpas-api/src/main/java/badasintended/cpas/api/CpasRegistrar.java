package badasintended.cpas.api;

import net.minecraft.util.Identifier;

public interface CpasRegistrar {

    void addSlot(SlotType type, Identifier slotId, CpasSlot slot);

}
