package me.drman.litminions.extra;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.InventoryUtil;
import com.massivecraft.massivecore.util.MUtil;
import me.drman.litminions.Config;
import me.drman.litminions.extra.gui.BaseGui;
import me.drman.litminions.extra.gui.IGuiClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EngineGui extends Engine {

    private static final EngineGui i = new EngineGui();
    public static EngineGui get() { return i; }

    private final Map<UUID, Long> clickThrottling = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        BaseGui baseGui = BaseGui.get(inventory);

        if(baseGui == null) return;

        Bukkit.getScheduler().runTask(MassiveCore.get(), () -> baseGui.getGuiOpenTasks().forEach(guiOpen -> guiOpen.onOpen(event)));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        if(MUtil.isntPlayer(event.getWhoClicked())) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        UUID uuid = player.getUniqueId();
        BaseGui baseGui = BaseGui.get(inventory);

        if(baseGui == null) return;

        event.setCancelled(true);
        event.setResult(Event.Result.DENY);

        InventoryAction action = event.getAction();

        long now = System.currentTimeMillis();
        long lastClick = this.clickThrottling.getOrDefault(uuid, 0L);

        if(now - lastClick >= Config.get().guiClickThrottleDelayMs) {
            if(action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                this.clickThrottling.put(uuid, now);
            }
        } else {
            return;
        }

        if (InventoryUtil.isBottomInventory(event)) {
            boolean isAllowed = baseGui.isAllowBottomGuiClick();

            if(isAllowed) {
                event.setCancelled(false);
                event.setResult(Event.Result.DEFAULT);
            }

            boolean hasBottomClick = !baseGui.getGuiBottomClick().isEmpty();

            if(hasBottomClick) {
                baseGui.getGuiBottomClick().forEach(guiClick -> guiClick.onClick(event));
            }

            if(!hasBottomClick && !isAllowed) {
                MixinMessage.get().msgOne(player, Config.get().msgGuiLeaveWindowToEdit);
            }

            return;
        }

        int slot = event.getSlot();
        IGuiClick clickable = baseGui.getClickable(slot);

        if(clickable != null) {
            clickable.onClick(event);
            return;
        }

        if(InventoryUtil.isTopInventory(event)) {
            boolean hasTopClick = !baseGui.getGuiTopClick().isEmpty();

            if(hasTopClick) {
                baseGui.getGuiTopClick().forEach(guiClick -> guiClick.onClick(event));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        BaseGui baseGui = BaseGui.get(inventory);

        if(baseGui == null) return;

        Bukkit.getScheduler().runTask(MassiveCore.get(), () -> baseGui.getGuiCloseTasks().forEach(guiClose -> guiClose.onClose(event)));
    }

}
