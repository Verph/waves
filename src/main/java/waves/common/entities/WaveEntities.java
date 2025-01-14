package waves.common.entities;

import java.util.Locale;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static waves.Waves.MOD_ID;

public class WaveEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final RegistryObject<EntityType<Wave>> WAVES = register("waves", EntityType.Builder.<Wave>of((entityType, level) -> new Wave(entityType, level, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 1.0F, 1.0F, 1.0F, 1), MobCategory.MISC)
                                                                                            .sized(0.2F, 0.2F)
                                                                                            .noSave()
                                                                                            .fireImmune()
                                                                                            .canSpawnFarFromPlayer()
                                                                                            .setTrackingRange(32)
                                                                                            .clientTrackingRange(32)
                                                                                            .setUpdateInterval(1)
                                                                                            .updateInterval(1));

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITIES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        });
    }
}
