package dev._3000IQPlay.experium.features.gui;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.gui.components.Component;
import dev._3000IQPlay.experium.features.gui.components.items.DescriptionDisplay;
import dev._3000IQPlay.experium.features.gui.components.items.Item;
import dev._3000IQPlay.experium.features.gui.components.items.buttons.ModuleButton;
import dev._3000IQPlay.experium.features.gui.particle.ParticleSystem;
import dev._3000IQPlay.experium.features.gui.particle.Snow;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.ClickGui;
import dev._3000IQPlay.experium.features.modules.client.Colors;
import dev._3000IQPlay.experium.util.RenderUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class ExperiumGui
        extends GuiScreen {
    private static ExperiumGui experiumGui;
    private static ExperiumGui INSTANCE;
    private static DescriptionDisplay descriptionDisplay;
    private final ArrayList<Component> components = new ArrayList();
    public ParticleSystem particleSystem;
    private ArrayList<Snow> _snowList = new ArrayList();
    private ExperiumGui ClickGuiMod;

    public ExperiumGui() {
        this.setInstance();
        this.load();
    }

    public static ExperiumGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExperiumGui();
        }
        return INSTANCE;
    }

    public static ExperiumGui getClickGui() {
        return ExperiumGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -109;
        Random random = new Random();
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                this._snowList.add(snow);
            }
        }
        for (final Module.Category category : Experium.moduleManager.getCategories()) {
            this.components.add(new Component(category.getName(), x += 115, 4, true){

                @Override
                public void setupItems() {
                    Experium.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton((Module)module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort((item1, item2) -> item1.getName().compareTo(item2.getName())));
    }

    public void updateModule(Module module) {
        block0: for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton)item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
                continue block0;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ClickGui clickGui = ClickGui.getInstance();
        if (ClickGui.getInstance().guiBackground.getValue().booleanValue()) {
			RenderUtil.drawRect(0.0f, 0.0f, this.width, this.height, ClickGui.getInstance().colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor().getRGB() : new Color(ClickGui.getInstance().gbC.getValue().getRed(), ClickGui.getInstance().gbC.getValue().getGreen(), ClickGui.getInstance().gbC.getValue().getBlue(), ClickGui.getInstance().gbC.getValue().getAlpha()).getRGB());
        }
        descriptionDisplay.setDraw(false);
        this.checkMouseWheel();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        ScaledResolution sr = new ScaledResolution(this.mc);
        if (ClickGui.getInstance().gradiant.getValue().booleanValue()) {
            this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, ClickGui.getInstance().colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor().getRGB() : new Color(ClickGui.getInstance().gradiantC.getValue().getRed(), ClickGui.getInstance().gradiantC.getValue().getGreen(), ClickGui.getInstance().gradiantC.getValue().getBlue(), ClickGui.getInstance().gradiantC.getValue().getAlpha() / 2).getRGB());
        }
        if (descriptionDisplay.shouldDraw() && clickGui.moduleDescription.getValue().booleanValue()) {
            descriptionDisplay.drawScreen(mouseX, mouseY, partialTicks);
        }
        ScaledResolution res = new ScaledResolution(this.mc);
        if (!this._snowList.isEmpty() && ClickGui.getInstance().snowing.getValue().booleanValue()) {
            this._snowList.forEach(snow -> snow.Update(res));
        }
        if (this.particleSystem != null && ClickGui.getInstance().particles.getValue().booleanValue()) {
            this.particleSystem.render(mouseX, mouseY);
        } else {
            this.particleSystem = new ParticleSystem(new ScaledResolution(this.mc));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if (ClickGui.getInstance().scroll.getValue().booleanValue()) {
                this.components.forEach(component -> component.setY(component.getY() - ClickGui.getInstance().scrollval.getValue()));
            }
        } else if (dWheel > 0 && ClickGui.getInstance().scroll.getValue().booleanValue()) {
            this.components.forEach(component -> component.setY(component.getY() + ClickGui.getInstance().scrollval.getValue()));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public DescriptionDisplay getDescriptionDisplay() {
        return descriptionDisplay;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    public void updateScreen() {
        if (this.particleSystem != null) {
            this.particleSystem.update();
        }
    }

    static {
        INSTANCE = new ExperiumGui();
        descriptionDisplay = new DescriptionDisplay("", 0.0f, 0.0f);
    }
}
