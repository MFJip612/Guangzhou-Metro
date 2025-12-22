package top.waterspo.gzmetro.forge;

import top.waterspo.gzmetro.Gzmetro;
import net.minecraftforge.fml.common.Mod;

@Mod(Gzmetro.MOD_ID)
public final class GzmetroForge {
    public GzmetroForge() {
        // Run our common setup.
        Gzmetro.init();
    }
}
