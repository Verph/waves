package waves.client.particle;

import java.util.function.Function;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import waves.util.registry.RegistryHolder;

import static waves.Waves.MOD_ID;

public class WaveParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final Id<ParticleType<WaveParticleOption>> WAVES = register("waves", WaveParticleOption::codec, WaveParticleOption::streamCodec);

    private static <O extends ParticleOptions> Id<ParticleType<O>> register(
        final String name,
        final Function<ParticleType<O>, MapCodec<O>> codec,
        final Function<ParticleType<O>, StreamCodec<? super RegistryFriendlyByteBuf, O>> streamCodec)
    {
        return new Id<>(PARTICLE_TYPES.register(name, () -> new ParticleType<O>(false)
        {
            @Override
            public MapCodec<O> codec()
            {
                return codec.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, O> streamCodec()
            {
                return streamCodec.apply(this);
            }
        }));
    }

    private static Id<SimpleParticleType> register(String name)
    {
        return new Id<>(PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false)));
    }

    public record Id<T extends ParticleType<?>>(DeferredHolder<ParticleType<?>, T> holder)
        implements RegistryHolder<ParticleType<?>, T> {}
}
