package waves.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import net.irisshaders.iris.Iris;

import waves.common.WavesTags;
import waves.config.Config;

import static waves.Waves.MOD_ID;

public class WaveHelpers
{
    public static final Direction[] DIRECTIONS_HORIZONTAL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public static ResourceLocation identifier(String name)
    {
        return resourceLocation(MOD_ID, name);
    }

    public static ResourceLocation resourceLocation(String name)
    {
        return new ResourceLocation(name);
    }

    public static ResourceLocation resourceLocation(String domain, String path)
    {
        return new ResourceLocation(domain, path);
    }

    public static ArtifactVersion getVersion()
    {
        return ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion();
    }

    public static int compareVersions(String oldVersion)
    {
        /*
        * -1 = "Current version is older than the old version."
        * 1 = "Current version is newer than the old version."
        * 0 = "Both versions are the same."
        */
        return ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion().compareTo(new DefaultArtifactVersion(oldVersion));
    }

    public static boolean areShadersEnabled()
    {
        if (ModList.get().isLoaded("oculus") || ModList.get().isLoaded("iris"))
        {
            return Iris.getIrisConfig().areShadersEnabled();
        }
        return false;
    }

    public static Noise2D noise(long seed, int octaves, float scale, int minHeight, int maxHeight)
    {
        final OpenSimplex2D warp = new OpenSimplex2D(seed).octaves(octaves).spread(0.03f).scaled(-scale, scale);
        return new OpenSimplex2D(seed + 1)
            .octaves(octaves)
            .spread(0.06f)
            .warped(warp)
            .map(x -> x > 0.4 ? x - 0.8f : -x)
            .scaled(-0.4f, 0.8f, minHeight, maxHeight);
    }

    public static double noise(double x, long seed, int octaves, int minHeight, int maxHeight, double factor)
    {
        return noise(seed, octaves, 100f, minHeight, maxHeight).noise(x, 0) * factor;
    }

    public static float triangle(RandomSource random)
    {
        return random.nextFloat() - random.nextFloat() * 0.5F;
    }

    public static float lerp(float delta, float min, float max)
    {
        return min + (max - min) * delta;
    }

    public static double lerp(double delta, double min, double max)
    {
        return min + (max - min) * delta;
    }

    public static double easeOutQuart(double t, double min, double max)
    {
        double change = max - min;
        return -change * ((t = t - 1) * t * t * t - 1) + min;
    }

    public static Vec3 applyEaseOutQuart(Vec3 direction, double distance, double initialDistance, double min, double max)
    {
        double t = (distance / initialDistance);
        double speed = easeOutQuart(t, min, max);
        return direction.scale(speed);
    }

    public static double easeInOutExpo(double t, double min, double max)
    {
        double range = max - min;
        if (t <= 0) return min;
        if (t >= 1) return max;
        if (t < 0.5D)
        {
            return min + (range / 2) * Math.pow(2, 20 * t - 10);
        }
        else
        {
            return min + (range / 2) * (2 - Math.pow(2, -20 * t + 10));
        }
    }

    public static double easeOutInExpo(double t, double min, double max)
    {
        double range = max - min;
        if (t >= 1) return min;
        if (t <= 0) return max;
        if (t < 0.5D)
        {
            return min + range * (1 - Math.pow(2, -20 * t));
        }
        else
        {
            return min + range * (Math.pow(2, 20 * (t - 1)) / 2 + 0.5);
        }
    }

    public static double easeInOutExpoNorm(double t, double min, double max, double lowerBound, double upperBound)
    {
        double range = max - min;
        double normalizedT = (t - lowerBound) / (upperBound - lowerBound);
        if (normalizedT <= 0) return min;
        if (normalizedT >= 1) return max;
        if (normalizedT < 0.5)
        {
            return min + (range / 2) * Math.pow(2, 20 * normalizedT - 10);
        }
        else
        {
            return min + (range / 2) * (2 - Math.pow(2, -20 * normalizedT + 10));
        }
    }

    public static double easeInExpo(double t, double min, double max)
    {
        double range = max - min;
        return t <= 0 ? min : min + range * Math.pow(2, 10 * (t - 1));
    }

    public static double easeOutExpo(double t, double min, double max)
    {
        double range = max - min;
        return t >= 1 ? max : min + range * (1 - Math.pow(2, -10 * t));
    }

    public static Vec3 inverse(Vec3 vector)
    {
        return new Vec3(-vector.x(), -vector.y(), -vector.z());
    }

    public static BlockPos toBlockPos(Vec3 pos)
    {
        return toBlockPos(pos, 0);
    }

