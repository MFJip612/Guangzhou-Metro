package top.waterspo.gzmtraddons.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import top.waterspo.gzmtraddons.GzMtr;

@Mod(GzMtr.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        EventBuses.registerModEventBus(GzMtr.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        GzMtr.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GzMtr::clientInit);
    }
}
