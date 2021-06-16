package badasintended.cpas.compat.trinkets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CpasTrinketsConfig {

    public final Map<String, Set<String>> slotGroups = new HashMap<>();

    public CpasTrinketsConfig() {
        Set<String> head = slotGroups.computeIfAbsent("head", s -> new HashSet<>());
        Set<String> chest = slotGroups.computeIfAbsent("chest", s -> new HashSet<>());
        Set<String> legs = slotGroups.computeIfAbsent("legs", s -> new HashSet<>());
        Set<String> feet = slotGroups.computeIfAbsent("feet", s -> new HashSet<>());
        Set<String> offhand = slotGroups.computeIfAbsent("offhand", s -> new HashSet<>());

        head.add("head");
        chest.add("chest");
        legs.add("legs");
        feet.add("feet");
        offhand.add("hand");
        offhand.add("offhand");
    }

}
