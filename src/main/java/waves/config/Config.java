package waves.config;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import static waves.Waves.*;

public class Config
{
    public static final Config COMMON = register(ModConfig.Type.COMMON, Config::new);

    public static void init() {}

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory)
    {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getLeft();
    }

    public final ForgeConfigSpec.BooleanValue debug;
    public final ForgeConfigSpec.IntValue vecAxisIndex;

    public final ForgeConfigSpec.IntValue waveSearchDistance;
    public final ForgeConfigSpec.DoubleValue waveSpawnDistance;
    public final ForgeConfigSpec.DoubleValue waveSpawnAmount;
    public final ForgeConfigSpec.IntValue waveSpawnFrequency;
    public final ForgeConfigSpec.DoubleValue waveSpawnDistanceFromShoreMax;
    public final ForgeConfigSpec.DoubleValue waveSpawnDistanceFromShoreMin;
    public final ForgeConfigSpec.IntValue waveSpriteCount;
    public final ForgeConfigSpec.IntValue waveBlockDepositChance;
    public final ForgeConfigSpec.DoubleValue waveBreakingSoundChance;
    public final ForgeConfigSpec.IntValue waveBioluminescenceChange;
    public final ForgeConfigSpec.DoubleValue waveBioluminescenceFrequency;
    public final ForgeConfigSpec.DoubleValue waveSpawningFOVLimit;

    Config(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.common." + name);

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