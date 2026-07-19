package top.waterspo.gzmtraddons;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import top.waterspo.gzmtraddons.block.ModBlocks;
import top.waterspo.gzmtraddons.item.ModCreativeTab;
import top.waterspo.gzmtraddons.item.ModItems;

public final class GzMtr {
    public static final String MOD_ID = "gzmtr";

    private GzMtr() {
    }

    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static void init() {
        // TODO: 在此编写公共初始化逻辑。
        System.out.println("Loading Gzmetro mod...");
        ModCreativeTab.register();
        ModBlocks.register();
        ModItems.register();
        System.out.println("Gzmetro mod initialized.");
    }
}
