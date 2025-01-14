package waves.util.registry;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public interface IdHolder<T> extends Supplier<T>
{
    DeferredHolder<? super T, T> holder();

    default ResourceLocation getId()
    {
        return holder().getId();
    }
}
