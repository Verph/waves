package waves.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import waves.client.particle.WaveParticle;
import waves.client.particle.WaveParticles;
import waves.common.entities.WaveEntities;

public class ClientEventHandler
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::registerEntityRenderers);
        bus.addListener(ClientEventHandler::registerParticleFactories);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(WaveEntities.WAVES.get(), NoopRenderer::new);
    }

    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(WaveParticles.WAVES.get(), WaveParticle::provider);
    }
}
