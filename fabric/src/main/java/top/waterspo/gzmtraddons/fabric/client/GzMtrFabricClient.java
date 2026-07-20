package top.waterspo.gzmtraddons.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import top.waterspo.gzmtraddons.GzMtr;

public final class GzMtrFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GzMtr.clientInit();
    }
}
