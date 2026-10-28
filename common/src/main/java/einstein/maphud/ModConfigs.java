package einstein.maphud;

import com.mojang.blaze3d.platform.InputConstants;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.screen.context.FzzyKeybind;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedKeybind;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.ResourceLocation;

public class ModConfigs extends Config {

    public boolean visibleInAllPerspectives = false;
    public ValidatedKeybind selectSlotKey = new ValidatedKeybind(new FzzyKeybind.Builder().keyboard(InputConstants.KEY_M, true).build());
    public ValidatedInt mapScale = new ValidatedInt(2, 3, 1);
    public ValidatedFloat mapXPos = new ValidatedFloat(0, 1, 0);
    public ValidatedFloat mapYPos = new ValidatedFloat(0, 1, 0);

    public ModConfigs() {
        super(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "main"), "", "", Constants.MOD_ID);
    }
}
