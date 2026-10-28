package einstein.maphud;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MapHUD.MOD_ID, dist = Dist.CLIENT)
public class MapHUDNeoForge {

    public MapHUDNeoForge(IEventBus eventBus) {
        MapHUD.init();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> MapHUD.tick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post event) -> MapHUD.renderMapHUD(event.getGuiGraphics(), event.getPartialTick()));
    }
}