    public static BlockPos toBlockPos(Vec3 pos, int roundingMethod)
    {
        if (roundingMethod >= 2)
        {
            return new BlockPos((int) Math.ceil(pos.x()), (int) Math.ceil(pos.y()), (int) Math.ceil(pos.z()));
        }
        else if (roundingMethod == 1)
        {
            return new BlockPos((int) Math.round(pos.x()), (int) Math.round(pos.y()), (int) Math.round(pos.z()));
        }
        return new BlockPos((int) Math.floor(pos.x()), (int) Math.floor(pos.y()), (int) Math.floor(pos.z()));
    }

    public static Vec3 toVec3(BlockPos pos)
    {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 findNearestShorePos(Level level, Vec3 currentPos, int searchRadius, double minDistance)
    {
        Vec3 nearestShorePos = null;
        double closestDistance = Double.MAX_VALUE;

        for (int i = -searchRadius; i <= searchRadius; i++)
        {
            for (int j = -searchRadius; j <= searchRadius; j++)
            {
                double x = currentPos.x() + i;
                double z = currentPos.z() + j;
                Vec3 checkPos = new Vec3(x, currentPos.y(), z);

                if (level.isLoaded(toBlockPos(checkPos)) && isShore(level, checkPos))
                {
                    double distance = currentPos.distanceTo(checkPos);

                    if (distance < closestDistance && distance > minDistance)
                    {
                        closestDistance = distance;
                        nearestShorePos = checkPos;
                    }
                }
            }
        }
        return nearestShorePos;
    }

    public static double calculateDistanceToShore(Level level, Vec3 currentPos, int searchRadius, double minDistance)
    {
        Vec3 nearestShorePos = findNearestShorePos(level, currentPos, searchRadius, minDistance);
        return calculateDistanceToShore(currentPos, nearestShorePos);
    }

    public static double calculateDistanceToShore(Vec3 currentPos, Vec3 nearestShorePos)
    {
        return currentPos.distanceTo(nearestShorePos);
    }

    public static double calculateAngleToShore(Level level, Vec3 currentPos, int searchRadius, double minDistance)
    {
        Vec3 nearestShorePos = findNearestShorePos(level, currentPos, searchRadius, minDistance);
        if (nearestShorePos != null)
        {
            double deltaX = nearestShorePos.x() - currentPos.x();
            double deltaZ = nearestShorePos.z() - currentPos.z();
            return Math.toDegrees(Math.atan2(deltaZ, deltaX));
        }
        return -1;
    }

    public static double calculateAngleToTarget(Vec3 currentPos, Vec3 shorePos)
    {
        Vec3 direction = shorePos.subtract(currentPos).normalize();
        return Math.atan2(direction.z(), direction.x());
    }

    public static double calculateAverageAngle(Level level, BlockPos targetCenter, int radius)
    {
        List<Double> angles = new ArrayList<>();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                BlockPos checkPos = targetCenter.offset(x, 0, z);
                if (isShore(level, checkPos))
                {
                    Vec3 vecCenter = toVec3(targetCenter);
                    Vec3 vecCheck = toVec3(checkPos);
                    double angle = calculateAngleToTarget(vecCenter, vecCheck);
                    angles.add(angle);
                }
            }
        }
        return angles.stream().mapToDouble(Double::doubleValue).average().orElse(0.0D);
    }

    public static Vec3 calculateCenterPoint(Level level, BlockPos targetCenter, int radius)
    {
        List<BlockPos> shorePositions = new ArrayList<>();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                BlockPos checkPos = targetCenter.offset(x, 0, z);
                if (isShore(level, checkPos))
                {
                    shorePositions.add(checkPos);
                }
            }
        }

        if (shorePositions.isEmpty())
        
        {
            return Vec3.ZERO;
        }

        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;

        for (BlockPos pos : shorePositions)
        {
            sumX += pos.getX();
            sumY += pos.getY();
            sumZ += pos.getZ();
        }

        double centerX = sumX / shorePositions.size();
        double centerY = sumY / shorePositions.size();
        double centerZ = sumZ / shorePositions.size();

        return new Vec3(centerX, centerY, centerZ);
    }

    public static double calculateWaveOrthogonalToTarget(Level level, BlockPos targetCenter, int radius)
    {
        return calculateAverageAngle(level, targetCenter, radius) + Math.PI / 2;
    }

    public static boolean isShore(Level level, Vec3 pos)
    {
        return isShore(level, toBlockPos(pos));
    }

    public static boolean isShore(Level level, BlockPos pos)
    {
        return level.getFluidState(pos).isEmpty() && isSurroundedByWater(level, pos, 1, 1);
    }

    public static boolean isSurroundedByWater(Level level, Vec3 pos, int radius)
    {
        return isSurroundedByWater(level, pos, radius, 4 * radius);
    }

    public static boolean isSurroundedByWater(Level level, Vec3 pos, int radius, int requiredAmount)
    {
        return isSurroundedByWater(level, toBlockPos(pos), radius, requiredAmount);
    }

    public static boolean isSurroundedByWater(Level level, BlockPos pos, int radius, int requiredAmount)
    {
        int waterFaces = 0;
        for (Direction direction : DIRECTIONS_HORIZONTAL)
        {
            for (int offset = 0; offset < radius; offset++)
            {
                if (level.getFluidState(pos.relative(direction, offset + 1)).is(WavesTags.Fluids.HAS_WAVES))
                {
                    waterFaces += 1;
                }
                if (waterFaces >= requiredAmount)
                {
                    return true;  
                }
            }
        }
        return waterFaces >= requiredAmount;
    }

    public static boolean isSurroundedByWaterCircle(Level level, Vec3 pos, int radius)
    {
        return isSurroundedByWaterCircle(level, pos, radius, Mth.floor(Math.PI * Math.pow(radius, 2)) - 1);
    }

    public static boolean isSurroundedByWaterCircle(Level level, Vec3 pos, int radius, int requiredAmount)
    {
        return isSurroundedByWaterCircle(level, toBlockPos(pos), radius, requiredAmount);
    }

    public static boolean isSurroundedByWaterCircle(Level level, BlockPos pos, int radius, int requiredAmount)
    {
        int waterFaces = 0;
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                if (x * x + z * z <= radius * radius)
                {
                    if (level.getFluidState(pos.offset(x, 0, z)).is(WavesTags.Fluids.HAS_WAVES))
                    {
                        waterFaces += 1;
                    }
                    if (waterFaces >= requiredAmount)
                    {
                        return true;
                    }
                }
            }
        }
        return waterFaces >= requiredAmount;
    }

    public static int getSurroundingWaterBlocks(Level level, Vec3 pos)
    {
        BlockPos blockPos = toBlockPos(pos);
        int waterFaces = 0;
        for (Direction direction : DIRECTIONS_HORIZONTAL)
        {
            if (level.getFluidState(blockPos.relative(direction, 1)).is(WavesTags.Fluids.HAS_WAVES))
            {
                waterFaces += 1;
            }
        }
        return waterFaces;
    }

    public static String toSize(int index)
    {
        switch (index)
        {
            case 1:
                return "medium";
            case 2:
                return "large";
            default:
                return "small";
        }
    }

    public static void playSound(Level level, RandomSource random, BlockPos pos, SoundEvent sound, SoundSource source, boolean distanceDelay, float amplifier)
    {
        level.playLocalSound(pos, sound, source, (random.nextFloat() * 0.7F + 0.6F) * amplifier, random.nextFloat() * 0.8F + 0.7F, distanceDelay);
    }

    public static double soundDistanceMod(Player player, Vec3 targetPos, double min, double max)
    {
        double distance = player.position().distanceTo(targetPos);

        if (distance <= max)
        {
            return 1.0D;
        }

        if (distance >= min)
        {
            return 0.0D;
        }

        double lerpFactor = (min - distance) / (min - max);
        lerpFactor = Math.max(0, Math.min(lerpFactor, 1));

        return lerpFactor;
    }

    public static BlockPos getRandomBlockPosAlongWave(Level level, RandomSource random, Vec3 pos, Vec3 direction, double width)
    {
        Quaternionf quaternion = new Quaternionf().rotateY((float) Math.toRadians(90));
        Vec3 rotatedVec = new Vec3(direction.toVector3f().rotate(quaternion));
        double pointOnWave = ((random.nextDouble() - 0.5D) * 2.0D) * width;
        double thickness = ((random.nextDouble() - 0.5D) * 2.0D) * 2.0D;

        Vec3 position = new Vec3(pos.x(), level.getSeaLevel(), pos.z())
            .add(rotatedVec.scale(pointOnWave).x(), 0.0D, rotatedVec.scale(pointOnWave).z())
            .add(direction.scale(thickness).x(), 0.0D, direction.scale(thickness).z());

        return toBlockPos(position, 1);
    }

    public static Optional<Block> randomBlock(TagKey<Block> tag, RandomSource random)
    {
        return getRandomElement(BuiltInRegistries.BLOCK, tag, random);
    }

    public static <T> Optional<T> getRandomElement(Registry<T> registry, TagKey<T> tag, RandomSource random)
    {
        return registry.getTag(tag).flatMap(set -> set.getRandomElement(random)).map(Holder::value);
    }

    public static int updateSprite(double initialDistance, double currentDistance)
    {
        if (initialDistance == 0)
        {
            return 0;
        }
        double lerpFactor = 1.0D - (currentDistance / initialDistance);
        return (int) Math.round(Mth.clamp(Math.max(0, Math.min(lerpFactor, 1)) * (Config.COMMON.waveSpriteCount.get() - 1), 0, Config.COMMON.waveSpriteCount.get() - 1));
    }

    public static int updateSprite(double initialDistance, double currentDistance, double power)
    {
        if (initialDistance == 0)
        {
            return 0;
        }
        double progress = 1.0D - (currentDistance / initialDistance);
        double easeInQuartFactor = Math.pow(progress, power);
        return (int) Math.round(Mth.clamp(easeInQuartFactor * (Config.COMMON.waveSpriteCount.get() - 1), 0, Config.COMMON.waveSpriteCount.get() - 1));
    }

    public static BlockPos getRandomPositionInChunk(LevelChunk chunk, RandomSource random)
    {
        int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        return new BlockPos(x, y, z);
    }

    public static boolean isWithinDistanceToPlayer(ServerLevel level, BlockPos pos, double maxDistance)
    {
        List<ServerPlayer> players = level.players();
        for (ServerPlayer player : players)
        {
            if (player.blockPosition().distSqr(pos) <= maxDistance * maxDistance)
            {
                return true;
            }
        }
        return false;
    }

    public static Vec3 getPositionOffset(Vec3 currentPos, double direction, double directionOffset, double distance)
    {
        double angle = Math.toRadians(direction + directionOffset);
        double x = currentPos.x() + (distance * Math.cos(angle));
        double z = currentPos.z() + (distance * Math.sin(angle));
        return new Vec3(x, currentPos.y(), z);
    }

    public static double getMoonPhase(Level level)
    {
        double phase = ((level.dayTime() / Level.TICKS_PER_DAY) % 8.0D + 8.0D) % 8.0D;
        double shift = phase - 3.75D;
        double adjust = shift < 0.0D ? 8.0D + shift : shift >= 8.0D ? shift - 8.0D : shift;
        return adjust >= 4.0D ? 1.0D - ((adjust - 4.0D) / 4.0D) : adjust / 4.0D;
    }

    public static double getSkyBrightness(Level level, float partialTicks)
    {
        double d0 = 1.0D - (level.getRainLevel(partialTicks) * 5.0F) / 16.0D;
        double d1 = 1.0D - (level.getThunderLevel(partialTicks) * 5.0F) / 16.0D;
        double d2 = 0.5D + 2.0D * Mth.clamp(Math.cos(level.getTimeOfDay(partialTicks) * (Math.PI * 2D)), -0.25D, 0.25D);
        return 1.0D - (((1.0D - d2 * d0 * d1) * 11.0D) / 11.0D);
    }

    public static Noise2D bioluminescenceNoise(Level level, long seed, int octaves)
    {
        double moonPhase = easeInOutExpoNorm(getMoonPhase(level), 0.0D, 1.0D, 0.875D, 0.9375D) * 0.3D;
        Noise2D layer = new OpenSimplex2D(seed).octaves(octaves).scaled(0.0D, 1.0D);
        return layer.easeInOutExpoNorm(0.0D, 1.0D, 0.78D - moonPhase, 0.875D - moonPhase);
    }

    public static int getServerChunkRenderDistance(ServerLevel level)
    {
        return level.getServer().getPlayerList().getViewDistance();
    }

    public static Vec3 getVectorAxis(Vec3 vec)
    {
        int index = Config.COMMON.vecAxisIndex.get();
        switch (index)
        {
            case 1:
                return new Vec3(vec.x(), vec.z(), vec.y());
            case 2:
                return new Vec3(vec.y(), vec.x(), vec.z());
            case 3:
                return new Vec3(vec.y(), vec.z(), vec.x());
            case 4:
                return new Vec3(vec.z(), vec.x(), vec.y());
            case 5:
                return new Vec3(vec.z(), vec.y(), vec.x());
            default:
                return vec;
        }
    }

    public static boolean isWithinAngle(Vec3 vector1, Vec3 vector2, double angle)
    {
        Vec3 vec1 = getVectorAxis(vector1);
        Vec3 vec2 = getVectorAxis(vector2);

        double dotProduct = vec1.dot(vec2);
        double magnitude1 = vec1.length();
        double magnitude2 = vec2.length();

        double cosineOfAngle = dotProduct / (magnitude1 * magnitude2);
        double angleBetweenVectors = Math.toDegrees(Math.asin(cosineOfAngle));

        return angleBetweenVectors <= angle;
    }
}
