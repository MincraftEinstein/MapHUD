package einstein.maphud;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.resources.ResourceLocation;

public class ModConfigs extends Config {

    public boolean visibleInAllPerspectives = false;
    public ValidatedFloat mapScale = new ValidatedFloat(0.75F, 1.5F, 0.5F);

    public ModConfigs() {
        super(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "main"), "", "", Constants.MOD_ID);
    }
}
