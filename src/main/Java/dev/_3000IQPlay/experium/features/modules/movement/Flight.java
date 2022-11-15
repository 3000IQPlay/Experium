package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.BlockCollisionBoundingBoxEvent;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.DamageUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.MovementUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight
        extends Module {
    private static Flight INSTANCE = new Flight();
    public Setting<FlyMode> flyMode = this.register(new Setting<FlyMode>("FlyType", FlyMode.Motion));
	public Setting<Boolean> glide = this.register(new Setting<Boolean>("Glide", false, v -> this.flyMode.getValue() == FlyMode.Vanilla || this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> glideSpeed = this.register(new Setting<Float>("GlideSpeed", 0.315f, 0.1f, 2.0f, v -> this.glide.getValue() && this.flyMode.getValue() == FlyMode.Vanilla || this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Boolean> reDamage = this.register(new Setting<Boolean>("ReDamage", true, v -> this.flyMode.getValue() == FlyMode.VerusBoost));
	public Setting<Float> vbSpeed = this.register(new Setting<Float>("VerusBoostSpeed", 1.5f, 0.2f, 5.0f, v -> this.flyMode.getValue() == FlyMode.VerusBoost));
	public Setting<Float> mHorizontalSpeed = this.register(new Setting<Float>("MotionHorizSpeed", 0.5f, 0.2f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> mVerticalSpeed = this.register(new Setting<Float>("MotionVertSpeed", 0.42f, 0.1f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> vanillaSpeed = this.register(new Setting<Float>("VanillaHorizSpeed", 0.5f, 0.2f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> verticalSpeed = this.register(new Setting<Float>("VanillaVertSpeed", 1.0f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> keepAlive = this.register(new Setting<Boolean>("KeepAlive", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> noClip = this.register(new Setting<Boolean>("NoClip", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> spoofGround = this.register(new Setting<Boolean>("SpoofGround", false, v -> this.flyMode.getValue() == FlyMode.Vanilla || this.flyMode.getValue() == FlyMode.Motion || this.flyMode.getValue() == FlyMode.Creative));
	public Setting<Boolean> vanillaKickBypass = this.register(new Setting<Boolean>("VanillaKickBypass", true, v -> this.flyMode.getValue() == FlyMode.Vanilla || this.flyMode.getValue() == FlyMode.Motion || this.flyMode.getValue() == FlyMode.Creative));
	public Setting<Boolean> damage = this.register(new Setting<Boolean>("Damage", true));
	public Setting<Float> plusYPos = this.register(new Setting<Float>("PlusYPos", 3.42f, 2.0f, 10.0f, v -> this.damage.getValue()));
	public Setting<Integer> damagePackets = this.register(new Setting<Integer>("PacketAmount", 1, 1, 3, v -> this.damage.getValue()));
	public Setting<Boolean> groundCheck = this.register(new Setting<Boolean>("GroundCheck", true, v -> this.damage.getValue()));
	public Setting<Boolean> hurtTimeCheck = this.register(new Setting<Boolean>("HurtTimeCheck", false, v -> this.damage.getValue()));
	private final Timer groundTimer = new Timer();
	private boolean flyable;
	private int ticks = 0;
	private double y;
	private int offGroundTicks, onGroundTicks = -1;

    public Flight() {
        super("Flight", "Makes you fly.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }
	
	public static Flight getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Flight();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
	
	@Override
	public void onEnable() {
		if (Flight.fullNullCheck()) {
			return;
		}
		this.ticks = 1;
		/*
		if (this.damage.getValue().booleanValue()) {
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.getEntityBoundingBox().minY + 3.5, Flight.mc.player.posZ, false));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.getEntityBoundingBox().minY, Flight.mc.player.posZ, false));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.getEntityBoundingBox().minY, Flight.mc.player.posZ, true));
        }
		*/
		if (this.damage.getValue().booleanValue()) {
			DamageUtil.damagePlayer(DamageUtil.DamageType.POSITION, 3.42f, 1, this.groundCheck.getValue().booleanValue(), this.hurtTimeCheck.getValue().booleanValue());
		}
    }
	
	public static double roundToOnGround(final double posY) {
        return posY - (posY % 0.015625);
    }
	
	@Override
	public void onUpdate() {
		if (Flight.mc.player.onGround) {
            this.offGroundTicks = 0;
            ++this.onGroundTicks;
        } else {
            this.onGroundTicks = 0;
            ++this.offGroundTicks;
        }
		if (this.flyMode.getValue() == FlyMode.Motion) {
            Flight.mc.player.capabilities.isFlying = false;
            Flight.mc.player.motionY = 0.0f;
            Flight.mc.player.motionX = 0.0f;
            Flight.mc.player.motionZ = 0.0f;
			if (this.glide.getValue().booleanValue() && !Flight.mc.player.onGround) {
                Flight.mc.player.motionY = -0.0315f;
            }
            if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
                Flight.mc.player.motionY += this.mVerticalSpeed.getValue().floatValue();
			}
            if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
                Flight.mc.player.motionY -= this.mVerticalSpeed.getValue().floatValue();
			}
            EntityUtil.moveEntityStrafe(this.mHorizontalSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
            Flight.getInstance().handleVanillaKickBypass();
		}
		if (this.flyMode.getValue() == FlyMode.ManualAirJump) {
		    Flight.mc.player.onGround = true;
		}
		if (this.flyMode.getValue() == FlyMode.Vanilla) {
			Experium.timerManager.reset();
			if (this.keepAlive.getValue().booleanValue()) {
                Flight.mc.player.connection.sendPacket((Packet)new CPacketKeepAlive());
            }
            if (this.noClip.getValue().booleanValue()) {
                Flight.mc.player.noClip = true;
            } else {
				Flight.mc.player.noClip = false;
			}
			Flight.mc.player.capabilities.isFlying = false;
            Flight.mc.player.motionX = Flight.mc.player.motionZ = Flight.mc.player.motionY = 0.0;
			if (this.glide.getValue().booleanValue() && !Flight.mc.player.onGround) {
                Flight.mc.player.motionY =- this.glideSpeed.getValue().floatValue() / 100;
            }
            if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
				Flight.mc.player.motionY += this.verticalSpeed.getValue().floatValue();
			}
            if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
				Flight.mc.player.motionY -= this.verticalSpeed.getValue().floatValue();
			}
            EntityUtil.moveEntityStrafe(this.vanillaSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
			Flight.getInstance().handleVanillaKickBypass();
		}
		if (this.flyMode.getValue() == FlyMode.Jetpack) {
			Experium.timerManager.reset();
			if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
                Flight.mc.player.motionY += 0.15;
                Flight.mc.player.motionX *= 1.1;
                Flight.mc.player.motionZ *= 1.1;
			}
        }
		if (this.flyMode.getValue() == FlyMode.VerusBoost) {
			if (this.ticks == 1) {
                Flight.mc.player.connection.sendPacket((Packet)new CPacketEntityAction(Flight.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, true));
                Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY + 3.42, Flight.mc.player.posZ, false));
                Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, false));
                Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, true));
                Experium.timerManager.setTimer(0.15f);
                Flight.mc.player.jump();
                Flight.mc.player.onGround = true;
            } else if (this.ticks == 2) {
                Experium.timerManager.reset();
            }
            if (Flight.mc.player.onGround) {
                Flight.mc.player.jump();
            }
            if (Flight.mc.player.fallDistance > 1) {
                Flight.mc.player.motionY = -((Flight.mc.player.posY) - Math.floor(Flight.mc.player.posY));
            }
            if (Flight.mc.player.motionY == 0.0) {
                Flight.mc.player.jump();
                Flight.mc.player.onGround = true;
                Flight.mc.player.fallDistance = 0f;
            }
            if (this.ticks < 25) {
                EntityUtil.moveEntityStrafe(this.vbSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
            } else {
                if (this.ticks == 25){
                    EntityUtil.moveEntityStrafe(0.48f, (Entity)Flight.mc.player);
                }
                if (this.reDamage.getValue().booleanValue()) {
                    this.ticks = 1;
                }
                EntityUtil.moveEntityStrafe(Math.sqrt(Flight.mc.player.motionX * Flight.mc.player.motionX + Flight.mc.player.motionZ * Flight.mc.player.motionZ), (Entity)Flight.mc.player);
            }
			this.ticks++;
        }
		if (this.flyMode.getValue() == FlyMode.Creative) {
            Flight.mc.player.capabilities.isFlying = true;
            Flight.getInstance().handleVanillaKickBypass();
		}
        if (this.flyMode.getValue() == FlyMode.ACR) {
            EntityUtil.moveEntityStrafe(0.06f, (Entity)Flight.mc.player);
            if (Flight.mc.player.ticksExisted % 3 == 0) {
                Flight.mc.player.motionY = 0.40444491418477213;
                this.offGroundTicks = 0;
            }
            if (this.offGroundTicks == 1) {
                EntityUtil.moveEntityStrafe(0.36f, (Entity)Flight.mc.player);
                Flight.mc.player.motionY = 0.33319999363422365;
            }
        }
		if (this.flyMode.getValue() == FlyMode.VerusAirJump) {
            EntityUtil.moveEntityStrafe(Math.sqrt(Flight.mc.player.motionX * Flight.mc.player.motionX + Flight.mc.player.motionZ * Flight.mc.player.motionZ), (Entity)Flight.mc.player);
            if (!Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
                if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (Flight.mc.player.ticksExisted % 2 == 0)
                        Flight.mc.player.motionY = 0.42f;
                } else {
                    if (Flight.mc.player.onGround) {
                        Flight.mc.player.jump();
                    }
                    if (Flight.mc.player.fallDistance > 1) {
                        Flight.mc.player.motionY =- ((Flight.mc.player.posY) - Math.floor(Flight.mc.player.posY));
                    }
                    if (Flight.mc.player.motionY == 0) {
                        Flight.mc.player.jump();
                        Flight.mc.player.onGround = true;
                        Flight.mc.player.fallDistance = 0;
                        Flight.mc.player.onGround = true;
                    }
                }
            }
        }
    }
	
	private void handleVanillaKickBypass() {
        if (!this.vanillaKickBypass.getValue().booleanValue() || !groundTimer.passed(1000L)) return;
        final double ground = calculateGround();
        for (double posY = Flight.mc.player.posY; posY > ground; posY -= 8D) {
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, posY, Flight.mc.player.posZ, true));
            if (posY - 8D < ground) break; // Prevent next step
        }
        Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, ground, Flight.mc.player.posZ, true));
        for (double posY = ground; posY < Flight.mc.player.posY; posY += 8D) {
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, posY, Flight.mc.player.posZ, true));
            if (posY + 8D > Flight.mc.player.posY) break; // Prevent next step
        }
        Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, mc.player.posY, Flight.mc.player.posZ, true));
        groundTimer.reset();
    }
	
	private double calculateGround() {
        final AxisAlignedBB playerBoundingBox = mc.player.getEntityBoundingBox();
        double blockHeight = 1D;
        for (double ground = mc.player.posY; ground > 0D; ground -= blockHeight) {
            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);
            if (mc.world.checkBlockCollision(customBox)) {
                if(blockHeight <= 0.05D) {
                    return ground + blockHeight;
				}
                ground += blockHeight;
                blockHeight = 0.05D;
            }
        }
        return 0F;
    }
	
	@SubscribeEvent
	public void onPacketSend(PacketEvent.Send event) {
		if (this.flyMode.getValue() == FlyMode.Vanilla || this.flyMode.getValue() == FlyMode.Motion || this.flyMode.getValue() == FlyMode.Creative) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (this.spoofGround.getValue().booleanValue()) {
				    Flight.mc.player.onGround = true;
			    }
            }
		}
    }
	
	@Override
	public void onDisable() {
		Experium.timerManager.reset();
		Flight.mc.player.capabilities.isFlying = false;
		Flight.mc.player.noClip = false;
		super.onDisable();
    }
	
	public static enum FlyMode {
		Vanilla,
		Motion,
		VerusBoost,
		VerusAirJump,
		ACR,
		Jetpack,
		Creative,
		ManualAirJump;
	}
}
