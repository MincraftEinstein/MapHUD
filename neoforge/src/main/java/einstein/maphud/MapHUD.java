package einstein.maphud;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@Mod(Constants.MOD_ID)
public class MapHUD {

    public MapHUD(IEventBus eventBus) {
        CommonClass.init();
        eventBus.addListener((RenderGuiEvent.Post event) -> CommonClass.renderMapHUD(event.getGuiGraphics(), event.getPartialTick()));
    }
}
