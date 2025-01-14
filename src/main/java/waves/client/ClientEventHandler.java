package waves.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import waves.client.particle.WaveParticle;
import waves.client.particle.WaveParticles;
import waves.common.entities.WaveEntities;

public class ClientEventHandler
{
    public static void init(ModContainer mod, IEventBus bus)
    {
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
