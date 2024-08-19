package tk.shanebee.hg.util;

import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Util for getting {@link PotionData}
 */
public enum PotionTypeUtils {

    EMPTY("UNCRAFTABLE"),
    WATER("WATER"),
    MUNDANE("MUNDANE"),
    THICK("THICK"),
    AWKWARD("AWKWARD"),
    NIGHT_VISION("NIGHT_VISION"),
    INVISIBILITY("INVISIBILITY"),
    LEAPING("JUMP"),
    FIRE_RESISTANCE("FIRE_RESISTANCE"),
    SWIFTNESS("SPEED"),
    SLOWNESS("SLOWNESS"),
    WATER_BREATHING("WATER_BREATHING"),
    HEALING("INSTANT_HEAL"),
    HARMING("INSTANT_DAMAGE"),
    POISON("POISON"),
    REGENERATION("REGEN"),
    STRENGTH("STRENGTH"),
    WEAKNESS("WEAKNESS"),
    LUCK("LUCK"),
    TURTLE_MASTER("TURTLE_MASTER"),
    SLOW_FALLING("SLOW_FALLING");

    private final String bukkit;
    private static final Map<String, String> BY_NAME = new HashMap<>();

    PotionTypeUtils(String bukkit) {
        this.bukkit = bukkit;
    }

    static {
        for (PotionTypeUtils p : values()) {
            BY_NAME.put(p.name(), p.bukkit);
        }
        for (PotionType value : PotionType.values()) {
            if (!BY_NAME.containsValue(value.toString())) {
                Util.warning("Missing PotionType for '&7" + value + "&e' please let dev know.");
            }
        }
    }

    /**
     * Get a PotionType based on a Minecraft namespace with Bukkit key fallback
     *
     * @param key Key for PotionType (can be Minecraft namespace or Bukkit key)
     * @return PotionType (null if MC or Bukkit key does not exist)
     */
    @Nullable
    public static PotionType get(String key) {
        String upper = key.toUpperCase();
        if (BY_NAME.containsKey(upper)) {
            return getByKey(upper);
        } else if (BY_NAME.containsValue(upper)) {
            return getByBukkit(upper);
        }
        return null;
    }

    /**
     * Get a PotionType based on a Minecraft namespace
     *
     * @param key Minecraft namespace
     * @return PotionType
     */
    @Nullable
    public static PotionType getByKey(String key) {
        return getByBukkit(valueOf(key).bukkit);
    }

    /**
     * Get a PotionType based on a Bukkit key
     *
     * @param bukkit Key for PotionType
     * @return PotionType
     */
    public static PotionType getByBukkit(String bukkit) {
        return PotionType.valueOf(bukkit.toUpperCase());
    }

    private static void potionTypeWarning(@Nullable String warning) {
        if (warning != null) Util.warning(warning);
        Util.warning("&r  - Check your configs");
        Util.warning("&r  - Proper example:");
        Util.warning("      &bpotion-base:POTION_TYPE:UPGRADED:EXTENDED");
        Util.warning("      &bpotion-base:turtle_master:true:false");
    }

}
