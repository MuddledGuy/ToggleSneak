package host.leak.togglesneak;

import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;

public class MovementInputModded extends MovementInput {

	private final GameSettings gameSettings;
	public boolean sprint;
	private ToggleSneak ITS;
	private Minecraft mc;
	private int sneakWasPressed;
	private int sprintWasPressed;
	private EntityPlayerSP player;
	private float originalFlySpeed = -1.0F;
	private float boostedFlySpeed;

	public MovementInputModded(GameSettings gameSettings, ToggleSneak ITS) {
		this.gameSettings = gameSettings;
		this.sprint = false;
		this.ITS = ITS;
		this.mc = Minecraft.getMinecraft(); // we'll need replace the static ref by a link passed as parameter
		this.sneakWasPressed = 0;
		this.sprintWasPressed = 0;
	}

	public void updatePlayerMoveState() {
		
		player = mc.player;
		moveStrafe = 0.0F;
		moveForward = 0.0F;

		if (this.forwardKeyDown = gameSettings.keyBindForward.isKeyDown()) moveForward++;
		if (this.backKeyDown = gameSettings.keyBindBack.isKeyDown()) moveForward--;
		if (this.leftKeyDown = gameSettings.keyBindLeft.isKeyDown()) moveStrafe++;
		if (this.rightKeyDown = gameSettings.keyBindRight.isKeyDown()) moveStrafe--;

		jump = gameSettings.keyBindJump.isKeyDown();
		
		if (ITS.toggleSneak) {
			if (gameSettings.keyBindSneak.isKeyDown()) {
				if (sneakWasPressed == 0) {
					if (sneak) {
						sneakWasPressed = -1;
					} else if (player.isRiding() || player.capabilities.isFlying) {
						sneakWasPressed = ITS.keyHoldTicks + 1;
					} else {
						sneakWasPressed = 1;
					}
					sneak = !sneak;
				} else if (sneakWasPressed > 0){
					sneakWasPressed++;
				}
			} else {
				if ((ITS.keyHoldTicks > 0) && (sneakWasPressed > ITS.keyHoldTicks)) sneak = false;
				sneakWasPressed = 0;
			}
		} else {
			sneak = gameSettings.keyBindSneak.isKeyDown();
		}
		
		if (sneak) {
			moveStrafe *= 0.3F;
			moveForward *= 0.3F;
		}
		
		if (ITS.toggleSprint) {
			if (gameSettings.keyBindSprint.isKeyDown()) {
				if (sprintWasPressed == 0) {
					if (sprint) {
						sprintWasPressed = -1;
					} else if (player.capabilities.isFlying) {
						sprintWasPressed = ITS.keyHoldTicks + 1;
					} else {
						sprintWasPressed = 1;
					}
					sprint = !sprint;
				} else if (sprintWasPressed > 0){
					sprintWasPressed++;
				}
			} else {
				if ((ITS.keyHoldTicks > 0) && (sprintWasPressed > ITS.keyHoldTicks)) sprint = false;
				sprintWasPressed = 0;
			}
		} else sprint = false;
		
		// sprint conditions same as in net.minecraft.client.entity.EntityPlayerSP.onLivingUpdate()
		// check for hungry or flying. But nvm, if conditions not met, sprint will 
		// be canceled there afterwards anyways 
		if (sprint && moveForward == 1.0F && player.onGround && !player.isHandActive()
				&& !player.isPotionActive(MobEffects.BLINDNESS)) player.setSprinting(true);
		
		if (ITS.flyBoost && player.capabilities.isCreativeMode && player.capabilities.isFlying 
				&& (mc.getRenderViewEntity() == player) && sprint) {
			
			if (originalFlySpeed < 0.0F || this.player.capabilities.getFlySpeed() != boostedFlySpeed)
				originalFlySpeed = this.player.capabilities.getFlySpeed();
			boostedFlySpeed = originalFlySpeed * ITS.flyBoostFactor;
			player.capabilities.setFlySpeed(boostedFlySpeed);
			
			if (sneak) player.motionY -= 0.15D * (double)(ITS.flyBoostFactor - 1.0F);
			if (jump) player.motionY += 0.15D * (double)(ITS.flyBoostFactor - 1.0F);
				
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
		boolean isFlying = mc.player.capabilities.isFlying;
		boolean isRiding = mc.player.isRiding();
		boolean isHoldingSneak = gameSettings.keyBindSneak.isKeyDown();
		boolean isHoldingSprint = gameSettings.keyBindSprint.isKeyDown();
		
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
}
