package top.waterspo.item;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.waterspo.Gzmetro;

public class ModCreativeTab {
    public static final CreativeTabRegistry.TabSupplier GZMTR_TAB = CreativeTabRegistry.create(
            new ResourceLocation(Gzmetro.MOD_ID, "gzmtr_tab"),
            () -> new ItemStack(ModItems.COMPANY_LOGO.get())
    );
    public static void register() {
        // No additional actions needed for now.
    }
}
