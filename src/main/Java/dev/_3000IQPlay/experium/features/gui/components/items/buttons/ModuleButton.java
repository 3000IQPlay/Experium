package dev._3000IQPlay.experium.features.gui.components.items.buttons;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.gui.ExperiumGui;
import dev._3000IQPlay.experium.features.gui.components.items.DescriptionDisplay;
import dev._3000IQPlay.experium.features.gui.components.items.Item;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.BindButton;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.BooleanButton;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.Button;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.EnumButton;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.Slider;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.StringButton;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.UnlimitedSlider;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.Util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

public class ModuleButton
        extends Button {
    private final Module module;
    private final ResourceLocation logo = new ResourceLocation("textures/gear.png");
    private List<Item> items = new ArrayList<Item>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)0.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)width, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)width, (float)0.0f, (float)0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<Item>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }
                if (setting.getValue() instanceof Bind && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton(setting));
                }
                if (setting.getValue() instanceof String || setting.getValue() instanceof Character) {
                    newItems.add(new StringButton(setting));
                }
                if (setting.isNumberSetting()) {
                    if (setting.hasRestriction()) {
                        newItems.add(new Slider(setting));
                        continue;
                    }
                    newItems.add(new UnlimitedSlider(setting));
                }
                if (!setting.isEnumSetting()) continue;
                newItems.add(new EnumButton(setting));
            }
        }
        this.items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            ClickGui gui = Experium.moduleManager.getModuleByClass(ClickGui.class);
            Experium.textManager.drawStringWithShadow(gui.openCloseChange.getValue().booleanValue() ? (this.subOpen ? gui.close.getValue() : gui.open.getValue()) : gui.moduleButton.getValue(), this.x - 1.5f + (float)this.width - 7.4f, this.y - 2.0f - (float)ExperiumGui.getClickGui().getTextOffset(), -1);
            if (this.subOpen) {
                float height = 1.0f;
                for (Item item : this.items) {
                    if (!item.isHidden()) {
                        item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                        item.setHeight(15);
                        item.setWidth(this.width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }
                    item.update();
                }
            }
        }
        if (ClickGui.getInstance().gear.getValue().booleanValue()) {
            mc.getTextureManager().bindTexture(this.logo);
            ModuleButton.drawCompleteImage(this.x - 1.5f + (float)this.width - 7.4f, this.y - 2.2f - (float)ExperiumGui.getClickGui().getTextOffset(), 9, 9);
        }
        if (this.isHovering(mouseX, mouseY)) {
            DescriptionDisplay descriptionDisplay = ExperiumGui.getInstance().getDescriptionDisplay();
            descriptionDisplay.setDescription(this.module.getDescription());
            descriptionDisplay.setLocation(mouseX + 2, mouseY + 1);
            descriptionDisplay.setDraw(true);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            }
            if (this.subOpen) {
                for (Item item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = ClickGui.getInstance().moduleButtonHeight.getValue().intValue();
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return ClickGui.getInstance().moduleButtonHeight.getValue().intValue();
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}
