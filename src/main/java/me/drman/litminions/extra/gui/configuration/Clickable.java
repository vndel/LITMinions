package me.drman.litminions.extra.gui.configuration;

import me.drman.litminions.extra.ItemStackWrapper;

public class Clickable {

    private final ItemStackWrapper displayItem;
    private final String action;
    private final int slot;
    private String[] args;

    public Clickable(int slot, ItemStackWrapper itemStackWrapper, String actionLabel) {
        this.slot = slot;
        this.displayItem = itemStackWrapper;
        this.action = actionLabel;
    }

    public Clickable(int slot, ItemStackWrapper itemStackWrapper, String actionLabel, String... args) {
        this.slot = slot;
        this.displayItem = itemStackWrapper;
        this.action = actionLabel;
        this.args = args;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStackWrapper getDisplayItem() {
        return this.displayItem;
    }

    public String getAction() {
        return this.action;
    }

    public String[] getArgs() {
        return this.args;
    }
}
