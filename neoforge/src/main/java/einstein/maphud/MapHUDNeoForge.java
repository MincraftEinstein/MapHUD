package einstein.maphud;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@Mod(MapHUD.MOD_ID)
public class MapHUDNeoForge {

    public MapHUDNeoForge(IEventBus eventBus) {
        MapHUD.init();
        eventBus.addListener((ClientTickEvent.Post event) -> MapHUD.tick(Minecraft.getInstance()));
        eventBus.addListener((RenderGuiEvent.Post event) -> MapHUD.renderMapHUD(event.getGuiGraphics(), event.getPartialTick()));
    }
}
