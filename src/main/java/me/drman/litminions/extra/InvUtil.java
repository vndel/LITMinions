package me.drman.litminions.extra;

import com.massivecraft.massivecore.mixin.MixinInventory;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

import static me.drman.litminions.extra.ExtraKt.createInventory;

public final class InvUtil {
    private static Set<Material> TRANSPARENT_MATERIALS;

    public static int getFreeSlots(Inventory inventory) {
        inventory = InventoryUtil.clone(inventory, false);
        int count = 0;

        for(int i = 0; i < inventory.getSize(); ++i) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                ++count;
            }
        }

        return count;
    }

    public static void giveItemStack(Player player, ItemStack itemStack) {
        if (player != null && itemStack != null) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                MixinMessage.get().msgOne(player, "&7&oYour inventory was full so the item was dropped at your feet.");
            } else {
                player.getInventory().addItem(new ItemStack[]{itemStack});
            }

        }
    }

    public static void removeSingleItemInHand(Player player, ItemStack itemStack) {
        if (itemStack.getAmount() > 1) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }

    }

    public static Set<Material> getTransparentMaterial() {
        if (TRANSPARENT_MATERIALS != null) {
            return TRANSPARENT_MATERIALS;
        } else {
            TRANSPARENT_MATERIALS = new HashSet();
            Stream var10000 = Arrays.stream(Material.values()).filter(Objects::nonNull).filter(Material::isTransparent);
            Set var10001 = TRANSPARENT_MATERIALS;
            var10000.forEach(var10001::add);
            TRANSPARENT_MATERIALS.add(Material.WATER);
            return TRANSPARENT_MATERIALS;
        }
    }

    public static Inventory clone(Inventory inventory, InventoryType inventoryType) {
        if (inventory == null) {
            return null;
        } else {
            Inventory ret = createInventory(inventory.getHolder(), inventoryType, inventory.getType().getDefaultTitle());
            ItemStack[] all = InventoryUtil.getContentsAll(inventory);
            all = InventoryUtil.clone(all);
            InventoryUtil.setContentsAll(ret, all);
            return ret;
        }
    }

    public static int roomLeft(Inventory inventory, InventoryType inventoryType, ItemStack itemStack, int limit) {
        inventory = clone(inventory, inventoryType);

        int ret;
        for(ret = 0; limit <= 0 || ret < limit; ++ret) {
            HashMap<Integer, ItemStack> result = inventory.addItem(new ItemStack[]{itemStack.clone()});
            if (result.size() != 0) {
                return ret;
            }
        }

        return ret;
    }

    private InvUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
