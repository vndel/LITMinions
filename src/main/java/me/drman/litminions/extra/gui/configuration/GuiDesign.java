package me.drman.litminions.extra.gui.configuration;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.InventoryUtil;
import me.drman.litminions.Config;
import me.drman.litminions.extra.ItemStackWrapper;
import me.drman.litminions.extra.TxtUtil;
import me.drman.litminions.extra.gui.IGuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiDesign implements IGuiBuilder {

    private final Map<Character, ItemStackWrapper> items;
    private final List<String> pattern;
    private final String title;

    public GuiDesign(String guiName, Map<Character, ItemStackWrapper> displayMap, List<String> format) {
        this.title = guiName;
        this.items = displayMap;
        this.pattern = format;
    }

    public String getTitle() {
        return this.title;
    }

    public List<Integer> getFilledSlots() {
        List<Integer> slots = new ArrayList<>();

        int slot = 0;

        for(String s : this.pattern) {
            for(char c : s.toCharArray()) {
                ItemStackWrapper itemStackWrapper = this.items.getOrDefault(c, null);

                if(itemStackWrapper == null) {
                    slot += 1;
                    continue;
                }
                if(InventoryUtil.isNothing(itemStackWrapper.getBaseItem())) {
                    slot += 1;
                    continue;
                }

                slots.add(slot);
                slot += 1;
            }
        }

        return slots;
    }

    public void updateGuiDisplay(Inventory inventory, String... replacements) {
        String firstRow = this.pattern.get(0);
        int rowLength = firstRow.length();

        int row = 0;

        for(String line : this.pattern) {
            int index = 0;

            for(char key : line.toCharArray()) {
                int slot = index + row * rowLength;

                index += 1;

                if(slot >= inventory.getSize() || !this.items.containsKey(key)) continue;

                ItemStackWrapper itemStackWrapper = this.items.get(key);

                if(itemStackWrapper == null) continue;

                inventory.setItem(slot, itemStackWrapper.toItemStack(replacements));
            }

            row += 1;
        }
    }

    @Override
    public Inventory build(Player player, String... replacements) {
        try {
            GuiType guiType = GuiType.getFromFormat(this.pattern);
            Inventory inventory = guiType.build(player, this.title, this.pattern, replacements);

            updateGuiDisplay(inventory, replacements);

            return inventory;
        } catch (MassiveException ex) {
            if(player != null) {
                MixinMessage.get().messageOne(player, ex.getMessages());
            }
            return Bukkit.createInventory(player, 0, TxtUtil.parse(Config.get().failedGuiDisplay));
        }
    }

    public Inventory build(String... replacements) {
        return build(null, replacements);
    }
}
