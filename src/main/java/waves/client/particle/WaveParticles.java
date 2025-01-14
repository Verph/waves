package waves.client.particle;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static waves.Waves.MOD_ID;

public class WaveParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

	public static final RegistryObject<ParticleType<WaveParticleOption>> WAVES = register("waves", WaveParticleOption.DESERIALIZER, WaveParticleOption::getCodec);

    @SuppressWarnings("deprecation")
    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
    {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<>(false, deserializer) {
            @Override
            public Codec<T> codec()
            {
                return codec.apply(this);
            }
        });
    }

    public static RegistryObject<SimpleParticleType> register(String name)
    {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
    }
}
