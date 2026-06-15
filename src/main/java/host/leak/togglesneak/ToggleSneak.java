package host.leak.togglesneak;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToggleSneak.MODID)
public final class ToggleSneak {
    public static final String MODID = "togglesneak";

    public ToggleSneak(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, ToggleSneakConfig.SPEC);
        context.getContainer().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(parent -> new ToggleSneakConfigScreen(parent))
        );
    }
}
