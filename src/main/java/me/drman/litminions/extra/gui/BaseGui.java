package me.drman.litminions.extra.gui;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.mixin.MixinMessage;
import me.drman.litminions.Config;
import me.drman.litminions.extra.gui.configuration.GuiDesign;
import me.drman.litminions.extra.gui.types.IClickableGui;
import me.drman.litminions.extra.gui.types.RefreshGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseGui {

    protected static final Map<Inventory, BaseGui> INVENTORY_TO_GUI = new HashMap<>();

    private final Map<Integer, IGuiClick> clickableSlots = new HashMap<>();
    private final List<IGuiClose> guiCloseTasks = new ArrayList<>();
    private final List<IGuiOpen> guiOpenTasks = new ArrayList<>();
    private final List<IGuiClick> guiTopClick = new ArrayList<>(), guiBottomClick = new ArrayList<>();

    private final GuiDesign guiDesign;

    protected final Player player;
    protected Inventory inventory;

    public BaseGui(Player player, GuiDesign guiDesign) {
        this.player = player;
        this.guiDesign = guiDesign;

        addGuiCloseTask(event -> INVENTORY_TO_GUI.remove(this.inventory));
    }

    public BaseGui(GuiDesign guiDesign) {
        this.player = null;
        this.guiDesign = guiDesign;
    }

    protected void preloadGui() {}

    protected void loadGui() {
        if(this.inventory == null) {
            MixinMessage.get().msgOne(this.player, Config.get().msgGuiInventoryNotSet);
            return;
        }

        INVENTORY_TO_GUI.put(this.inventory, this);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean isAllowBottomGuiClick() {
        return false;
    }

    public List<String> getReplacements() { return new ArrayList<>(); }

    public List<IGuiClose> getGuiCloseTasks() {
        return this.guiCloseTasks;
    }
    public List<IGuiOpen> getGuiOpenTasks() {
        return this.guiOpenTasks;
    }
    public List<IGuiClick> getGuiTopClick() {
        return this.guiTopClick;
    }
    public List<IGuiClick> getGuiBottomClick() {
        return this.guiBottomClick;
    }

    public void addGuiCloseTask(IGuiClose guiClose) {
        this.guiCloseTasks.add(guiClose);
    }
    public void addGuiOpenTask(IGuiOpen guiOpen) {
        this.guiOpenTasks.add(guiOpen);
    }
    public void addGuiTopClick(IGuiClick guiClick) {
        this.guiTopClick.add(guiClick);
    }
    public void addGuiBottomClick(IGuiClick guiClick) {
        this.guiBottomClick.add(guiClick);
    }

    public void setClickable(int slot, IGuiClick guiClick) {
        this.clickableSlots.put(slot, guiClick);
    }
    public void clearClickable(int slot) {
        this.clickableSlots.remove(slot);
    }
    public IGuiClick getClickable(int slot) {
        return this.clickableSlots.getOrDefault(slot, null);
    }

    public void buildGui() {
        if(this.guiDesign == null) {
            if(this.player != null) MixinMessage.get().msgOne(this.player, Config.get().msgGuiDesignNotSet);
            return;
        }

        this.inventory = this.guiDesign.build(this.player, getReplacements().toArray(new String[0]));

        preloadGui();

        if(this instanceof IClickableGui) {
            IClickableGui clickableGui = (IClickableGui) this;

            clickableGui.loadClicks();
        }

        loadGui();

        if(this instanceof RefreshGui) {
            RefreshGui refreshGui = (RefreshGui) this;

            refreshGui.refresh();
        }
    }

    public void open() {
        openDelayed(0L);
    }
    public void openDelayed(long delayTick) {
        Bukkit.getScheduler().runTaskLater(MassiveCore.get(), () -> {

            buildGui();

            if(this.player != null) {
                this.player.openInventory(this.inventory);
            }
        }, delayTick);
    }

    public static BaseGui get(Inventory inventory) {
        return INVENTORY_TO_GUI.getOrDefault(inventory, null);
    }

}
