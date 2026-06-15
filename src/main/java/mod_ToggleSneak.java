import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.minecraft.client.Minecraft;

public class mod_ToggleSneak extends BaseMod {
    static final String VERSION = "1.1.0";

    boolean toggleSneak = true;
    boolean toggleSprint = false;
    boolean sprintOverridesSneak = false;
    boolean hudEnabled = true;
    String statusDisplay = "color coded";
    String displayHPos = "left";
    String displayVPos = "middle";

    afu sneakBinding;
    afu sprintBinding;

    private ToggleSneakMovementInput movementInput;
    private int displayWidth = -1;
    private int displayHeight = -1;
    private int rectX1;
    private int rectX2;
    private int rectSneakY1;
    private int rectSneakY2;
    private int rectSprintY1;
    private int rectSprintY2;

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getName() {
        return "Toggle Sneak&Sprint";
    }

    @Override
    public void load() {
        loadConfig();

        sneakBinding = new afu("Sneak function enable/disable", 34);
        sprintBinding = new afu("Sprint function enable/disable", 47);
        ModLoader.registerKey(this, sneakBinding, false);
        ModLoader.registerKey(this, sprintBinding, false);
        ModLoader.setInGameHook(this, true, false);
    }

    @Override
    public void keyboardEvent(afu keyBinding) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        if (mc.s != null) {
            return;
        }

