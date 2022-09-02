package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.util.PlayerUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class StashFinder
        extends Module {
    private Timer timer = new Timer();
    private HashMap<Chunk, ArrayList<TileEntity>> map = new HashMap();
    private ArrayList<Chunk> loggedChunks = new ArrayList();
    public Setting<Integer> amount = this.register(new Setting<Integer>("Amount", 15, 1, 100));
	public Setting<Boolean> hoppers = this.register(new Setting<Boolean>("Hoppers", true));
    public Setting<Boolean> shulkers = this.register(new Setting<Boolean>("shulkers", true));
    public Setting<Boolean> dispensers = this.register(new Setting<Boolean>("Dispensers", true));
    public Setting<Boolean> droppers = this.register(new Setting<Boolean>("Droppers", true));
    public Setting<Boolean> chests = this.register(new Setting<Boolean>("Chests", true));
    public Setting<Boolean> windowsAlert = this.register(new Setting<Boolean>("WindowsAlert", true));
    public Setting<Boolean> sound = this.register(new Setting<Boolean>("Sound", true));
    public Setting<Boolean> chatMessage = this.register(new Setting<Boolean>("ChatMessage", true));
    private static final String pathSave = "experium/StashLogger.txt";

    public StashFinder() {
        super("StashFinder", "Ong ong1!1!!1", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onDisable() {
        this.loggedChunks.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (this.timer.passedMs(500L) && StashFinder.mc.player != null && StashFinder.mc.world != null && StashFinder.mc.world.loadedEntityList != null) {
            this.timer.reset();
            this.map.clear();
            for (TileEntity tileEntity : StashFinder.mc.world.loadedTileEntityList) {
                if (!this.isValid(tileEntity)) continue;
                Chunk chunk = StashFinder.mc.world.getChunk(tileEntity.getPos());
                ArrayList<TileEntity> list = new ArrayList<TileEntity>();
                if (this.map.containsKey((Object)chunk)) {
                    list = this.map.get((Object)chunk);
                }
                list.add(tileEntity);
                this.map.put(chunk, list);
            }
            for (Chunk chunk : this.map.keySet()) {
                if (this.map.get((Object)chunk).size() < this.amount.getValue() || this.loggedChunks.contains((Object)chunk)) continue;
                this.loggedChunks.add(chunk);
                this.log(chunk, this.map.get((Object)chunk));
            }
        }
    }

    public void log(Chunk chunk, ArrayList<TileEntity> list) {
        if (list.size() <= 0) {
            return;
        }
        int x = list.get(0).getPos().getX();
        int z = list.get(0).getPos().getZ();
        if (this.chatMessage.getValue().booleanValue()) {
            Command.sendMessage("Stash located with " + list.size() + " on X: " + x + " Z: " + z);
        }
        if (this.sound.getValue().booleanValue()) {
            StashFinder.mc.world.playSound(PlayerUtil.getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100.0f, 18.0f, true);
        }
        if (this.windowsAlert.getValue().booleanValue()) {
            StashFinder.sendWindowsAlert("Found the stem!");
        }
        new Thread(() -> {
            try {
                File file = new File(pathSave);
                if (!file.exists()) {
                    file.createNewFile();
                }
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                LocalDateTime now = LocalDateTime.now();
                bw.write("X: " + x + " Z: " + z + " Found " + list.size() + " container blocks - " + dtf.format(now));
                bw.newLine();
                bw.close();
            }
            catch (Exception e) {
                System.out.println(" - Error logging chunk. StashLogger");
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendWindowsAlert(String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);
            trayIcon.displayMessage("Experium", message, TrayIcon.MessageType.INFO);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean isValid(TileEntity tileEntity) {
        return this.chests.getValue() != false && tileEntity instanceof TileEntityChest || this.droppers.getValue() != false && tileEntity instanceof TileEntityDropper || this.dispensers.getValue() != false && tileEntity instanceof TileEntityDispenser || this.shulkers.getValue() != false && tileEntity instanceof TileEntityShulkerBox || this.hoppers.getValue() != false && tileEntity instanceof TileEntityDropper;
    }
}

