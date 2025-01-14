package waves;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

import waves.client.ClientEventHandler;
import waves.client.WaveSounds;
import waves.client.particle.WaveParticles;
import waves.common.entities.WaveEntities;
import waves.config.Config;

@Mod(Waves.MOD_ID)
public class Waves
{
    public static final String MOD_ID = "waves";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Waves(ModContainer mod, IEventBus bus)
    {
        mod.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        EventHandler.init();
        WaveEntities.ENTITIES.register(bus);
        WaveSounds.SOUNDS.register(bus);
        WaveParticles.PARTICLE_TYPES.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init(mod, bus);
        }
    }
}