        if (keyBinding == sneakBinding) {
            toggleSneak = !toggleSneak;
        } else if (keyBinding == sprintBinding) {
            toggleSprint = !toggleSprint;
        }
    }

    @Override
    public boolean onTickInGame(float tick, Minecraft mc) {
        if (mc.h != null) {
            if (!(mc.h.a instanceof ToggleSneakMovementInput)) {
                movementInput = new ToggleSneakMovementInput(mc.A, this, mc);
                mc.h.a = movementInput;
            }
            drawStatus(mc);
        }

        return true;
    }

    int displayStatus() {
        if (!hudEnabled || "no display".equals(statusDisplay)) {
            return 0;
        }
        if ("text only".equals(statusDisplay)) {
            return 2;
        }
        return 1;
    }

    private void drawStatus(Minecraft mc) {
        if (mc.s != null || movementInput == null) {
            return;
        }

        int mode = displayStatus();
        if (mode == 0) {
            return;
        }

        if (mode == 1) {
            computeDrawPosIfChanged(mc);
            drawRect(rectX1, rectSneakY1, rectX2, rectSneakY2,
                    toggleSneak ? colorPack(0, 0, 196, 196) : colorPack(196, 196, 196, 64));
            mc.q.b("Sneak", rectX1 + 2, rectSneakY1 + 2,
                    movementInput.e ? colorPack(255, 255, 0, 96) : colorPack(64, 64, 64, 128));
            drawRect(rectX1, rectSprintY1, rectX2, rectSprintY2,
                    toggleSprint ? colorPack(0, 0, 196, 196) : colorPack(196, 196, 196, 64));
            mc.q.b("Sprint", rectX1 + 2, rectSprintY1 + 2,
                    movementInput.sprint ? colorPack(255, 255, 0, 96) : colorPack(64, 64, 64, 128));
        } else {
            String text = movementInput.displayText();
            if (text.length() == 0) {
                return;
            }
            computeTextPos(mc, text);
            mc.q.b(text, rectX1, rectSneakY1, colorPack(255, 255, 255, 192));
        }
    }

    private void computeDrawPosIfChanged(Minecraft mc) {
        if (displayWidth == mc.d && displayHeight == mc.e) {
            return;
        }

        agd scaled = new agd(mc.A, mc.d, mc.e);
        int width = scaled.a();
        int textWidth = Math.max(mc.q.a("Sprint"), mc.q.a("Sneak"));
        if ("right".equals(displayHPos)) {
            rectX2 = width - 2;
            rectX1 = rectX2 - 2 - textWidth - 2;
        } else if ("center".equals(displayHPos)) {
            rectX1 = (width / 2) - (textWidth / 2) - 2;
            rectX2 = rectX1 + 2 + textWidth + 2;
        } else {
            rectX1 = 2;
            rectX2 = rectX1 + 2 + textWidth + 2;
        }

        int height = scaled.b();
        int textHeight = 8;
        if ("bottom".equals(displayVPos)) {
            rectSprintY2 = height - 2;
            rectSprintY1 = rectSprintY2 - 2 - textHeight - 2;
            rectSneakY2 = rectSprintY1 - 2;
            rectSneakY1 = rectSneakY2 - 2 - textHeight - 2;
        } else if ("middle".equals(displayVPos)) {
            rectSneakY1 = (height / 2) - 1 - 2 - textHeight - 2;
            rectSneakY2 = rectSneakY1 + 2 + textHeight + 2;
            rectSprintY1 = rectSneakY2 + 2;
            rectSprintY2 = rectSprintY1 + 2 + textHeight + 2;
        } else {
            rectSneakY1 = 2;
            rectSneakY2 = rectSneakY1 + 2 + textHeight + 2;
            rectSprintY1 = rectSneakY2 + 2;
            rectSprintY2 = rectSprintY1 + 2 + textHeight + 2;
        }

        displayWidth = mc.d;
        displayHeight = mc.e;
    }

    private void computeTextPos(Minecraft mc, String text) {
        agd scaled = new agd(mc.A, mc.d, mc.e);
        int width = scaled.a();
        int textWidth = mc.q.a(text);
        if ("right".equals(displayHPos)) {
            rectX1 = width - textWidth - 2;
        } else if ("center".equals(displayHPos)) {
            rectX1 = (width / 2) - (textWidth / 2) - 2;
        } else {
            rectX1 = 2;
        }

        int height = scaled.b();
        int textHeight = 8;
        if ("bottom".equals(displayVPos)) {
            rectSneakY1 = height - 2;
        } else if ("middle".equals(displayVPos)) {
            rectSneakY1 = (height / 2) + textHeight / 2;
        } else {
            rectSneakY1 = 2 + textHeight;
        }
    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        oo.a(left, top, right, bottom, color);
    }

    private int colorPack(int red, int green, int blue, int alpha) {
        return ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255) | ((alpha & 255) << 24);
    }

    private void loadConfig() {
        Properties properties = new Properties();
        File configFile = getConfigFile();
        if (configFile.exists()) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(configFile);
                properties.load(input);
            } catch (IOException ignored) {
            } finally {
                closeQuietly(input);
            }
        }

        toggleSneak = getBoolean(properties, "toggleSneakEnabled", toggleSneak);
        toggleSprint = getBoolean(properties, "toggleSprintEnabled", toggleSprint);
        sprintOverridesSneak = getBoolean(properties, "sprintOverridesSneak", sprintOverridesSneak);
        hudEnabled = getBoolean(properties, "hudEnabled", hudEnabled);
        statusDisplay = getOption(properties, "statusDisplay", statusDisplay,
                new String[] {"no display", "color coded", "text only"});
        displayHPos = getOption(properties, "displayHPosition", displayHPos,
                new String[] {"left", "center", "right"});
        displayVPos = getOption(properties, "displayVPosition", displayVPos,
                new String[] {"top", "middle", "bottom"});

        saveConfig(configFile);
    }

    private void saveConfig(File configFile) {
        Properties properties = new Properties();
        properties.setProperty("toggleSneakEnabled", Boolean.toString(toggleSneak));
        properties.setProperty("toggleSprintEnabled", Boolean.toString(toggleSprint));
        properties.setProperty("sprintOverridesSneak", Boolean.toString(sprintOverridesSneak));
        properties.setProperty("hudEnabled", Boolean.toString(hudEnabled));
        properties.setProperty("statusDisplay", statusDisplay);
        properties.setProperty("displayHPosition", displayHPos);
        properties.setProperty("displayVPosition", displayVPos);

        File parent = configFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(configFile);
            properties.store(output, "Toggle Sneak&Sprint settings");
        } catch (IOException ignored) {
        } finally {
            closeQuietly(output);
        }
    }

    private File getConfigFile() {
        return new File(new File(Minecraft.b(), "config"), "togglesneak.cfg");
    }

    private boolean getBoolean(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        return Boolean.valueOf(value).booleanValue();
    }

    private String getOption(Properties properties, String key, String fallback, String[] options) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                return value;
            }
        }
        return fallback;
    }

    private void closeQuietly(java.io.Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
