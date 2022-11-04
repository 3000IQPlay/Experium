package dev._3000IQPlay.experium.features.modules.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import dev._3000IQPlay.experium.features.modules.Module;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

public class Capes
extends Module {
    public static final ResourceLocation THREEVT_CAPE = new ResourceLocation("textures/3vt2.png");
    public static final ResourceLocation ZBOB_CAPE = new ResourceLocation("textures/zb0b.png");
    public static final ResourceLocation OHARE_CAPE = new ResourceLocation("textures/ohare.png");
    public static final ResourceLocation SQUID_CAPE = new ResourceLocation("textures/squidcape.png");
    public static Map<String, String[]> UUIDs = new HashMap<String, String[]>();
    private static Capes instance;

    public Capes() {
        super("Capes", "Renders the client's capes.", Module.Category.CLIENT, false, false, false);
UUIDs.put("Brotherismad", new String[]{"647e7cd0-fb35-4fdc-b413-ed53bc34b082"});
UUIDS.put("MrBubblegum123", new String[]{"a6a6de0e-e0f9-418f-8ea7-373a2ec87aa3"});
UUIDS.put("_3000IQPlay", new String[]{"5d0d3c96-bd09-4490-9cf3-a718d250e4ca"});
UUIDS.put("PlsDontStealname", new String[]{"0b9b9bf4-bb0e-43ae-a703-c987b4eb52d9"});        
        instance = this;
    }

    public static Capes getInstance() {
        if (instance == null) {
            instance = new Capes();
        }
        return instance;
    }

    public static ResourceLocation getCapeResource(AbstractClientPlayer player) {
        for (String name : UUIDs.keySet()) {
            for (String uuid : UUIDs.get(name)) {
                if (name.equalsIgnoreCase("3vt") && player.getUniqueID().toString().equals(uuid)) {
                    return THREEVT_CAPE;
                }
                if (name.equalsIgnoreCase("Megyn") && player.getUniqueID().toString().equals(uuid)) {
                    return THREEVT_CAPE;
                }
                if (!name.equalsIgnoreCase("oHare") || !player.getUniqueID().toString().equals(uuid)) continue;
                return OHARE_CAPE;
            }
        }
        return null;
    }

    public static boolean hasCape(UUID uuid) {
        Iterator<String> iterator = UUIDs.keySet().iterator();
        if (iterator.hasNext()) {
            String name = iterator.next();
            return Arrays.asList((Object[])UUIDs.get(name)).contains(uuid.toString());
        }
        return false;
    }
}
