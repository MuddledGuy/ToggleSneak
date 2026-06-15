package host.leak.togglesneak;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ToggleSneakConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue TOGGLE_SNEAK = BUILDER
            .comment("Will the sneak toggle function be enabled on startup?")
            .translation("togglesneak.config.panel.sneak")
            .define("toggleSneakEnabled", true);

    public static final ForgeConfigSpec.BooleanValue TOGGLE_SPRINT = BUILDER
            .comment("Will the sprint toggle function be enabled on startup?")
            .translation("togglesneak.config.panel.sprint")
            .define("toggleSprintEnabled", false);

    public static final ForgeConfigSpec.BooleanValue SPRINT_OVERRIDES_SNEAK = BUILDER
            .comment("Will pressing the sprint key untoggle sneak?")
            .translation("togglesneak.config.panel.sprintoverridessneak")
            .define("sprintOverridesSneak", false);

    public static final ForgeConfigSpec.BooleanValue FLY_BOOST = BUILDER
            .comment("Fly boost activated by sprint key in creative mode.")
            .translation("togglesneak.config.panel.flyboost")
            .define("flyBoostEnabled", false);

    public static final ForgeConfigSpec.DoubleValue FLY_BOOST_FACTOR = BUILDER
            .comment("Speed multiplier for fly boost.")
            .translation("togglesneak.config.panel.flyboostfactor")
            .defineInRange("flyBoostFactor", 4.0D, 1.0D, 8.0D);

    public static final ForgeConfigSpec.IntValue KEY_HOLD_TICKS = BUILDER
            .comment("Minimum key hold time in ticks to prevent toggle.")
            .translation("togglesneak.config.panel.keyholdticks")
            .defineInRange("keyHoldTicks", 7, 0, 200);

    public static final ForgeConfigSpec.BooleanValue HUD_ENABLED = BUILDER
            .comment("Will the status HUD be shown on startup?")
            .translation("togglesneak.config.panel.hud")
            .define("hudEnabled", true);

    public static final ForgeConfigSpec.EnumValue<StatusDisplay> STATUS_DISPLAY = BUILDER
            .comment("How to display the current status of the toggle function.")
            .translation("togglesneak.config.panel.display")
            .defineEnum("statusDisplay", StatusDisplay.COLOR_CODED);

    public static final ForgeConfigSpec.EnumValue<HorizontalPosition> DISPLAY_H_POSITION = BUILDER
            .comment("Horizontal position of onscreen display.")
            .translation("togglesneak.config.panel.hpos")
            .defineEnum("displayHPosition", HorizontalPosition.LEFT);

    public static final ForgeConfigSpec.EnumValue<VerticalPosition> DISPLAY_V_POSITION = BUILDER
            .comment("Vertical position of onscreen display.")
            .translation("togglesneak.config.panel.vpos")
            .defineEnum("displayVPosition", VerticalPosition.MIDDLE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ToggleSneakConfig() {
    }

    public enum StatusDisplay {
        NONE,
        COLOR_CODED,
        TEXT_ONLY
    }

    public enum HorizontalPosition {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalPosition {
        TOP,
        MIDDLE,
        BOTTOM
    }
}
