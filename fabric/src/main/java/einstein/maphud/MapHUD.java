package einstein.maphud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class MapHUD implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        HudRenderCallback.EVENT.register(CommonClass::renderMapHUD);
    }
}
