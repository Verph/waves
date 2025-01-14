package waves.config;

import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    public static final WavesConfig COMMON;
    public static final ModConfigSpec SPEC;

    static
    {
        final Pair<WavesConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(WavesConfig::new);
        COMMON = specPair.getLeft();
        SPEC = specPair.getRight();
    }
}