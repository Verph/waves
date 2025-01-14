package waves.client;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;

public class ClientHelpers
{
    public static final ParticleRenderType PARTICLE_SHEET_TEX_TRANSLUCENT = new ParticleRenderType()
    {
        @Nullable
        public BufferBuilder begin(Tesselator builder, TextureManager manager)
        {
            return ParticleRenderType.CUSTOM.begin(builder, manager);
        }

        public String toString()
        {
            return "PARTICLE_SHEET_TEX_TRANSLUCENT";
        }
    };
}
