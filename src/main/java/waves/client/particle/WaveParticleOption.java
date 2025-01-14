package waves.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import waves.network.WaveCodecs;

@SuppressWarnings("null")
public class WaveParticleOption implements ParticleOptions
{
    @Override
    public void writeToNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeDouble(startPos.x);
        buffer.writeDouble(startPos.y);
        buffer.writeDouble(startPos.z);
        buffer.writeDouble(shorePos.x);
        buffer.writeDouble(shorePos.y);
        buffer.writeDouble(shorePos.z);
        buffer.writeDouble(direction.x);
        buffer.writeDouble(direction.y);
        buffer.writeDouble(direction.z);
        buffer.writeFloat(scale);
        buffer.writeFloat(size);
        buffer.writeFloat(speed);
        buffer.writeInt(waveSize);
    }

    @Override
    public String writeToString()
    {
        return String.format("%s %s %s %s %f %f %f %d", 
            ForgeRegistries.PARTICLE_TYPES.getKey(type), 
            startPos, shorePos, direction, scale, size, speed, waveSize);
    }

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<WaveParticleOption> DESERIALIZER = new Deserializer<>()
    {
        @Override
        public WaveParticleOption fromCommand(ParticleType<WaveParticleOption> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            Vec3 startPos = new Vec3(reader.readDouble(), reader.readDouble(), reader.readDouble());
            reader.expect(' ');
            Vec3 shorePos = new Vec3(reader.readDouble(), reader.readDouble(), reader.readDouble());
            reader.expect(' ');
            Vec3 direction = new Vec3(reader.readDouble(), reader.readDouble(), reader.readDouble());
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float speed = reader.readFloat();
            reader.expect(' ');
            int waveSize = reader.readInt();
            return new WaveParticleOption(type, startPos, shorePos, direction, scale, size, speed, waveSize);
        }

        @Override
        public WaveParticleOption fromNetwork(ParticleType<WaveParticleOption> type, FriendlyByteBuf buffer)
        {
            Vec3 startPos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            Vec3 shorePos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            Vec3 direction = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            float scale = buffer.readFloat();
            float size = buffer.readFloat();
            float speed = buffer.readFloat();
            int waveSize = buffer.readInt();
            return new WaveParticleOption(type, startPos, shorePos, direction, scale, size, speed, waveSize);
        }
    };

    public static Codec<WaveParticleOption> getCodec(ParticleType<WaveParticleOption> type)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
            WaveCodecs.VEC3.fieldOf("startPos").forGetter(option -> option.startPos),
            WaveCodecs.VEC3.fieldOf("shorePos").forGetter(option -> option.shorePos),
            WaveCodecs.VEC3.fieldOf("direction").forGetter(option -> option.direction),
            Codec.FLOAT.fieldOf("scale").forGetter(option -> option.scale),
            Codec.FLOAT.fieldOf("size").forGetter(option -> option.size),
            Codec.FLOAT.fieldOf("speed").forGetter(option -> option.speed),
            Codec.INT.fieldOf("waveSize").forGetter(option -> option.waveSize)
        ).apply(instance, (startPos, shorePos, direction, scale, size, speed, waveSize) -> new WaveParticleOption(type, startPos, shorePos, direction, scale, size, speed, waveSize)));
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
        return this.type;
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
