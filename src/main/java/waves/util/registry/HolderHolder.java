package waves.util.registry;

import java.util.Objects;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public interface HolderHolder<R>
{
    DeferredHolder<R, ? extends R> holder();

    default ResourceKey<R> key()
    {
        return Objects.requireNonNull(holder().getKey());
    }
}
