package me.drman.litminions.extra;

import com.massivecraft.massivecore.util.InventoryUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public final class TxtUtil {
    public static String parse(String string) {
        return Txt.parse(string);
    }

    public static String parse(String string, Object... args) {
        return Txt.parse(string, args);
    }

    public static Collection<String> parse(Collection<String> list) {
        return Txt.parse(list);
    }

    public static String parseAndReplace(String string, String... replacements) {
        if (replacements.length > 0) {
            Iterator iterator = Arrays.asList(replacements).iterator();

            while(iterator.hasNext()) {
                String key = (String)iterator.next();
                if (iterator.hasNext()) {
                    String value = (String)iterator.next();
                    if (key != null && value != null) {
                        string = string.replace(key, value);
                    }
                }
            }
        }

        return parse(string);
    }

    public static List<String> parseAndReplace(List<String> list, String... replacements) {
        return (List)list.stream().map((line) -> {
            return parseAndReplace(line, replacements);
        }).filter((line) -> {
            return !line.isEmpty();
        }).collect(Collectors.toList());
    }

    public static String getMaterialName(Material material) {
        return Txt.getNicedEnum(material);
    }

    public static String getItemName(ItemStack itemStack) {
        if (InventoryUtil.isNothing(itemStack)) {
            return Txt.parse("<silver><em>Nothing");
        } else {
            ChatColor color;
            try {
                color = itemStack.getEnchantments().size() > 0 ? ChatColor.AQUA : ChatColor.WHITE;
            } catch (Throwable var4) {
                color = ChatColor.WHITE;
            }

            if (color == ChatColor.WHITE && itemStack.getType() == Material.GOLDEN_APPLE) {
                short durability = itemStack.getDurability();
                switch(durability) {
                    case 0:
                    default:
                        color = ChatColor.AQUA;
                        break;
                    case 1:
                        color = ChatColor.LIGHT_PURPLE;
                }
            }

            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    return color.toString() + ChatColor.ITALIC.toString() + itemMeta.getDisplayName();
                }
            }

            String presetDisplay = null;
            short durability;
            if (itemStack.getType() == Material.COAL) {
                durability = itemStack.getDurability();
                switch(durability) {
                    case 0:
                    default:
                        presetDisplay = "Coal";
                        break;
                    case 1:
                        presetDisplay = "Charcoal";
                }
            } else if (itemStack.getType() == Material.INK_SACK) {
                durability = itemStack.getDurability();
                switch(durability) {
                    case 0:
                    default:
                        presetDisplay = "Ink Sack";
                        break;
                    case 1:
                        presetDisplay = "Rose Red";
                        break;
                    case 2:
                        presetDisplay = "Cactus Green";
                        break;
                    case 3:
                        presetDisplay = "Cocoa Beans";
                        break;
                    case 4:
                        presetDisplay = "Lapis Lazuli";
                        break;
                    case 5:
                        presetDisplay = "Purple Dye";
                        break;
                    case 6:
                        presetDisplay = "Cyan Dye";
                        break;
                    case 7:
                        presetDisplay = "Light Gray Dye";
                        break;
                    case 8:
                        presetDisplay = "Gray Dye";
                        break;
                    case 9:
                        presetDisplay = "Pink Dye";
                        break;
                    case 10:
                        presetDisplay = "Lime Dye";
                        break;
                    case 11:
                        presetDisplay = "Dandelion Yellow";
                        break;
                    case 12:
                        presetDisplay = "Light Blue Dye";
                        break;
                    case 13:
                        presetDisplay = "Magenta Dye";
                        break;
                    case 14:
                        presetDisplay = "Orange Dye";
                        break;
                    case 15:
                        presetDisplay = "Bone Meal";
                }
            } else if (itemStack.getType() == Material.COOKED_FISH) {
                durability = itemStack.getDurability();
                switch(durability) {
                    case 0:
                    default:
                        presetDisplay = "Cooked Fish";
                        break;
                    case 1:
                        presetDisplay = "Cooked Salmon";
                }
            } else if (itemStack.getType() == Material.RAW_FISH) {
                durability = itemStack.getDurability();
                switch(durability) {
                    case 0:
                    default:
                        presetDisplay = "Raw Fish";
                        break;
                    case 1:
                        presetDisplay = "Raw Salmon";
                        break;
                    case 2:
                        presetDisplay = "Clownfish";
                        break;
                    case 3:
                        presetDisplay = "Pufferfish";
                }
            }

            return color + (presetDisplay != null ? presetDisplay : Txt.getMaterialName(itemStack.getType()));
        }
    }

    public static String shuffle(String input) {
        List<Character> characters = new ArrayList();
        char[] var2 = input.toCharArray();
        int randPicker = var2.length;

        for(int var4 = 0; var4 < randPicker; ++var4) {
            char c = var2[var4];
            characters.add(c);
        }

        StringBuilder output = new StringBuilder(input.length());

        while(characters.size() != 0) {
            randPicker = (int)(Math.random() * (double)characters.size());
            output.append(characters.remove(randPicker));
        }

        return output.toString();
    }

    public static String getPrettyLocation(Location location) {
        return getPrettyLocation(location, true);
    }

    public static String getPrettyLocation(Location location, boolean includeWorld) {
        return (includeWorld ? location.getWorld().getName() + ", " : "") + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    private TxtUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
