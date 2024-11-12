package me.drman.litminions.extra.gui.types;

import me.drman.litminions.extra.gui.configuration.Clickable;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public interface IClickableGui {

    List<Clickable> getClickables();

    default List<Clickable> getClickablesFiltered(Inventory inventory) {
        return getClickables().stream()
                .filter(clickable -> clickable.getSlot() >= 0 && clickable.getSlot() < inventory.getSize())
                .collect(Collectors.toList());
    }

    void loadClicks();

}
