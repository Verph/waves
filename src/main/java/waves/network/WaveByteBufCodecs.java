package waves.network;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;

import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("null")
public interface WaveByteBufCodecs
{
    StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<ByteBuf, Vec3>()
    {
        public Vec3 decode(ByteBuf buffer)
        {
            return WaveByteBuf.readVec3(buffer);
        }

        public void encode(ByteBuf buffer, Vec3 vec3)
        {
            WaveByteBuf.writeVec3(buffer, vec3);
        }
    };

    static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
        StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
        StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
        StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
        StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
        StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
        StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
        StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
        Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
        StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
        StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
        StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
        StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
        StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
        StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
        StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
        StreamCodec<? super B, T8> codec8, Function<C, T8> getter8,
        Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
                codec8.encode(buffer, getter8.apply(value));
            }
        };
    }
}
