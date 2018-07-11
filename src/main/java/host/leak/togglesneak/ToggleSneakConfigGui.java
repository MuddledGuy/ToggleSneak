package host.leak.togglesneak;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ToggleSneakConfigGui extends GuiConfig {

	public ToggleSneakConfigGui(GuiScreen parent) {
		super(parent,
				(new ConfigElement(ToggleSneak.config.getCategory(Configuration.CATEGORY_GENERAL))).getChildElements(),
				"@MOD_ID@", false, false, I18n.format("togglesneak.config.panel.title"),
				GuiConfig.getAbridgedConfigPath(ToggleSneak.config.toString()));
    }
	
	@Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
    }
}
