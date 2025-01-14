package waves.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;

public class ClientHelpers
{
    public static final ParticleRenderType PARTICLE_SHEET_TEX_TRANSLUCENT = new ParticleRenderType()
    {
        public void begin(BufferBuilder builder, TextureManager manager) {}

        public void end(Tesselator tesselator) {}

        public String toString()
        {
            return "PARTICLE_SHEET_TEX_TRANSLUCENT";
        }
    };
}
