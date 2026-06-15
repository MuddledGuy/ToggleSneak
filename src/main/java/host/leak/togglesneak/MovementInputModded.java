package host.leak.togglesneak;

import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class MovementInputModded extends MovementInput {

	private final GameSettings gameSettings;
	public boolean sprint;
	private ToggleSneak ITS;
	private Minecraft mc;
	private boolean sneakWasPressed;
	private boolean sprintWasPressed;
	private EntityPlayer player;
	private float originalFlySpeed = -1.0F;
	private float boostedFlySpeed;

	public MovementInputModded(GameSettings gameSettings, ToggleSneak ITS) {
		this.gameSettings = gameSettings;
		this.sprint = false;
		this.ITS = ITS;
		this.mc = Minecraft.getMinecraft(); // we'll need replace the static ref by a link passed as parameter
		this.sneakWasPressed = false;
		this.sprintWasPressed = false;
	}

	public void updatePlayerMoveState() {
		
		player = mc.thePlayer;
		moveStrafe = 0.0F;
		moveForward = 0.0F;

		if (isKeyDown(gameSettings.keyBindForward)) moveForward++;
		if (isKeyDown(gameSettings.keyBindBack)) moveForward--;
		if (isKeyDown(gameSettings.keyBindLeft)) moveStrafe++;
		if (isKeyDown(gameSettings.keyBindRight)) moveStrafe--;

		jump = isKeyDown(gameSettings.keyBindJump);
		
		boolean physicalSneak = isKeyDown(gameSettings.keyBindSneak);
		boolean physicalSprint = Keyboard.isKeyDown(Keyboard.KEY_V);
		if (ITS.toggleSneak) {
			if (physicalSneak && !sneakWasPressed) {
				if (sneak || (!isRiding(player) && !player.capabilities.isFlying)) {
					sneak = !sneak;
				}
			}
			sneakWasPressed = physicalSneak;
		} else {
			sneak = physicalSneak;
			sneakWasPressed = false;
		}
		if (ITS.toggleSneak && ITS.sprintOverridesSneak && physicalSprint && !sprintWasPressed && !physicalSneak) {
			sneak = false;
		}
		
		if (sneak) {
			moveStrafe *= 0.3F;
			moveForward *= 0.3F;
		}
		
		sprintWasPressed = physicalSprint;
		sprint = physicalSprint || (ITS.toggleSprint && moveForward == 1.0F && !sneak);
		
		// sprint conditions same as in net.minecraft.client.entity.EntityPlayerSP.onLivingUpdate()
		// check for hungry or flying. But nvm, if conditions not met, sprint will 
		// be canceled there afterwards anyways 
		if (sprint && moveForward == 1.0F && isOnGround(player) && !player.isUsingItem()
				&& !isPotionActive(player, Potion.blindness)) setSprinting(player, true);
		
		if (ITS.flyBoost && player.capabilities.isCreativeMode && player.capabilities.isFlying 
				&& (mc.renderViewEntity == player) && sprint) {
			
			if (originalFlySpeed < 0.0F || this.player.capabilities.getFlySpeed() != boostedFlySpeed)
				originalFlySpeed = this.player.capabilities.getFlySpeed();
			boostedFlySpeed = originalFlySpeed * ITS.flyBoostFactor;
			player.capabilities.setFlySpeed(boostedFlySpeed);
			
			if (sneak) addMotionY(player, -0.15D * (double)(ITS.flyBoostFactor - 1.0F));
			if (jump) addMotionY(player, 0.15D * (double)(ITS.flyBoostFactor - 1.0F));
				
		} else {
			if (player.capabilities.getFlySpeed() == boostedFlySpeed)
				this.player.capabilities.setFlySpeed(originalFlySpeed);
			originalFlySpeed = -1.0F;
		}

	}
	
	public String displayText() {
		
		// This is a slightly refactored version of Deez's UpdateStatus( ... ) function
		// found here https://github.com/DouweKoopmans/ToggleSneak/blob/master/src/main/java/deez/togglesneak/CustomMovementInput.java
		
		String displayText = "";
		EntityPlayer player = mc.thePlayer;
		boolean isFlying = player.capabilities.isFlying;
		boolean isRiding = isRiding(player);
		boolean isHoldingSneak = isKeyDown(gameSettings.keyBindSneak);
		boolean isHoldingSprint = Keyboard.isKeyDown(Keyboard.KEY_V);
		
		if (isFlying) {
			if (originalFlySpeed > 0.0F) {
				displayText += "[Flying (" + (new DecimalFormat("#.0")).format(boostedFlySpeed/originalFlySpeed) + "x Boost)]  ";								
			} else {
				displayText += "[Flying]  ";				
			}
		}
		if (isRiding) displayText += "[Riding]  ";
		
		if (sneak) {

			if (isFlying) displayText += "[Descending]  ";
			else if (isRiding) displayText += "[Dismounting]  ";
			else if (isHoldingSneak) displayText += "[Sneaking (Key Held)]  ";
			else displayText += "[Sneaking (Toggled)]  ";

		} else if (sprint && !isFlying && !isRiding) {

			if (isHoldingSprint) displayText += "[Sprinting (Key Held)]";
			else displayText += "[Sprinting (Toggled)]";
		}
		
		return displayText.trim();
	}

	private boolean isKeyDown(net.minecraft.client.settings.KeyBinding keyBinding) {
		return GameSettings.isKeyDown(keyBinding);
	}

	private boolean isRiding(EntityPlayer player) {
		return ((Entity) player).ridingEntity != null;
	}

	private boolean isOnGround(EntityPlayer player) {
		return ((Entity) player).onGround;
	}

	private boolean isPotionActive(EntityPlayer player, Potion potion) {
		return ((EntityLiving) player).isPotionActive(potion);
	}

	private void setSprinting(EntityPlayer player, boolean sprinting) {
		((Entity) player).setSprinting(sprinting);
	}

	private void addMotionY(EntityPlayer player, double amount) {
		((Entity) player).motionY += amount;
	}
}
