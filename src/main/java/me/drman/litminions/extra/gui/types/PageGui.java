package me.drman.litminions.extra.gui.types;

import com.massivecraft.massivecore.util.InventoryUtil;
import com.massivecraft.massivecore.util.MUtil;
import me.drman.litminions.LITMinionsPlugin;
import me.drman.litminions.extra.InvUtil;
import me.drman.litminions.extra.ItemStackWrapper;
import me.drman.litminions.extra.NumberUtil;
import me.drman.litminions.extra.gui.IGuiClick;
import me.drman.litminions.extra.gui.configuration.GuiDesign;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PageGui<T> extends RefreshGui implements IClickableGui {

    protected final Map<Integer, Integer> trueSlotMap = new HashMap<>();
    protected final Set<Integer> skipSlots = new HashSet<>();

    protected int currentPage = 0, maxPage = 0, amountPerPage = 0;
    private boolean displayButtons = false;

    public PageGui(Player player, GuiDesign guiDesign) {
        super(player, guiDesign);
    }

    public abstract List<T> getCompleteList();

    public abstract ItemStack getItemStack(int listPosition, T t);

    public IGuiClick getGuiClick(int listPosition, T t) {
        return null;
    }

    public ItemStack getEmptyItemStack() {
        return new ItemStack(Material.AIR);
    }

    @Override
    protected void preloadGui() {
        Inventory inventory = this.inventory;

        if(inventory == null) return;

        getClickablesFiltered(inventory).forEach(clickable -> {
            if(InventoryUtil.isNothing(inventory.getItem(clickable.getSlot()))) {
                inventory.setItem(clickable.getSlot(), new ItemStackWrapper(Material.GLASS, "&7Pending clickable...").toItemStack());
            }
        });

        List<T> completeList = getCompleteList();

        int amountPerPage = InvUtil.getFreeSlots(inventory);
        int maxPage = (int) Math.ceil((double) completeList.size() / (double) amountPerPage) - 1;
        boolean displayButtons = completeList.size() > amountPerPage;

        this.maxPage = maxPage;
        this.displayButtons = displayButtons;
        this.amountPerPage = amountPerPage;
    }

    @Override
    public void loadClicks() {
        if(!this.displayButtons) return;

        List<String> replaceables = getReplacements();

        replaceables.addAll(MUtil.list(
                "{page}", NumberUtil.format(this.currentPage+1),
                "{maxPage}", NumberUtil.format(this.maxPage+1)
        ));

        getClickables().stream()
                .filter(clickable -> clickable.getSlot() >= 0 && clickable.getSlot() < this.inventory.getSize())
                .forEach(clickable -> {
                    try {
                        Action action = Action.valueOf(clickable.getAction());
                        ItemStack itemStack = clickable.getDisplayItem().toItemStack(replaceables.toArray(new String[0]));
                        int slot = clickable.getSlot();

                        switch (action) {
                            case NEXT_PAGE:
                                if(this.displayButtons) {
                                    this.inventory.setItem(slot, itemStack);
                                    this.setClickable(slot, event -> {
                                        if(this.currentPage >= maxPage) return;

                                        this.currentPage += 1;
                                        loadClicks();
                                        refresh();
                                    });
                                }

                                break;
                            case PREVIOUS_PAGE:
                                if(this.displayButtons) {
                                    this.inventory.setItem(slot, itemStack);
                                    this.setClickable(slot, event -> {
                                        if(this.currentPage <= 0) return;

                                        this.currentPage -= 1;
                                        loadClicks();
                                        refresh();
                                    });
                                }

                                break;
                            case CURRENT_PAGE:
                                if(this.displayButtons) {
                                    this.inventory.setItem(slot, itemStack);
                                }
                        }
                    } catch (Exception ignore) {}
                });
    }

    @Override
    public void refresh() {
        int startIndex = this.currentPage * this.amountPerPage;
        List<T> items = getCompleteList();

        for(int i = startIndex; i < startIndex+this.amountPerPage; i++) {
            int realSlot = this.trueSlotMap.getOrDefault(i-startIndex, this.inventory.firstEmpty());
            this.trueSlotMap.put(i-startIndex, realSlot);

            if(realSlot == -1) {
                LITMinionsPlugin.get().log("Issue loading page; item listing slot is -1");
                break;
            }

            if(i >= items.size() || items.isEmpty()) {
                this.inventory.setItem(realSlot, getEmptyItemStack());
                clearClickable(realSlot);
            } else {
                T t = items.get(i);

                IGuiClick guiClick = getGuiClick(i, t);
                ItemStack itemStack = getItemStack(i, t);

                if(!this.skipSlots.contains(i)) {
                    this.inventory.setItem(realSlot, itemStack);
                }

                if(guiClick != null) {
                    this.setClickable(realSlot, guiClick);
                }
            }
        }
    }

    private enum Action {

        PREVIOUS_PAGE,
        CURRENT_PAGE,
        NEXT_PAGE

    }
}
