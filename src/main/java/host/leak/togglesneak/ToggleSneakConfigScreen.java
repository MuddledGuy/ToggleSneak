package host.leak.togglesneak;

import java.util.Locale;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ToggleSneakConfigScreen extends Screen {
    private final Screen parent;

    public ToggleSneakConfigScreen(Screen parent) {
        super(Component.translatable("togglesneak.config.panel.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 155;
        int y = 48;
        int rowHeight = 24;
        int controlWidth = 310;

        addBooleanOption(x, y, controlWidth, "togglesneak.config.panel.sneak", ToggleSneakConfig.TOGGLE_SNEAK, () -> ToggleSneakClient.applyConfigFeatureState());
        addBooleanOption(x, y + rowHeight, controlWidth, "togglesneak.config.panel.sprint", ToggleSneakConfig.TOGGLE_SPRINT, () -> ToggleSneakClient.applyConfigFeatureState());
        addBooleanOption(x, y + rowHeight * 2, controlWidth, "togglesneak.config.panel.flyboost", ToggleSneakConfig.FLY_BOOST, null);
        addBooleanOption(x, y + rowHeight * 3, controlWidth, "togglesneak.config.panel.hud", ToggleSneakConfig.HUD_ENABLED, null);

        addEnumOption(x, y + rowHeight * 4, controlWidth, "togglesneak.config.panel.display", ToggleSneakConfig.STATUS_DISPLAY, ToggleSneakConfig.StatusDisplay.values(), "togglesneak.config.status.");
        addEnumOption(x, y + rowHeight * 5, controlWidth, "togglesneak.config.panel.hpos", ToggleSneakConfig.DISPLAY_H_POSITION, ToggleSneakConfig.HorizontalPosition.values(), "togglesneak.config.hpos.");
        addEnumOption(x, y + rowHeight * 6, controlWidth, "togglesneak.config.panel.vpos", ToggleSneakConfig.DISPLAY_V_POSITION, ToggleSneakConfig.VerticalPosition.values(), "togglesneak.config.vpos.");

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> closeToParent())
                .bounds(this.width / 2 - 100, this.height - 32, 200, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.textRenderer().accept((this.width - this.font.width(this.title)) / 2, 20, this.title);
    }

    @Override
    public void onClose() {
        closeToParent();
    }

    private void addBooleanOption(int x, int y, int width, String labelKey, ForgeConfigSpec.BooleanValue config, Runnable afterChange) {
        addRenderableWidget(Checkbox.builder(Component.translatable(labelKey), this.font)
                .pos(x, y)
                .maxWidth(width)
                .selected(config.get())
                .onValueChange((checkbox, value) -> {
                    config.set(value);
                    config.save();
                    if (afterChange != null) {
                        afterChange.run();
                    }
                })
                .build());
    }

    private <T extends Enum<T>> void addEnumOption(int x, int y, int width, String labelKey, ForgeConfigSpec.EnumValue<T> config, T[] values, String valueTranslationPrefix) {
        addRenderableWidget(CycleButton.builder(value -> Component.translatable(valueTranslationPrefix + value.name().toLowerCase(Locale.ROOT)), config.get())
                .withValues(values)
                .create(x, y, width, 20, Component.translatable(labelKey), (button, value) -> {
                    config.set(value);
                    config.save();
                }));
    }

    private void closeToParent() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
