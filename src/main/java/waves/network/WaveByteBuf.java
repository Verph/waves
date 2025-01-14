package waves.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("null")
public class WaveByteBuf extends FriendlyByteBuf
{
    public WaveByteBuf(ByteBuf source)
    {
        super(source);
    }

    @Override
    public Vec3 readVec3()
    {
        return readVec3(this);
    }

    public static Vec3 readVec3(ByteBuf buffer)
    {
        return new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void writeVec3(Vec3 vec3)
    {
        writeVec3(this, vec3);
    }

    public static void writeVec3(ByteBuf buffer, Vec3 vec3)
    {
        buffer.writeDouble(vec3.x());
        buffer.writeDouble(vec3.y());
        buffer.writeDouble(vec3.z());
    }
}
