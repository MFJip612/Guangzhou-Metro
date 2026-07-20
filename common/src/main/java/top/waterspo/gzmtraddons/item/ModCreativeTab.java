package top.waterspo.gzmtraddons.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.waterspo.gzmtraddons.GzMtr;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(GzMtr.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> GZMTR_TAB = TABS.register("gzmtr_tab", () ->
            CreativeTabRegistry.create(
                    Component.translatable("itemGroup.gzmtr.gzmtr_tab"),
                    () -> new ItemStack(ModItems.COMPANY_LOGO.get())
            )
    );

    public static void register() {
        TABS.register();
    }
}
