package waves.config;

import java.util.function.Function;

import net.neoforged.neoforge.common.ModConfigSpec;

import static waves.Waves.MOD_ID;

public class WavesConfig
{
    public final ModConfigSpec.BooleanValue debug;
    public final ModConfigSpec.IntValue vecAxisIndex;

    public final ModConfigSpec.IntValue waveSearchDistance;
    public final ModConfigSpec.DoubleValue waveSpawnDistance;
    public final ModConfigSpec.DoubleValue waveSpawnAmount;
    public final ModConfigSpec.IntValue waveSpawnFrequency;
    public final ModConfigSpec.DoubleValue waveSpawnDistanceFromShoreMax;
    public final ModConfigSpec.DoubleValue waveSpawnDistanceFromShoreMin;
    public final ModConfigSpec.IntValue waveSpriteCount;
    public final ModConfigSpec.IntValue waveBlockDepositChance;
    public final ModConfigSpec.DoubleValue waveBreakingSoundChance;
    public final ModConfigSpec.IntValue waveBioluminescenceChange;
    public final ModConfigSpec.DoubleValue waveBioluminescenceFrequency;
    public final ModConfigSpec.DoubleValue waveSpawningFOVLimit;

    WavesConfig(ModConfigSpec.Builder innerBuilder)
    {
        Function<String, ModConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.common." + name);

        innerBuilder.push("Debug");
        debug = builder.apply("debug").comment("Enable debug?").define("debug", false);
        vecAxisIndex = builder.apply("vecAxisIndex").comment("Vector axis switch.").defineInRange("vecAxisIndex", 0, 0, 5);
        innerBuilder.pop();

        waveSearchDistance = builder.apply("waveSearchDistance").comment("Search distance for when creating coastal waves.").defineInRange("waveSearchDistance", 14, 0, Integer.MAX_VALUE);
        waveSpawnDistance = builder.apply("waveSpawnDistance").comment("Additional block distance on top of render distance at which waves can spawn.").defineInRange("waveSpawnDistance", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
        waveSpawnAmount = builder.apply("waveSpawnAmount").comment("Additional amount of waves to spawn.").defineInRange("waveSpawnAmount", 0.7D, -Double.MAX_VALUE, Double.MAX_VALUE);
        waveSpawnFrequency = builder.apply("waveSpawnFrequency").comment("Time in ticks between each spawn sequence of waves.").defineInRange("waveSpawnFrequency", 20, 1, Integer.MAX_VALUE);
        waveSpawnDistanceFromShoreMax = builder.apply("waveSpawnDistanceFromShoreMax").comment("Maximum spawn distance from shore.").defineInRange("waveSpawnDistanceFromShoreMax", 28, 0, Double.MAX_VALUE);
        waveSpawnDistanceFromShoreMin = builder.apply("waveSpawnDistanceFromShoreMin").comment("Minimum spawn distance from shore.").defineInRange("waveSpawnDistanceFromShoreMin", 4, 0, Double.MAX_VALUE);
        waveSpriteCount = builder.apply("waveSpriteCount").comment("Amount of sprites for waves. Zero indexed.").defineInRange("waveSpriteCount", 5, 1, Integer.MAX_VALUE);
        waveBlockDepositChance = builder.apply("waveBlockDepositChance").comment("How great should the chance for waves to deposit blocks be? Lower value = higher chance.").defineInRange("waveBlockDepositChance", 100, 1, Integer.MAX_VALUE);
        waveBreakingSoundChance = builder.apply("waveBreakingSoundChance").comment("How often the waves should make a sound. Lower value = rarer.").defineInRange("waveBreakingSoundChance", 0.0015D, 0, Double.MAX_VALUE);
        waveBioluminescenceChange = builder.apply("waveBioluminescenceChange").comment("How fast the bioluminescence should change (octaves).").defineInRange("waveBioluminescenceChange", 16, 1, Integer.MAX_VALUE);
        waveBioluminescenceFrequency = builder.apply("waveBioluminescenceFrequency").comment("The amount of iterations to process the bioluminescence noise map. More = rarer bioluminescent events.").defineInRange("waveBioluminescenceFrequency", 0.5D, 0, Double.MAX_VALUE);
        waveSpawningFOVLimit = builder.apply("waveSpawningFOVLimit").comment("The maximum angle within which waves can spawn irt. the player view direction. E.g. 360 degrees would allow waves to spawn all around the player.").defineInRange("waveSpawningFOVLimit", 140.0D, 0.0D, 360.0D);
    }
}
