package top.waterspo.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import top.waterspo.Gzmetro;

import static top.waterspo.item.ModCreativeTab.GZMTR_TAB;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Gzmetro.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> COMPANY_LOGO = ITEMS.register("company_logo",
            () -> new CompanyLogo(new Item.Properties().arch$tab(GZMTR_TAB))
    );

    public static void register() {
        ITEMS.register();

    }
}
