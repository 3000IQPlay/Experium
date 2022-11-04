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