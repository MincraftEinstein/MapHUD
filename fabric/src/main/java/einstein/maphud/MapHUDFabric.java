package einstein.maphud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class MapHUDFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MapHUD.init();
        ClientTickEvents.END_CLIENT_TICK.register(MapHUD::tick);
        HudRenderCallback.EVENT.register(MapHUD::renderMapHUD);
    }
}
