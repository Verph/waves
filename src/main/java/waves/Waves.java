package waves;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

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

    public Waves()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        Config.init();
        EventHandler.init();
        WaveEntities.ENTITIES.register(bus);
        WaveSounds.SOUNDS.register(bus);
        WaveParticles.PARTICLE_TYPES.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init();
        }
    }
}