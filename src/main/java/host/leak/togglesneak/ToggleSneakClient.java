package host.leak.togglesneak;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Input;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ToggleSneak.MODID, value = Dist.CLIENT)
public final class ToggleSneakClient {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.parse("togglesneak:keys"));
    private static final Identifier HUD_LAYER = Identifier.parse("togglesneak:status");

    private static final KeyMapping TOGGLE_SNEAK_KEY = new KeyMapping(
            "togglesneak.key.toggle.sneak",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY,
            0
    );

    private static final KeyMapping TOGGLE_SPRINT_KEY = new KeyMapping(
            "togglesneak.key.toggle.sprint",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY,
            1
    );

    private static boolean sneakFeatureEnabled = ToggleSneakConfig.TOGGLE_SNEAK.getDefault();
    private static boolean sprintFeatureEnabled = ToggleSneakConfig.TOGGLE_SPRINT.getDefault();
    private static boolean featureSettingsInitialized;
    private static boolean sneakToggled;
    private static boolean sprintToggled;
    private static boolean wasSneakPhysicallyDown;
    private static boolean wasSprintPhysicallyDown;
    private static float originalFlySpeed = -1.0F;
    private static float boostedFlySpeed;

    private ToggleSneakClient() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_SNEAK_KEY);
        event.register(TOGGLE_SPRINT_KEY);
    }

    @SubscribeEvent
    public static void registerGuiLayers(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().addAbove(ForgeLayeredDraw.POST_SLEEP_STACK, HUD_LAYER, ForgeLayeredDraw.CHAT_OVERLAY, ToggleSneakClient::renderStatusOverlay);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        if (MC.player == null || MC.options == null) {
            return;
        }

        if (!featureSettingsInitialized) {
            applyConfigFeatureState();
            featureSettingsInitialized = true;
        }

        LocalPlayer player = MC.player;
        handleDedicatedToggleKeys();

        if (!sneakFeatureEnabled) {
            sneakToggled = false;
            wasSneakPhysicallyDown = false;
            wasSprintPhysicallyDown = false;
        }
        if (!sprintFeatureEnabled) {
            sprintToggled = false;
        }

        updateFlyBoost(player);
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (event.getEntity() != MC.player || MC.options == null) {
            return;
        }

        LocalPlayer player = MC.player;
        boolean physicalSneak = isPhysicallyDown(MC.options.keyShift);
        boolean physicalSprint = isPhysicallyDown(MC.options.keySprint);
        boolean sneakPressed = physicalSneak && !wasSneakPhysicallyDown;
        boolean sprintPressed = physicalSprint && !wasSprintPhysicallyDown;
        Input input = event.getInput().keyPresses;
        boolean shift = input.shift();
        boolean sprint = input.sprint();

        if (sneakFeatureEnabled) {
            if (sneakPressed) {
                toggleSneakState(player);
            }
            if (ToggleSneakConfig.SPRINT_OVERRIDES_SNEAK.get() && sprintPressed) {
                sneakToggled = false;
            }
            shift = physicalSneak || sneakToggled;
        }

        wasSneakPhysicallyDown = physicalSneak;
        wasSprintPhysicallyDown = physicalSprint;

        if (sprintFeatureEnabled) {
            sprintToggled = input.forward() && !input.backward() && !shift;
            sprint = sprint || sprintToggled;
        }

        event.getInput().keyPresses = new Input(
                input.forward(),
                input.backward(),
                input.left(),
                input.right(),
                input.jump(),
                shift,
                sprint
        );
    }

    private static void renderStatusOverlay(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        ToggleSneakConfig.StatusDisplay display = ToggleSneakConfig.STATUS_DISPLAY.get();
        if (!ToggleSneakConfig.HUD_ENABLED.get() || display == ToggleSneakConfig.StatusDisplay.NONE || MC.player == null) {
            return;
        }

        if (display == ToggleSneakConfig.StatusDisplay.COLOR_CODED) {
            drawStatusBoxes(graphics);
        } else {
            drawStatusText(graphics, statusText());
        }
    }

    private static void handleDedicatedToggleKeys() {
        while (TOGGLE_SNEAK_KEY.consumeClick()) {
            sneakFeatureEnabled = !sneakFeatureEnabled;
            if (!sneakFeatureEnabled) {
                sneakToggled = false;
                wasSneakPhysicallyDown = false;
                wasSprintPhysicallyDown = false;
            }
        }
        while (TOGGLE_SPRINT_KEY.consumeClick()) {
            sprintFeatureEnabled = !sprintFeatureEnabled;
            if (!sprintFeatureEnabled) {
                sprintToggled = false;
            }
        }
    }

    static void applyConfigFeatureState() {
        sneakFeatureEnabled = ToggleSneakConfig.TOGGLE_SNEAK.get();
        sprintFeatureEnabled = ToggleSneakConfig.TOGGLE_SPRINT.get();

        if (!sneakFeatureEnabled) {
            sneakToggled = false;
            wasSneakPhysicallyDown = false;
            wasSprintPhysicallyDown = false;
        }
        if (!sprintFeatureEnabled) {
            sprintToggled = false;
        }
    }

    private static void toggleSneakState(LocalPlayer player) {
        if (sneakToggled || (!player.isPassenger() && !player.getAbilities().flying)) {
            sneakToggled = !sneakToggled;
        }
    }

    private static boolean isPhysicallyDown(KeyMapping keyMapping) {
        if (MC.getWindow() == null) {
            return false;
        }
        return InputConstants.isKeyDown(MC.getWindow(), keyMapping.getKey().getValue());
    }

    private static void updateFlyBoost(LocalPlayer player) {
        boolean boosting = ToggleSneakConfig.FLY_BOOST.get()
                && player.getAbilities().instabuild
                && player.getAbilities().flying
                && sprintToggled;

        if (boosting) {
            if (originalFlySpeed < 0.0F || player.getAbilities().getFlyingSpeed() != boostedFlySpeed) {
                originalFlySpeed = player.getAbilities().getFlyingSpeed();
            }
            boostedFlySpeed = originalFlySpeed * ToggleSneakConfig.FLY_BOOST_FACTOR.get().floatValue();
            player.getAbilities().setFlyingSpeed(boostedFlySpeed);

            double verticalBoost = 0.15D * (ToggleSneakConfig.FLY_BOOST_FACTOR.get() - 1.0D);
            if (player.input.keyPresses.shift()) {
                player.setDeltaMovement(player.getDeltaMovement().add(0.0D, -verticalBoost, 0.0D));
            }
            if (player.input.keyPresses.jump()) {
                player.setDeltaMovement(player.getDeltaMovement().add(0.0D, verticalBoost, 0.0D));
            }
        } else if (originalFlySpeed >= 0.0F) {
            player.getAbilities().setFlyingSpeed(originalFlySpeed);
            originalFlySpeed = -1.0F;
        }
    }

    private static void drawStatusBoxes(GuiGraphicsExtractor graphics) {
        int textWidth = Math.max(MC.font.width(Component.translatable("togglesneak.display.label.sneak")),
                MC.font.width(Component.translatable("togglesneak.display.label.sprint")));
        int width = textWidth + 4;
        int height = MC.font.lineHeight + 4;
        int x = horizontalX(width);
        int sneakY = verticalY(height * 2 + 2);
        int sprintY = sneakY + height + 2;

        drawStatusBox(graphics, Component.translatable("togglesneak.display.label.sneak"), x, sneakY, width, height, sneakFeatureEnabled, sneakToggled);
        drawStatusBox(graphics, Component.translatable("togglesneak.display.label.sprint"), x, sprintY, width, height, sprintFeatureEnabled, sprintToggled);
    }

    private static void drawStatusBox(GuiGraphicsExtractor graphics, Component label, int x, int y, int width, int height, boolean featureEnabled, boolean toggled) {
        int background = featureEnabled ? argb(196, 0, 0, 196) : argb(64, 196, 196, 196);
        int foreground = toggled ? argb(255, 255, 255, 0) : argb(128, 64, 64, 64);
        graphics.fill(x, y, x + width, y + height, background);
        graphics.textRenderer().defaultParameters(graphics.textRenderer().defaultParameters().withOpacity(((foreground >>> 24) & 255) / 255.0F));
        graphics.textRenderer().accept(x + 2, y + 2, label);
    }

    private static void drawStatusText(GuiGraphicsExtractor graphics, String text) {
        if (text.isEmpty()) {
            return;
        }

        int width = MC.font.width(text);
        int x = horizontalX(width);
        int y = verticalY(MC.font.lineHeight);
        graphics.textRenderer().defaultParameters(graphics.textRenderer().defaultParameters().withOpacity(192.0F / 255.0F));
        graphics.textRenderer().accept(x, y, Component.literal(text));
    }

    private static int horizontalX(int width) {
        return switch (ToggleSneakConfig.DISPLAY_H_POSITION.get()) {
            case RIGHT -> MC.getWindow().getGuiScaledWidth() - width - 2;
            case CENTER -> (MC.getWindow().getGuiScaledWidth() - width) / 2;
            case LEFT -> 2;
        };
    }

    private static int verticalY(int height) {
        return switch (ToggleSneakConfig.DISPLAY_V_POSITION.get()) {
            case BOTTOM -> MC.getWindow().getGuiScaledHeight() - height - 2;
            case MIDDLE -> (MC.getWindow().getGuiScaledHeight() - height) / 2;
            case TOP -> 2;
        };
    }

    private static String statusText() {
        StringBuilder text = new StringBuilder();
        LocalPlayer player = MC.player;
        if (player == null) {
            return "";
        }

        if (player.getAbilities().flying) {
            text.append(originalFlySpeed > 0.0F ? "[Flying Boost]  " : "[Flying]  ");
        }
        if (player.isPassenger()) {
            text.append("[Riding]  ");
        }
        if (sneakToggled) {
            text.append(player.getAbilities().flying ? "[Descending]  " : "[Sneaking (Toggled)]  ");
        }
        if (sprintToggled && !player.getAbilities().flying && !player.isPassenger()) {
            text.append("[Sprinting (Toggled)]");
        }
        return text.toString().trim();
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return ((alpha & 255) << 24) | ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
    }
}
