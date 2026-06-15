package host.leak.togglesneak;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(modid = "@MOD_ID@", name = "@MOD_NAME@", version = "@MOD_VERSION@",
     acceptedMinecraftVersions = "[@MINECRAFT_VERSION@]", canBeDeactivated = true,
	 guiFactory = "host.leak.togglesneak.ToggleSneakGuiFactory")
public class ToggleSneak {
	public static final String MODID = "togglesneak";

	public static Configuration config;
	private final int configVersionMod = 1;
	private int configVersionFile = 0;
	public boolean toggleSneak = true;
	public boolean toggleSprint = false;
	public boolean sprintOverridesSneak = false;
	public boolean flyBoost = false;
	public float flyBoostFactor = 4.0F;
	public boolean hudEnabled = true;
	private final String statusDisplayOpts[] = {"no display", "color coded", "text only"};
	public String statusDisplay = statusDisplayOpts[1];
	private final String displayHPosOpts[] = {"left", "center", "right"};
	public String displayHPos = displayHPosOpts[0];
	private final String displayVPosOpts[] = {"top", "middle", "bottom"};
	public String displayVPos = displayVPosOpts[1];
	private KeyBinding sneakBinding;
	private KeyBinding sprintBinding;
	private List<KeyBinding> kbList;
	private final Minecraft mc = Minecraft.getMinecraft();
	private final MovementInputModded mim = new MovementInputModded(mc.gameSettings, this);
	public final GuiDrawer guiDrawer = new GuiDrawer(this, mim);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		config = new Configuration(event.getSuggestedConfigurationFile(), Integer.toString(configVersionMod));
		config.setCategoryComment(Configuration.CATEGORY_GENERAL, "ATTENTION: Editing this file manually is no longer necessary. \n" +
				"Use the Mods button on Minecraft's home screen to modify these settings.");
		try { configVersionFile = Integer.parseInt(config.getLoadedConfigVersion()); } catch (NumberFormatException e) { };
		while (configVersionFile < configVersionMod) upgradeConfigFrom(configVersionFile++);
		syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
        kbList = getKeyBindings();
        for(KeyBinding kb: kbList) ClientRegistry.registerKeyBinding(kb);

		FMLCommonHandler.instance().bus().register(this);
	}

	@EventHandler
	public void deactivate(FMLModDisabledEvent event) {
		FMLCommonHandler.instance().bus().unregister(this);
		MinecraftForge.EVENT_BUS.unregister(guiDrawer);
		if (mc.thePlayer != null)
			mc.thePlayer.movementInput = new MovementInputFromOptions(mc.gameSettings);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {

		if (eventArgs.modID.equals("@MOD_ID@")) syncConfig();
	}

	public void syncConfig() {

		toggleSneak = config.getBoolean("toggleSneakEnabled", Configuration.CATEGORY_GENERAL, toggleSneak, "Will the sneak toggle function be enabled on startup?", "togglesneak.config.panel.sneak");
		toggleSprint = config.getBoolean("toggleSprintEnabled", Configuration.CATEGORY_GENERAL, toggleSprint, "Will the sprint toggle function be enabled on startup?", "togglesneak.config.panel.sprint");
		sprintOverridesSneak = config.getBoolean("sprintOverridesSneak", Configuration.CATEGORY_GENERAL, sprintOverridesSneak, "Will pressing the sprint key untoggle sneak?", "togglesneak.config.panel.sprintoverridessneak");
		flyBoost = config.getBoolean("flyBoostEnabled", Configuration.CATEGORY_GENERAL, flyBoost, "Fly boost activated by sprint key in creative mode", "togglesneak.config.panel.flyboost");
		flyBoostFactor = config.getFloat("flyBoostFactor", Configuration.CATEGORY_GENERAL, flyBoostFactor, 1.0F, 8.0F, "Speed multiplier for fly boost", "togglesneak.config.panel.flyboostfactor");
		hudEnabled = config.getBoolean("hudEnabled", Configuration.CATEGORY_GENERAL, hudEnabled, "Will the status HUD be shown?", "togglesneak.config.panel.hud");
		statusDisplay = config.getString("statusDisplay", Configuration.CATEGORY_GENERAL, statusDisplay, "Status display style", statusDisplayOpts, "togglesneak.config.panel.display");
		displayHPos = config.getString("displayHPosition", Configuration.CATEGORY_GENERAL, displayHPos, "Horizontal position of onscreen display", displayHPosOpts, "togglesneak.config.panel.hpos");
		displayVPos = config.getString("displayVPosition", Configuration.CATEGORY_GENERAL, displayVPos, "Vertical position of onscreen display", displayVPosOpts, "togglesneak.config.panel.vpos");
		config.getCategory(Configuration.CATEGORY_GENERAL).remove("keyHoldTicks");
		guiDrawer.setDrawPosition(displayHPos, displayVPos, displayHPosOpts, displayVPosOpts);
		config.save();
	}
	
	private void upgradeConfigFrom(int version) {
		switch (version) {
		case 0:   // upgrade to version 1: convert displayStatus to string option
			if (config.hasKey(Configuration.CATEGORY_GENERAL,"displayEnabled")) {
				if (!config.hasKey(Configuration.CATEGORY_GENERAL,"statusDisplay")) 
					statusDisplay = config.getBoolean("displayEnabled", Configuration.CATEGORY_GENERAL, true, "dummy")
						? statusDisplayOpts[1] : statusDisplayOpts[0];
				config.getCategory(Configuration.CATEGORY_GENERAL).remove("displayEnabled");
			}
			break;
		}
	}

	public List<KeyBinding> getKeyBindings() {
		
		List<KeyBinding> list = new ArrayList<KeyBinding>();		
		list.add(sneakBinding = new KeyBinding("togglesneak.key.toggle.sneak", org.lwjgl.input.Keyboard.KEY_G, "togglesneak.key.categories"));
		list.add(sprintBinding = new KeyBinding("togglesneak.key.toggle.sprint", org.lwjgl.input.Keyboard.KEY_V, "togglesneak.key.categories"));
		return list;
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
	
		MinecraftForge.EVENT_BUS.register(guiDrawer);
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		
		clientTick();
	}

	public void clientTick() {
		
		if ((mc.thePlayer != null) && (!(mc.thePlayer.movementInput instanceof MovementInputModded))) {
			mc.thePlayer.movementInput = mim;
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {

		for(KeyBinding kb: kbList) {
			if (kb.isPressed()) onKeyInput(kb);
		}
	}

	public void onKeyInput(KeyBinding kb) {
		
		if ((mc.currentScreen instanceof GuiChat)) return;
		
		if (kb == sneakBinding) toggleSneak = !toggleSneak;
		if (kb == sprintBinding) toggleSprint = !toggleSprint;
	}
	
	public int displayStatus() {
		if (!hudEnabled) return 0;
		for (int i=0; i < statusDisplayOpts.length; i++) 
			if (statusDisplayOpts[i].equals(statusDisplay)) return i;
		return 0;
	}

}
