package top.waterspo.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import top.waterspo.Gzmetro;

@Mod(Gzmetro.MOD_ID)
public final class GzmetroForge {
    public GzmetroForge() {
        EventBuses.registerModEventBus(Gzmetro.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        // Run our common setup.
        Gzmetro.init();
    }
}
