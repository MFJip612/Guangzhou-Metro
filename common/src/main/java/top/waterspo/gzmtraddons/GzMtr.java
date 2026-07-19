package top.waterspo.gzmtraddons;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import top.waterspo.gzmtraddons.block.ModBlocks;
import top.waterspo.gzmtraddons.item.ModCreativeTab;
import top.waterspo.gzmtraddons.item.ModItems;

public final class GzMtr {
    public static final String MOD_ID = "guangzhou_metro_add-ons";

    private GzMtr() {
    }

    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static void init() {
        ModCreativeTab.register();
        ModItems.register();
        ModBlocks.register();
    }
}
