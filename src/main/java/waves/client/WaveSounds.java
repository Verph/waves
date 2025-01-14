package waves.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import waves.util.WaveHelpers;

import static waves.Waves.MOD_ID;

public class WaveSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final RegistryObject<SoundEvent> WAVES_BREAKING = create("waves.waves_breaking");

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(WaveHelpers.identifier(name)));
    }
}
