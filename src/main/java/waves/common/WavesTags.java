package waves.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import waves.util.WaveHelpers;

public class WavesTags
{
    public static class Blocks
    {
        public static final TagKey<Block> TIDE_POOL_BLOCKS = create("wave_blocks");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registries.BLOCK, WaveHelpers.identifier(id));
        }
    }

    public static class Fluids
    {
        public static final TagKey<Fluid> HAS_WAVES = create("has_waves");

        private static TagKey<Fluid> create(String id)
        {
            return TagKey.create(Registries.FLUID, WaveHelpers.identifier(id));
        }
    }

    public static class Biomes
    {
        public static final TagKey<Biome> HAS_WAVES = create("has_waves");

        private static TagKey<Biome> create(String id)
        {
            return TagKey.create(Registries.BIOME, WaveHelpers.identifier(id));
        }
    }
}
