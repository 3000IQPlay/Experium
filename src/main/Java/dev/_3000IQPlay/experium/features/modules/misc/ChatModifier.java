package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.Managers;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.TextUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModifier
        extends Module {
    private static ChatModifier INSTANCE = new ChatModifier();
    private final Timer timer = new Timer();
    public Setting<Suffix> suffix = this.register(new Setting<Suffix>("Suffix", Suffix.NONE, "Your Suffix."));
	public Setting<TextColor> textcolor = this.register(new Setting<TextColor>("TextColor", TextColor.NONE, "Your text color."));
	public Setting<StiTextColor> stiTextColor = this.register(new Setting<StiTextColor>("StiTextColor", StiTextColor.NONE, "Your sti text color."));
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("CleanChat", Boolean.valueOf(false), "Cleans your chat"));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("Infinite", Boolean.valueOf(false), "Makes your chat infinite."));
    public Setting<TextUtil.Color> timeStamps = this.register(new Setting<TextUtil.Color>("Time", TextUtil.Color.NONE));
    public Setting<Boolean> rainbowTimeStamps = this.register(new Setting<Object>("RainbowTimeStamps", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<TextUtil.Color> bracket = this.register(new Setting<Object>("Bracket", TextUtil.Color.WHITE, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> space = this.register(new Setting<Object>("Space", Boolean.valueOf(true), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> all = this.register(new Setting<Object>("All", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));

    public ChatModifier() {
        super("ChatModifier", "Modifies your chat", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/") || s.startsWith("!")) {
                return;
            }
            switch (this.suffix.getValue()) {
                case EXPERIUM: {
                    s = s + " | Experium";
                    break;
                }
            }
			switch (this.stiTextColor.getValue()) {
				case RANDOM: {
                    int minNum = 1;
                    int maxNum = 16;
                    int randomNumber = (int)Math.floor(Math.random()*(maxNum-minNum+1)+minNum);
                    if (randomNumber == 1) {
						s = "!2" + s;
					}
                    if (randomNumber == 2) {
						s = "!3" + s;
					}
                    if (randomNumber == 3) {
						s = "!4" + s;
					}
                    if (randomNumber == 4) {
						s = "!5" + s;
					}
                    if (randomNumber == 5) {
						s = "!7" + s;
					}
                    if (randomNumber == 6) {
						s = "!8" + s;
					}
                    if (randomNumber == 7) {
						s = "!9" + s;
					}
                    if (randomNumber == 8) {
						s = "!g" + s;
					}
                    if (randomNumber == 9) {
						s = "!c" + s;
					}
                    if (randomNumber == 10) {
						s = "!d" + s;
					}
                    if (randomNumber == 11) {
						s = "!a" + s;
					}
                    if (randomNumber == 12) {
						s = "!e" + s;
						}
                    if (randomNumber == 13) {
						s = "!b" + s;
					}
                    if (randomNumber == 14) {
						s = "!f" + s;
					}
                    if (randomNumber == 15) {
						s = "!1" + s;
					}
                    if (randomNumber == 16) {
						s = "!6" + s;
					}
                    break;
				}	
                case LIGHT_GREEN: {
                    s = "!2" + s;
                    break;
                }
				case LIGHT_BLUE: {
                    s = "!3" + s;
                    break;
                }
				case LIGHT_RED: {
                    s = "!4" + s;
                }
				case LIGHT_AQUA: {
                    s = "!5" + s;
                    break;
                }
				case YELLOW: {
                    s = "!7" + s;
					break;
                }
				case LIGHT_GRAY: {
					s = "!8" + s;
					break;
                }
				case LIGHT_PURPLE: {
					s = "!g" + s;
					break;
                }
				case GRAY: {
					s = "!c" + s;
					break;
                }
				case BLACK: {
					s = "!9" + s;
					break;
                }
				case BLUE: {
					s = "!d" + s;
					break;
                }
				case GREEN: {
					s = "!a" + s;
					break;
                }
				case AQUA: {
					s = "!e" + s;
					break;
                }
				case RED: {
					s = "!b" + s;
					break;
                }
				case PURPLE: {
					s = "!f" + s;
					break;
                }
				case GOLD: {
                    s = "!6" + s;
					break;
                }
				case WHITE: {
                    s = "!1" + s;
                    break;
                }
				case ITALIC: {
					s = "!h" + s;
					break;
                }
				case STRIKE: {
					s = "!k" + s;
					break;
                }
				case UNDERLINE: {
					s = "!i" + s;
					break;
                }
				case BOLD: {
					s = "!j" + s;
					break;
                }
            }
			switch (this.textcolor.getValue()) {
                case GREEN: {
                    s = "> " + s;
                    break;
                }
				case YELLOW: {
                    s = "# " + s;
                    break;
                }
				case GOLD: {
                    s = "$ " + s;
                }
				case BLUE: {
                    s = "! " + s;
                    break;
                }
				case AQUA: {
                    s = "`` " + s;
					break;
                }
				case PURPLE: {
                    s = "? " + s;
					break;
                }
				case RED: {
					s = "& " + s;
					break;
                }
				case DARKRED: {
					s = "@ " + s;
					break;
                }
				case GRAY: {
					s = ". " + s;
					break;
                }
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public void onChatPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat) {
            // empty if block
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat) event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        if (this.rainbowTimeStamps.getValue().booleanValue()) {
            String timeString = "[" + date + "]" + (this.space.getValue() != false ? " " : "");
            StringBuilder builder = new StringBuilder(timeString);
            builder.insert(0, "\u00a7+");
            if (!message.contains(Managers.getInstance().getRainbowCommandMessage())) {
                builder.append("\u00a7r");
            }
            return builder.toString();
        }
        return (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getValue())) + TextUtil.coloredString(date, this.timeStamps.getValue()) + (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getValue())) + (this.space.getValue() != false ? " " : "") + "\u00a7r";
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        return player.getPosition().equals(new Vec3i(0, 240, 0));
    }

    public enum Suffix {
        NONE,
        EXPERIUM;
    }
	
	public enum TextColor {
        NONE,
        GREEN,
		YELLOW,
		GOLD,
		BLUE,
		AQUA,
		PURPLE,
		RED,
		DARKRED,
		GRAY;
    }
	
	public enum StiTextColor {
        NONE,
		RANDOM,
		GREEN,
        BLUE,
        AQUA,
		YELLOW,
        GOLD,
		RED,
		PURPLE,
        LIGHT_BLUE,
        LIGHT_GREEN,
        LIGHT_AQUA,
        LIGHT_RED,
        LIGHT_PURPLE,
		LIGHT_GRAY,
		GRAY,
		BLACK,
        WHITE,
        BOLD,
        STRIKE,
        UNDERLINE,
        ITALIC;
    }
}