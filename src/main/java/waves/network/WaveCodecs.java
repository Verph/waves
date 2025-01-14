package waves.network;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public final class WaveCodecs extends ExtraCodecs
{
    public static final Codec<Vec3> VEC3 = Codec.DOUBLE
        .listOf()
        .comapFlatMap(
            list -> Util.fixedSize((List<Double>)list, 3).map(pos -> new Vec3(pos.get(0), pos.get(1), pos.get(2))),
            pos -> List.of(pos.x(), pos.y(), pos.z())
        );
}
