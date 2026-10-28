package einstein.maphud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class MapHUD implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        ClientTickEvents.END_CLIENT_TICK.register(CommonClass::tick);
        HudRenderCallback.EVENT.register(CommonClass::renderMapHUD);
    }
}
