package waves.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import waves.network.WaveByteBufCodecs;
import waves.network.WaveCodecs;

public class WaveParticleOption implements ParticleOptions
{
    public static MapCodec<WaveParticleOption> codec(ParticleType<WaveParticleOption> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaveCodecs.VEC3.fieldOf("startPos").forGetter(option -> option.startPos),
            WaveCodecs.VEC3.fieldOf("shorePos").forGetter(option -> option.shorePos),
            WaveCodecs.VEC3.fieldOf("direction").forGetter(option -> option.direction),
            Codec.FLOAT.fieldOf("scale").forGetter(option -> option.scale),
            Codec.FLOAT.fieldOf("size").forGetter(option -> option.size),
            Codec.FLOAT.fieldOf("speed").forGetter(option -> option.speed),
            Codec.INT.fieldOf("waveSize").forGetter(option -> option.waveSize)
        ).apply(instance, (startPos, shorePos, direction, scale, size, speed, waveSize) -> new WaveParticleOption(type, startPos, shorePos, direction, scale, size, speed, waveSize)));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, WaveParticleOption> streamCodec(ParticleType<WaveParticleOption> type)
    {
        return WaveByteBufCodecs.composite(
            WaveByteBufCodecs.VEC3, WaveParticleOption::getStartPos,
            WaveByteBufCodecs.VEC3, WaveParticleOption::getShorePos,
            WaveByteBufCodecs.VEC3, WaveParticleOption::getDirection,
            ByteBufCodecs.FLOAT, WaveParticleOption::getScale,
            ByteBufCodecs.FLOAT, WaveParticleOption::getSize,
            ByteBufCodecs.FLOAT, WaveParticleOption::getSpeed,
            ByteBufCodecs.INT, WaveParticleOption::getWaveSize,
            (startPos, shorePos, direction, scale, size, speed, waveSize) -> new WaveParticleOption(type, startPos, shorePos, direction, scale, size, speed, waveSize)
        );
    }

    private final ParticleType<WaveParticleOption> type;
    private final Vec3 startPos;
    private final Vec3 shorePos;
    private final Vec3 direction;
    private final float scale;
    private final float size;
    private final float speed;
    private final int waveSize;

    public WaveParticleOption(ParticleType<WaveParticleOption> type, Vec3 startPos, Vec3 shorePos, Vec3 direction, float scale, float size, float speed, int waveSize)
    {
        this.type = type;
        this.startPos = startPos;
        this.shorePos = shorePos;
        this.direction = direction;
        this.scale = scale;
        this.size = size;
        this.speed = speed;
        this.waveSize = waveSize;
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }

    public Vec3 getStartPos()
    {
        return this.startPos;
    }

    public Vec3 getShorePos()
    {
        return this.shorePos;
    }

    public Vec3 getDirection()
    {
        return this.direction;
    }

    public float getScale()
    {
        return this.scale;
    }

    public float getSize()
    {
        return this.size;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public int getWaveSize()
    {
        return this.waveSize;
    }
}
