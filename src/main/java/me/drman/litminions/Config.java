package me.drman.litminions;

import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import me.drman.litminions.extra.ItemStackWrapper;
import me.drman.litminions.extra.MobType;
import me.drman.litminions.extra.gui.configuration.Clickable;
import me.drman.litminions.extra.gui.configuration.GuiDesign;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class Config extends Entity<Config> {
    public static Config inst;
    public static Config get() { return inst; }
    public static void set(Config i) { inst = i; }

    public Map<String, Integer> rankLimit = MUtil.map(
        "default", 5
    );
    public String msgGiveSelf = "&a&lMinions &8» &eMinion/Item has been given.";
    public String msgGiveOther = "&a&lMinions &8» &eYou have received a minion/item.";
    public String msgRemoveSelf = "&a&lMinions &8» &eDesired minion has been removed.";
    public String msgRemoveOther = "&a&lMinions &8» &eA minion has been removed by an administrator.";
    public String msgMinionOutOfHunger = "&a&lMinions &8» &eOne of your minions has ran out of hunger. &f$location";
    public String msgMinionDied = "&a&lMinions &8» &eOne of your minions has died. &f$location";
    public String minionsInfoFormat = "&b$id &e- $location; $type; Hunger:$hunger/Health:$health";
    public String minionLimitReached = "&a&lMinions &8» &cYou have reached max minions place. This can be upgraded by purchasing a rank at www.example.com.";
    public String chestLinkConnected = "&a&lMinions &8» &eChest has been set.";
    public String msgCantBreakOthersMinion = "&a&lMinions &8» &cYou are not allowed to break other's minions.";
    public String msgIslandPermDenyPickUp = "&a&lMinions &8» &cThis island does not allow you to pickup minions.";
    public String msgIslandPermDenyPlace = "&a&lMinions &8» &cThis island does not allow you to place minions.";
    public String msgIslandPermDenyUse = "&a&lMinions &8» &cThis island does not allow you to use minions.";
    public String msgHasNoMinion = "&a&lMinions &8» &cUnknown minion with given ID.";
    public String msgNotEnoughFunds = "&a&lMinions &8» &cYou do not have enough funds to purchase this item.";
    public String msgInvIsFull = "&a&lMinions &8» &cNo space available in your or given player's inventory.";
    public String msgMustHaveLinkInHand = "&a&lMinions &8» &cMust have an Chest Link item in hand to continue.";
    public String msgMustHaveFoodInHand = "&a&lMinions &8» &cMust have Minion Food/Heal item to continue.";
    public String msgLinkMustBeLinked = "&a&lMinions &8» &cGiven Chest Link is not linked.";
    public String msgUnknownPlayer = "&a&lMinions &8» &cUnknown given player.";
    public String msgUnknownMinion = "&a&lMinions &8» &cUnknown given Minion type.";
    public String msgUnknownMinionItem = "&a&lMinions &8» &cUnknown give item type.";
    public String msgLinkMustBeChest = "&a&lMinions &8» &cLocation given does not contain a chest.";
    public String msgLocationContainsMinion = "&a&lMinions &8» &cLocation given contains a minion.";

    public long guiClickThrottleDelayMs = 200L;
    public String failedGuiDisplay = "&c&lERROR 404";
    public String msgGuiNameNotSet = "&a&lMinions &8» &cThe gui name is not setup for that gui.";
    public String msgGuiFormatNotSet = "&a&lMinions &8» &cThe gui format is not setup for that gui.";
    public String msgGuiRowLengthNotSame = "&a&lMinions &8» &cThe gui is not setup correctly. Row lengths do not match.";
    public String msgGuiTooManyRows = "&a&lMinions &8» &cThere is too many rows configured for this gui type.";
    public String msgGuiDesignNotSet = "&a&lMinions &8» &cThe gui design is not set! Please contact the developer.";
    public String msgGuiInventoryNotSet = "&a&lMinions &8» &cThe inventory has not been initialized! Please show a developer this.";
    public String msgGuiLeaveWindowToEdit = "&a&lMinions &8» &cYou cannot do that while you're within a gui window.";

    public String minionNameTag = "&6Hunger&e$hunger&7-&c$health&4Health";
    public long minionNameTagUpdateTick = 20L;

    public Map<String,MinionFood> minionFoodList = MUtil.map(
    "basic_food", new MinionFood(new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&a&lBasic Minion Food", MUtil.list("&7Feeds your minion by &a&l10")), 10, false, 1000),
    "deluxe_food", new MinionFood(new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&e&lDeluxe Minion Food", MUtil.list("&7Feeds your minion by &e&l100")), 100, false, 10000),
    "luxurious_food", new MinionFood(new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&d&lLuxurious Minion Food", MUtil.list("&7Feeds your minion by &d&l1,000")), 1000, false, 100000),
    "med_kit", new MinionFood(new ItemStackWrapper(Material.POTION, 1, (short)8261, "&2&lMinion Med-kit", MUtil.list("&7Heals your minion by &2&l100")), 100, true, 50000)
    );

    public GuiDesign minionShopGui = new GuiDesign(
        "&e&lMinion Shop",
        MUtil.map(
        '#', new ItemStackWrapper(Material.STAINED_GLASS_PANE, 1, (short)7),
        '-', new ItemStackWrapper(Material.AIR)
        ),
        MUtil.list(
        "##-----##",
        "##-----##"
        )
    );

    public List<Clickable> minionShopButtons = MUtil.list(
            new Clickable(2, new ItemStackWrapper(Material.MONSTER_EGG, 1, (short)96, "&4&lMining Minion", MUtil.list("&7Mines block in front of it.", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "EGG", "miner"),
            new Clickable(3, new ItemStackWrapper(Material.MONSTER_EGG, 1, (short)101, "&7&lFeeder Minion", MUtil.list("&7Feed surrounding minions. Max hunger is &710,000&8.", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "EGG", "feeder"),
            new Clickable(4, new ItemStackWrapper(Material.MONSTER_EGG, 1, (short)58, "&8&lReaper Minion", MUtil.list("&7Kills nearby hostile mobs and drops &7XP Bottles&7 instead of loot.", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "EGG", "reaper"),
            new Clickable(5, new ItemStackWrapper(Material.MONSTER_EGG, 1, (short)91, "&2&lButcher Minion", MUtil.list("&7Kills nearby passive mobs and drops loot.", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "EGG", "butcher"),
            new Clickable(6, new ItemStackWrapper(Material.MONSTER_EGG, 1, (short)50, "&a&lHunter Minion", MUtil.list("&7Kills nearby hostile mobs and drops loot.", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "EGG", "hunter"),
            new Clickable(11, new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&a&lBasic Minion Food", MUtil.list("&7Feeds your minion by &a&l10", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "FOOD", "basic_food"),
            new Clickable(12, new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&e&lDeluxe Minion Food", MUtil.list("&7Feeds your minion by &e&l10", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "FOOD", "deluxe_food"),
            new Clickable(13, new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&d&lLuxurious Minion Food", MUtil.list("&7Feeds your minion by &d&l10", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "FOOD", "luxurious_food"),
            new Clickable(14, new ItemStackWrapper(Material.POTION, 1, (short)8261, "&2&lMinion Med-kit", MUtil.list("&7Heals your minion by &2&l100", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "FOOD", "med_kit"),
            new Clickable(15, new ItemStackWrapper(Material.ENDER_PEARL, 1, (short)0, "&e&lChest Link", MUtil.list("&8Can link a chest to a minion by", "&8right-clicking a chest and placing", "&8it in the minion's menu.", "", "&8Linked to: &c&lUnknown", "", "&2&l&m-----------", "&a&l   $$price", "&2&l&m-----------")), "LINK")
    );

    public ItemStackWrapper minionChestLink = new ItemStackWrapper(
        Material.ENDER_PEARL,
        1, (short)0,
        "&e&lChest Link",
        MUtil.list(
        "&8Can link a chest to a minion by",
        "&8right-clicking a chest and placing",
        "&8it in the minion's menu.",
        "",
        "&8Linked to: &e&l$location"
    ));

    public long minionChestLinkPrice = 100000L;

    public GuiDesign minionInteractGui = new GuiDesign(
            "&e&lMinion Menu", 
            MUtil.map(
                    '#', new ItemStackWrapper(Material.STAINED_GLASS_PANE, 1, (short) 7), 
                    '-', new ItemStackWrapper(Material.AIR)), 
            MUtil.list("##-----##")
    );

    public List<Clickable> minionInteractButtons = MUtil.list(
        new Clickable(3, new ItemStackWrapper(Material.ENDER_PEARL, 1, (short)0, "&e&lChest Link",MUtil.list("&7Place Chest Link item with a chest linked to link chest.", "&7Currently set to: $location")), "LINK"),
        new Clickable(4, new ItemStackWrapper(Material.BOOK, 1, (short)0, "&8&lMinion Info", MUtil.list("&7Information about your minion","","&6Hunger &7&l» &6$hunger", "&4Health &7&l» &c$health")), "NONE"),
        new Clickable(5, new ItemStackWrapper(Material.COOKED_BEEF, 1, (short)0, "&6&lFood Slot", MUtil.list("&7Place Minion Food to keep your minion's health from depleting")), "FEED")
    );

    public ItemStackWrapper cannotHaveLinkItem = new ItemStackWrapper(
            Material.BARRIER,
            1,
            (short)0,
            "&4&lUnlinkable",
            MUtil.list(
                    "&7This minion cannot have a chest link."
            )
    );

    public List<MobType> hunterKillWhitelist = MUtil.list(
        MobType.CREEPER,
        MobType.ZOMBIE,
        MobType.PIG_ZOMBIE,
        MobType.SKELETON,
        MobType.SPIDER,
        MobType.CAVE_SPIDER,
        MobType.WITHER_SKELETON,
        MobType.WITCH,
        MobType.GUARDIAN,
        MobType.BLAZE,
        MobType.GHAST,
        MobType.ENDERMAN,
        MobType.ENDERMITE,
        MobType.SILVERFISH
    );
    public List<MobType> butcherKillWhitelist = MUtil.list(
        MobType.SHEEP,
        MobType.COW,
        MobType.PIG,
        MobType.CHICKEN,
        MobType.RABBIT,
        MobType.WOLF,
        MobType.HORSE,
        MobType.IRON_GOLEM
    );
    public List<MobType> reaperKillBlacklist = MUtil.list(
        MobType.IRON_GOLEM
    );
}