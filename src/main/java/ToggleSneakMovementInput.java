import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public class ToggleSneakMovementInput extends oy {
    public boolean sprint;

    private final hu gameSettings;
    private final mod_ToggleSneak mod;
    private final Minecraft mc;
    private boolean sneakWasPressed;
    private boolean sprintWasPressed;

    ToggleSneakMovementInput(hu gameSettings, mod_ToggleSneak mod, Minecraft mc) {
        this.gameSettings = gameSettings;
        this.mod = mod;
        this.mc = mc;
    }

    @Override
    public void a() {
        vq player = mc.h;
        a = 0.0F;
        b = 0.0F;

        if (gameSettings.n.e) {
            b += 1.0F;
        }
        if (gameSettings.p.e) {
            b -= 1.0F;
        }
        if (gameSettings.o.e) {
            a += 1.0F;
        }
        if (gameSettings.q.e) {
            a -= 1.0F;
        }

        d = gameSettings.r.e;

        boolean physicalSneak = gameSettings.v.e;
        boolean physicalSprint = mod.sprintBinding.e || Keyboard.isKeyDown(mod.sprintBinding.d);
        if (mod.toggleSneak) {
            if (physicalSneak && !sneakWasPressed) {
                if (e || (!isRiding(player) && !isFlying(player))) {
                    e = !e;
                }
            }
            sneakWasPressed = physicalSneak;
        } else {
            e = physicalSneak;
            sneakWasPressed = false;
        }

        if (mod.toggleSneak && mod.sprintOverridesSneak && physicalSprint && !sprintWasPressed && !physicalSneak) {
            e = false;
        }

        if (e) {
            a *= 0.3F;
            b *= 0.3F;
        }

        sprintWasPressed = physicalSprint;
        sprint = physicalSprint || (mod.toggleSprint && b == 1.0F && !e);
        if (sprint && b == 1.0F && !player.W() && !player.aj() && !player.a(aad.q)) {
            player.d(true);
        }
    }

    String displayText() {
        vq player = mc.h;
        if (player == null) {
            return "";
        }

        String text = "";
        boolean flying = isFlying(player);
        boolean riding = isRiding(player);
        boolean holdingSneak = gameSettings.v.e;
        boolean holdingSprint = mod.sprintBinding.e || Keyboard.isKeyDown(mod.sprintBinding.d);

        if (flying) {
            text += "[Flying]  ";
        }
        if (riding) {
            text += "[Riding]  ";
        }

        if (e) {
            if (flying) {
                text += "[Descending]  ";
            } else if (riding) {
                text += "[Dismounting]  ";
            } else if (holdingSneak) {
                text += "[Sneaking (Key Held)]  ";
            } else {
                text += "[Sneaking (Toggled)]  ";
            }
        } else if (sprint && !flying && !riding) {
            if (holdingSprint) {
                text += "[Sprinting (Key Held)]";
            } else {
                text += "[Sprinting (Toggled)]";
            }
        }

        return text.trim();
    }

    private boolean isRiding(vq player) {
        return player.j != null;
    }

    private boolean isFlying(vq player) {
        return player.aT.b;
    }
}
