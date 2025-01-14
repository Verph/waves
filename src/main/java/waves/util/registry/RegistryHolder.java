package waves.util.registry;

import net.neoforged.neoforge.registries.DeferredHolder;

public interface RegistryHolder<R, T extends R> extends IdHolder<T>, HolderHolder<R>
{
    @Override
    DeferredHolder<R, T> holder();

    @Override
    default T get()
    {
        return holder().get();
    }
}
