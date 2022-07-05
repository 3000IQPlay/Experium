package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiSpam
        extends Module {
    public Setting<Boolean> Badwords = this.register(new Setting<Boolean>("BadWords", true));
    public Setting<Boolean> Links = this.register(new Setting<Boolean>("Links", true));
    public Setting<Boolean> Shop = this.register(new Setting<Boolean>("Shop", true));
    public Setting<Boolean> Kit = this.register(new Setting<Boolean>("Kit", true));
    public Setting<Boolean> Nig = this.register(new Setting<Boolean>("Racism", true));
    public Setting<Boolean> Discord = this.register(new Setting<Boolean>("Discord", true));
    public Setting<Boolean> YouTube = this.register(new Setting<Boolean>("YouTube", true));

    public AntiSpam() {
        super("AntiSpam", "Prevents you from seeing 8yo kids in chat", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onClientChatReceive(ClientChatReceivedEvent e) {
        if (this.Links.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("http")) {
            e.setCanceled(true);
        }
        if (this.Shop.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("Shop") || e.getMessage().getUnformattedText().contains("shop") || e.getMessage().getUnformattedText().contains("Sh0p") || e.getMessage().getUnformattedText().contains("sh0p") || e.getMessage().getUnformattedText().contains("Buy") || e.getMessage().getUnformattedText().contains("buy") || e.getMessage().getUnformattedText().contains("sell") || e.getMessage().getUnformattedText().contains("Sell") || e.getMessage().getUnformattedText().contains("\u0448\u043e\u043f") || e.getMessage().getUnformattedText().contains("\u0428\u043e\u043f") || e.getMessage().getUnformattedText().contains("\u043c\u0430\u0433") || e.getMessage().getUnformattedText().contains("\u0444\u0435\u0434") || e.getMessage().getUnformattedText().contains("\u0431\u0435\u0441")) {
            e.setCanceled(true);
        }
        if (this.Kit.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("Kit") || e.getMessage().getUnformattedText().contains("kits") || e.getMessage().getUnformattedText().contains("\u043a\u0438\u0442") || e.getMessage().getUnformattedText().contains("\u041a\u0438\u0442") || e.getMessage().getUnformattedText().contains("\u041a1\u0442") || e.getMessage().getUnformattedText().contains("\u043a1\u0442")) {
            e.setCanceled(true);
        }
        if (this.Nig.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("Nig") || e.getMessage().getUnformattedText().contains("nig") || e.getMessage().getUnformattedText().contains("n1g") || e.getMessage().getUnformattedText().contains("Nig") || e.getMessage().getUnformattedText().contains("N1g") || e.getMessage().getUnformattedText().contains("\u041d\u0435\u0433") || e.getMessage().getUnformattedText().contains("\u043d\u0435\u0433")) {
            e.setCanceled(true);
        }
        if (this.Discord.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("disc") || e.getMessage().getUnformattedText().contains("Disc") || e.getMessage().getUnformattedText().contains("#") || e.getMessage().getUnformattedText().contains("\u0434\u0438\u0441") || e.getMessage().getUnformattedText().contains("\u0414\u0438\u0441")) {
            e.setCanceled(true);
        }
        if (this.Badwords.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("fag") || e.getMessage().getUnformattedText().contains("ped") || e.getMessage().getUnformattedText().contains("dum") || e.getMessage().getUnformattedText().contains("ass") || e.getMessage().getUnformattedText().contains("fat") || e.getMessage().getUnformattedText().contains("bitch") || e.getMessage().getUnformattedText().contains("Bitch") || e.getMessage().getUnformattedText().contains("fuc") || e.getMessage().getUnformattedText().contains("dick") || e.getMessage().getUnformattedText().contains("Ped") || e.getMessage().getUnformattedText().contains("Dum") || e.getMessage().getUnformattedText().contains("Ass") || e.getMessage().getUnformattedText().contains("Fat") || e.getMessage().getUnformattedText().contains("Fuc") || e.getMessage().getUnformattedText().contains("Dick") || e.getMessage().getUnformattedText().contains("\u0445\u0443") || e.getMessage().getUnformattedText().contains("\u043f\u0438\u0437") || e.getMessage().getUnformattedText().contains("\u0431\u043b") || e.getMessage().getUnformattedText().contains("\u043c\u043e\u0447\u0430") || e.getMessage().getUnformattedText().contains("\u0448\u043b\u044e") || e.getMessage().getUnformattedText().contains("\u0445\u0443") || e.getMessage().getUnformattedText().contains("\u0435\u0431") || e.getMessage().getUnformattedText().contains("\u0451\u0431") || e.getMessage().getUnformattedText().contains("\u0432\u043f\u0438\u0437") || e.getMessage().getUnformattedText().contains("\u0436\u043e\u043f") || e.getMessage().getUnformattedText().contains("\u0432\u044b\u0431") || e.getMessage().getUnformattedText().contains("\u0434\u0435\u0440\u044c\u043c\u043e") || e.getMessage().getUnformattedText().contains("\u0441\u044b\u043d") || e.getMessage().getUnformattedText().contains("\u043c\u0430\u043c") || e.getMessage().getUnformattedText().contains("\u0434\u043e\u0435") || e.getMessage().getUnformattedText().contains("\u0436\u0438\u0440") || e.getMessage().getUnformattedText().contains("\u0436\u0438\u0434") || e.getMessage().getUnformattedText().contains("\u0437\u0430\u0435") || e.getMessage().getUnformattedText().contains("\u043d\u0430\u0435") || e.getMessage().getUnformattedText().contains("\u0443\u0435\u0431") || e.getMessage().getUnformattedText().contains("\u0443\u0451\u0431") || e.getMessage().getUnformattedText().contains("\u0425\u0443") || e.getMessage().getUnformattedText().contains("\u041f\u0438\u0437") || e.getMessage().getUnformattedText().contains("\u0411\u043b") || e.getMessage().getUnformattedText().contains("\u041c\u043e\u0447\u0430") || e.getMessage().getUnformattedText().contains("\u0428\u043b\u044e") || e.getMessage().getUnformattedText().contains("\u0425\u0443") || e.getMessage().getUnformattedText().contains("\u0415\u0431") || e.getMessage().getUnformattedText().contains("\u0401\u0431") || e.getMessage().getUnformattedText().contains("\u0412\u043f\u0438\u0437") || e.getMessage().getUnformattedText().contains("\u0416\u043e\u043f") || e.getMessage().getUnformattedText().contains("\u0412\u044b\u0431") || e.getMessage().getUnformattedText().contains("\u0414\u0435\u0440\u044c\u043c\u043e") || e.getMessage().getUnformattedText().contains("\u0421\u044b\u043d") || e.getMessage().getUnformattedText().contains("\u041c\u0430\u043c") || e.getMessage().getUnformattedText().contains("\u0414\u043e\u0435") || e.getMessage().getUnformattedText().contains("\u0416\u0438\u0440") || e.getMessage().getUnformattedText().contains("\u0416\u0438\u0434") || e.getMessage().getUnformattedText().contains("\u0417\u0430\u0435") || e.getMessage().getUnformattedText().contains("\u041d\u0430\u0435") || e.getMessage().getUnformattedText().contains("\u0423\u0435\u0431") || e.getMessage().getUnformattedText().contains("\u0423\u0451\u0431") || e.getMessage().getUnformattedText().contains("\u0410\u043d\u0430") || e.getMessage().getUnformattedText().contains("\u0430\u043d\u0430") || e.getMessage().getUnformattedText().contains("\u041e\u0447\u043a") || e.getMessage().getUnformattedText().contains("\u043e\u0447\u043a") || e.getMessage().getUnformattedText().contains("\u0421\u0443") || e.getMessage().getUnformattedText().contains("\u0441\u0443") || e.getMessage().getUnformattedText().contains("\u0445\u0443")) {
            e.setCanceled(true);
        }
        if (this.YouTube.getValue().booleanValue() && e.getMessage().getUnformattedText().contains("yt") || e.getMessage().getUnformattedText().contains("tub") || e.getMessage().getUnformattedText().contains("ash") || e.getMessage().getUnformattedText().contains("\u0437\u0430\u0445\u043e\u0434\u0438") || e.getMessage().getUnformattedText().contains("join") || e.getMessage().getUnformattedText().contains("ash") || e.getMessage().getUnformattedText().contains("video") || e.getMessage().getUnformattedText().contains("\u044e\u0442") || e.getMessage().getUnformattedText().contains("\u043a\u0430\u043d\u0430\u043b") || e.getMessage().getUnformattedText().contains("chan")) {
            e.setCanceled(true);
        }
    }
}