package top.waterspo;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import top.waterspo.block.ModBlocks;
import top.waterspo.item.ModCreativeTab;
import top.waterspo.item.ModItems;

import java.util.function.Supplier;

/**
 * 公共端的主入口，供各平台调度。
 */
public final class Gzmetro {
    public static final String MOD_ID = "gzmtr";

    private Gzmetro() {
    }

    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static void init() {
        // TODO: 在此编写公共初始化逻辑。
        System.out.println("Loading Gzmetro mod...");
        ModCreativeTab.register();
        ModBlocks.register();
        ModItems.register();
        for (int i = 0; i < 50000; i++) {
            int a = 1 + 1;
        }
        System.out.println("Gzmetro mod initialized.");
    }
}

