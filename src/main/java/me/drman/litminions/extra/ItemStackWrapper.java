package me.drman.litminions.extra;

import com.massivecraft.massivecore.MassiveCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemStackWrapper {
    private final Material material;
    private final short damage;
    private final int amount;
    private MetaWrapper meta;

    public ItemStackWrapper(Material type) {
        this(type, (int)1);
    }

    public ItemStackWrapper(Material type, int amount) {
        this(type, amount, (short)0);
    }

    public ItemStackWrapper(Material type, short damage) {
        this(type, 1, damage);
    }

    public ItemStackWrapper(Material type, int amount, short damage) {
        this.material = type;
        this.amount = amount;
        this.damage = damage;
    }

    public ItemStackWrapper(Material type, String displayName) {
        this(type, 1, (short)0, (String)displayName);
    }

    public ItemStackWrapper(Material type, short damage, String displayName) {
        this(type, 1, damage, (String)displayName);
    }

    public ItemStackWrapper(Material type, int amount, short damage, String displayName) {
        this(type, (int)amount, (short)damage, (String)displayName, (List)null);
    }

    public ItemStackWrapper(Material type, int amount, short damage, List<String> lore) {
        this(type, (int)amount, (short)damage, (String)null, (List)lore);
    }

    public ItemStackWrapper(Material type, int amount, short damage, String displayName, List<String> lore) {
        this(type, amount, damage);
        if (displayName != null || lore != null) {
            MetaWrapper metaWrapper = new MetaWrapper();
            if (displayName != null) {
                metaWrapper.setDisplayName(displayName);
            }

            if (lore != null) {
                metaWrapper.setLore(lore);
            }

            this.meta = metaWrapper;
        }

    }

    public ItemStackWrapper(Material material, short damage, int amount, MetaWrapper metaWrapper) {
        this(material, amount, damage);
        this.meta = metaWrapper;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (this.material != null) {
            stringBuilder.append("material=").append(this.material.name()).append(" ");
        }

        if (this.meta != null) {
            if (this.meta.getDisplayName() != null) {
                stringBuilder.append("displayName=").append(this.meta.getDisplayName()).append(" ");
            }

            if (this.meta.getLore() != null) {
                stringBuilder.append("lore=").append(this.meta.getLore().toString()).append(" ");
            }

            if (this.meta.getEnchants() != null) {
                stringBuilder.append("enchants=").append(this.meta.getEnchants().toString()).append(" ");
            }

            if (this.meta.getItemFlags() != null) {
                stringBuilder.append("itemFlags=").append(this.meta.getItemFlags().toString()).append(" ");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public ItemStack getBaseItem() {
        try {
            Material material = this.material;
            if (material == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException var4) {
            MassiveCore.get().log(new Object[]{"material is missing from ItemStackWrapper!"});
            MassiveCore.get().log(new Object[]{this.toString()});
            return new ItemStack(Material.AIR);
        }

        try {
            int var5 = this.amount;
        } catch (NullPointerException var3) {
            MassiveCore.get().log(new Object[]{"amount is missing from ItemStackWrapper."});
            MassiveCore.get().log(new Object[]{this.toString()});
            return new ItemStack(Material.AIR);
        }

        try {
            short var6 = this.damage;
        } catch (NullPointerException var2) {
            MassiveCore.get().log(new Object[]{"damage is missing from ItemStackWrapper."});
            MassiveCore.get().log(new Object[]{this.toString()});
            return new ItemStack(Material.AIR);
        }

        return new ItemStack(this.material, this.amount, this.damage);
    }

    public boolean hasDisplayName() {
        return this.meta != null && this.meta.getDisplayName() != null && !this.meta.getDisplayName().isEmpty();
    }

    public boolean hasDisplayLore() {
        return this.meta != null && this.meta.getLore() != null && !this.meta.getLore().isEmpty();
    }

    public ItemStack applyMeta(ItemStack itemStack, String... replacements) {
        if (this.meta != null) {
            MetaWrapper metaWrapper = this.meta;
            ItemMeta itemMeta = itemStack.getItemMeta();
            String displayName = metaWrapper.getDisplayName();
            List<String> lore = metaWrapper.getLore();
            Map<String, Integer> enchants = metaWrapper.getEnchants();
            Set<ItemFlag> itemFlags = metaWrapper.getItemFlags();
            if (displayName != null) {
                itemMeta.setDisplayName(TxtUtil.parseAndReplace(displayName, replacements));
            }

            if (lore != null) {
                itemMeta.setLore(TxtUtil.parseAndReplace(lore, replacements));
            }

            if (enchants != null) {
                enchants.forEach((enchant, level) -> {
                    Enchantment enchantment = Enchantment.getByName(enchant);
                    if (enchant != null) {
                        itemMeta.addEnchant(enchantment, level, true);
                    }
                });
            }

            if (itemFlags != null) {
                itemFlags.forEach((xva$0) -> {
                    itemMeta.addItemFlags(new ItemFlag[]{xva$0});
                });
            }

            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    public ItemStack toItemStack(List<String> replacements) {
        return this.toItemStack((String[])replacements.toArray(new String[0]));
    }

    public ItemStack toItemStack(String... replacements) {
        ItemStack clone = this.getBaseItem();
        if (clone == null) {
            throw new NullPointerException("clone itemStack is null!");
        } else if (clone.getType() == Material.AIR) {
            return clone;
        } else {
            if (this.meta != null) {
                MetaWrapper metaWrapper = this.meta;
                ItemMeta itemMeta = clone.getItemMeta();
                String displayName = metaWrapper.getDisplayName();
                List<String> lore = metaWrapper.getLore();
                Map<String, Integer> enchants = metaWrapper.getEnchants();
                Set<ItemFlag> itemFlags = metaWrapper.getItemFlags();
                boolean unbreakable = metaWrapper.isUnbreakable();
                if (itemMeta == null) {
                    throw new NullPointerException("itemMeta is null!");
                }

                if (replacements == null) {
                    throw new NullPointerException("replacements is null!");
                }

                if (displayName != null) {
                    itemMeta.setDisplayName(TxtUtil.parseAndReplace(displayName, replacements));
                }

                if (lore != null) {
                    itemMeta.setLore(TxtUtil.parseAndReplace(lore, replacements));
                }

                if (enchants != null) {
                    enchants.forEach((enchant, level) -> {
                        Enchantment enchantment = Enchantment.getByName(enchant);
                        if (enchant != null) {
                            itemMeta.addEnchant(enchantment, level, true);
                        }
                    });
                }

                if (itemFlags != null) {
                    itemFlags.forEach((xva$0) -> {
                        itemMeta.addItemFlags(new ItemFlag[]{xva$0});
                    });
                }

                itemMeta.spigot().setUnbreakable(unbreakable);
                clone.setItemMeta(itemMeta);
            }

            return clone;
        }
    }

    public static ItemStackWrapper toWrapper(ItemStack itemStack) {
        ItemStackWrapper.ItemStackWrapperBuilder itemStackWrapperBuilder = builder();
        itemStackWrapperBuilder.amount(itemStack.getAmount()).damage(itemStack.getDurability()).material(itemStack.getType());
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            MetaWrapper metaWrapper = new MetaWrapper();
            if (itemMeta.hasDisplayName()) {
                metaWrapper.setDisplayName(itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                metaWrapper.setLore(itemMeta.getLore());
            }

            if (itemMeta.hasEnchants()) {
                itemMeta.getEnchants().forEach((enchant, level) -> {
                    metaWrapper.addEnchant(enchant.getName(), level);
                });
            }

            if (!itemMeta.getItemFlags().isEmpty()) {
                metaWrapper.setItemFlags(itemMeta.getItemFlags());
            }

            if (itemMeta.spigot().isUnbreakable()) {
                metaWrapper.setUnbreakable(true);
            }

            itemStackWrapperBuilder.meta(metaWrapper);
        }

        return itemStackWrapperBuilder.build();
    }

    public static ItemStackWrapper.ItemStackWrapperBuilder builder() {
        return new ItemStackWrapper.ItemStackWrapperBuilder();
    }

    public MetaWrapper getMeta() {
        return this.meta;
    }

    public void setMeta(MetaWrapper meta) {
        this.meta = meta;
    }

    public static class ItemStackWrapperBuilder {
        private Material material;
        private short damage;
        private int amount;
        private MetaWrapper meta;

        ItemStackWrapperBuilder() {
        }

        public ItemStackWrapper.ItemStackWrapperBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public ItemStackWrapper.ItemStackWrapperBuilder damage(short damage) {
            this.damage = damage;
            return this;
        }

        public ItemStackWrapper.ItemStackWrapperBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemStackWrapper.ItemStackWrapperBuilder meta(MetaWrapper meta) {
            this.meta = meta;
            return this;
        }

        public ItemStackWrapper build() {
            return new ItemStackWrapper(this.material, this.damage, this.amount, this.meta);
        }

        public String toString() {
            return "ItemStackWrapper.ItemStackWrapperBuilder(material=" + this.material + ", damage=" + this.damage + ", amount=" + this.amount + ", meta=" + this.meta + ")";
        }
    }
}

