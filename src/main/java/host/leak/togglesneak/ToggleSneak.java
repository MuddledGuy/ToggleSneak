package host.leak.togglesneak;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ToggleSneak.MODID, name = "Toggle Sneak&Sprint", version = "1.1.0",
     acceptedMinecraftVersions = "[1.5.2]")
public class ToggleSneak implements ITickHandler {
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

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent event) {

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Toggle Sneak&Sprint settings.");
		while (configVersionFile < configVersionMod) upgradeConfigFrom(configVersionFile++);
		syncConfig();
	}

	@Mod.Init
	public void init(FMLInitializationEvent event) {
        kbList = getKeyBindings();
        KeyBindingRegistry.registerKeyBinding(new KeyHandler(kbList.toArray(new KeyBinding[kbList.size()]), new boolean[kbList.size()]) {
			@Override
			public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
				if (tickEnd && isRepeat) onKeyInput(kb);
			}

			@Override
			public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
			}

			@Override
			public EnumSet<TickType> ticks() {
				return EnumSet.of(TickType.CLIENT);
			}

			@Override
			public String getLabel() {
				return "ToggleSneakKeyHandler";
			}
        });
        TickRegistry.registerTickHandler(this, Side.CLIENT);
	}

	public void syncConfig() {

		toggleSneak = config.get(Configuration.CATEGORY_GENERAL, "toggleSneakEnabled", toggleSneak, "Will the sneak toggle function be enabled on startup?").getBoolean(toggleSneak);
		toggleSprint = config.get(Configuration.CATEGORY_GENERAL, "toggleSprintEnabled", toggleSprint, "Will the sprint toggle function be enabled on startup?").getBoolean(toggleSprint);
		sprintOverridesSneak = config.get(Configuration.CATEGORY_GENERAL, "sprintOverridesSneak", sprintOverridesSneak, "Will pressing the sprint key untoggle sneak?").getBoolean(sprintOverridesSneak);
		flyBoost = config.get(Configuration.CATEGORY_GENERAL, "flyBoostEnabled", flyBoost, "Fly boost activated by sprint key in creative mode").getBoolean(flyBoost);
		flyBoostFactor = (float) config.get(Configuration.CATEGORY_GENERAL, "flyBoostFactor", (double) flyBoostFactor, "Speed multiplier for fly boost").getDouble(flyBoostFactor);
		hudEnabled = config.get(Configuration.CATEGORY_GENERAL, "hudEnabled", hudEnabled, "Will the status HUD be shown?").getBoolean(hudEnabled);
		statusDisplay = config.get(Configuration.CATEGORY_GENERAL, "statusDisplay", statusDisplay, "Status display style").getString();
		displayHPos = config.get(Configuration.CATEGORY_GENERAL, "displayHPosition", displayHPos, "Horizontal position of onscreen display").getString();
		displayVPos = config.get(Configuration.CATEGORY_GENERAL, "displayVPosition", displayVPos, "Vertical position of onscreen display").getString();
		config.getCategory(Configuration.CATEGORY_GENERAL).remove("keyHoldTicks");
		guiDrawer.setDrawPosition(displayHPos, displayVPos, displayHPosOpts, displayVPosOpts);
		config.save();
	}
	
	private void upgradeConfigFrom(int version) {
		switch (version) {
		case 0:   // upgrade to version 1: convert displayStatus to string option
			if (config.hasKey(Configuration.CATEGORY_GENERAL,"displayEnabled")) {
				if (!config.hasKey(Configuration.CATEGORY_GENERAL,"statusDisplay")) 
					statusDisplay = config.get(Configuration.CATEGORY_GENERAL, "displayEnabled", true, "dummy").getBoolean(true)
						? statusDisplayOpts[1] : statusDisplayOpts[0];
				config.getCategory(Configuration.CATEGORY_GENERAL).remove("displayEnabled");
			}
			break;
		}
	}

	public List<KeyBinding> getKeyBindings() {
		
		List<KeyBinding> list = new ArrayList<KeyBinding>();		
		list.add(sneakBinding = new KeyBinding("Sneak function enable/disable", org.lwjgl.input.Keyboard.KEY_G));
		list.add(sprintBinding = new KeyBinding("Sprint function enable/disable", org.lwjgl.input.Keyboard.KEY_V));
		return list;
	}

	@Mod.PostInit
	public void postLoad(FMLPostInitializationEvent event) {
	
		MinecraftForge.EVENT_BUS.register(guiDrawer);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (mc.thePlayer != null) {
			EntityPlayerSP player = mc.thePlayer;
			if (!(player.movementInput instanceof MovementInputModded)) {
				player.movementInput = mim;
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return MODID;
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
