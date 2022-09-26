package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.junit.experimental.categories.Category;

import java.awt.*;
import java.net.URI;

public class PreniumPorn
        extends Module {

    public PreniumPorn(){
        super("Prenium Porn","best", Module.Category.MISC, true, false, false);
    }



    public void onEnable(){
            try {
                Desktop.getDesktop().browse(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
            } catch (Exception ignored) {
            }
        }


    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.disable();
    }
}
