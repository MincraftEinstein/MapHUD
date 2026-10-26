package einstein.maphud;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MapHUD {

    public MapHUD(IEventBus eventBus) {
        CommonClass.init();
    }
}
