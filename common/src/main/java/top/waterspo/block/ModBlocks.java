package top.waterspo.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.waterspo.Gzmetro;
import top.waterspo.item.ModCreativeTab;
import top.waterspo.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Gzmetro.MOD_ID, Registries.BLOCK);
    public static final RegistrySupplier<Block> STATION_BLOCK = registerBlock("station_block", () -> new StationBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;

    }

    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().arch$tab(ModCreativeTab.GZMTR_TAB)));
    }


    public static void register() {
        BLOCKS.register();
    }
}
