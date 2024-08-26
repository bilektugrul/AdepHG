package tk.shanebee.hg.util;

import me.despical.commons.reflection.XReflection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import tk.shanebee.hg.HG;

public class NoAI {

    private static Class<?> NBT_TAG_COMPOUND_CLASS;

    static {
        try {
            NBT_TAG_COMPOUND_CLASS = XReflection.getNMSClass("NBTTagCompound");
        } catch (Exception ignored) {
        }
    }

    public static void disableEntityAI(LivingEntity entity) {
        try {
            Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
            Object tag = nmsEntity.getClass().getMethod("getNBTTag").invoke(nmsEntity);

            if (tag == null) {
                tag = NBT_TAG_COMPOUND_CLASS.newInstance();
            }

            nmsEntity.getClass().getMethod("c", NBT_TAG_COMPOUND_CLASS).invoke(nmsEntity, tag);
            NBT_TAG_COMPOUND_CLASS.getMethod("setInt", String.class, int.class).invoke(tag, "NoAI", 1);
            NBT_TAG_COMPOUND_CLASS.getMethod("setBoolean", String.class, boolean.class).invoke(tag, "Silent", true);
            nmsEntity.getClass().getMethod("f", NBT_TAG_COMPOUND_CLASS).invoke(nmsEntity, tag);
        } catch (Throwable ignored) {
        }

        trySilently(() -> entity.setMetadata("HG-GOD", new FixedMetadataValue(HG.getPlugin(), true)));
    }

    private static void trySilently(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }
}