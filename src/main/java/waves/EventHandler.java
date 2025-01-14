package waves;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import waves.common.WavesTags;
import waves.common.entities.Wave;
import waves.common.entities.WaveEntities;
import waves.config.Config;
import waves.util.WaveHelpers;

public class EventHandler
{
    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(EventPriority.LOW, EventHandler::tickCoastalWaves);
    }

    public static void tickCoastalWaves(PlayerTickEvent.Pre event)
    {
        Player player = event.getEntity();
        Level level = player.level();

        if (player != null && level != null)
        {
            if (level.getGameTime() % Config.COMMON.waveSpawnFrequency.get() == 0)
            {
                RandomSource random = level.getRandom();
                int seaLevel = level.getSeaLevel() - 1;
                Vec3 playerPos = player.position();
                float rainfall = 1.0F + level.getRainLevel(1.0F);

                double renderDistance = level instanceof ServerLevel server ? WaveHelpers.getServerChunkRenderDistance(server) : Minecraft.getInstance().options.renderDistance().get().doubleValue();
                int iterations = (int) Math.ceil(Math.pow(Math.PI * renderDistance, Config.COMMON.waveSpawnAmount.get()));
                int searchRadius = Config.COMMON.waveSearchDistance.get();
                double spawnRadius = Mth.clamp((renderDistance * 16.0D) + Config.COMMON.waveSpawnDistance.get(), 0.0D, Double.MAX_VALUE);
                double minShoreDistance = Config.COMMON.waveSpawnDistanceFromShoreMin.get();
                double maxShoreDistance = Config.COMMON.waveSpawnDistanceFromShoreMax.get();

                if (spawnRadius > 0.0D)
                {
                    for (int i = 0; i < iterations * rainfall; i++)
                    {
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double radius = random.nextDouble() * spawnRadius;
                        double x = playerPos.x() + radius * Math.cos(angle);
                        double z = playerPos.z() + radius * Math.sin(angle);

                        Vec3 wavePos = new Vec3(x, seaLevel, z);

                        if (!WaveHelpers.isWithinAngle(player.getLookAngle(), wavePos, Config.COMMON.waveSpawningFOVLimit.get())) continue;

                        boolean isOverlapping = false;
                        if (level instanceof ServerLevel server)
                        {
                            for (ServerPlayer serverPlayer : server.players())
                            {
                                if (serverPlayer.equals(player)) continue;
                                Vec3 playerPosMul = playerPos.multiply(1.0D, 0.0D, 1.0D);
                                Vec3 serverPlayerPosMul = serverPlayer.position().multiply(1.0D, 0.0D, 1.0D);
                                if (playerPosMul.distanceTo(serverPlayerPosMul) > spawnRadius * 2.0D) continue;
                                if (playerPosMul.distanceTo(wavePos) > serverPlayerPosMul.distanceTo(wavePos))
                                {
                                    isOverlapping = true;
                                    break;
                                }
                            }
                        }

                        if (!isOverlapping)
                        {
                            BlockPos waveBlockPos = WaveHelpers.toBlockPos(wavePos);

                            if (level.isLoaded(waveBlockPos) && level.getBiome(waveBlockPos).is(WavesTags.Biomes.HAS_WAVES))
                            {
                                Vec3 shorePos = WaveHelpers.findNearestShorePos(level, wavePos, searchRadius, minShoreDistance);

                                if (shorePos != null && level.isLoaded(WaveHelpers.toBlockPos(shorePos)) && WaveHelpers.calculateDistanceToShore(shorePos, wavePos) <= maxShoreDistance && WaveHelpers.isSurroundedByWaterCircle(level, wavePos, Mth.floor(minShoreDistance)))
                                {
                                    Vec3 direction = new Vec3(shorePos.x() - wavePos.x(), shorePos.y() - wavePos.y(), shorePos.z() - wavePos.z()).normalize();
                                    float waveWidth = 3.0F + random.nextInt(12);
                                    float waveSpeed = 0.1F * rainfall;

                                    int surroundWaterBlocks = WaveHelpers.getSurroundingWaterBlocks(level, shorePos);
                                    int surroundWaterBlocksJitter = Mth.clamp(surroundWaterBlocks + random.nextInt(2) - random.nextInt(2), 0, 4);
                                    int waveSize = Mth.clamp(Math.round((WaveHelpers.lerp(surroundWaterBlocksJitter, 0, 4) / 4.0F) * 2.0F), 0, 2);

                                    double heightOffset = random.nextDouble() * 0.01D;
                                    Vec3 wavePosOffset = wavePos.add(0.0D, 1.001D + heightOffset, 0.0D);
                                    Vec3 shorePosOffset = shorePos.add(0.0D, 1.001D + heightOffset, 0.0D);

                                    Wave wave = new Wave(WaveEntities.WAVES.get(), level, wavePosOffset, shorePosOffset, direction, 0.01F, waveWidth, waveSpeed, waveSize);
                                    level.addFreshEntity(wave);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

