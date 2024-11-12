package me.drman.litminions.extra;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum MobType {
    BAT(EntityType.BAT),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, new ItemStack(Material.LEATHER), new ItemStack(Material.RAW_BEEF)),
    CHICKEN(EntityType.CHICKEN, new ItemStack(Material.FEATHER), new ItemStack(Material.RAW_CHICKEN)),
    COW(EntityType.COW, new ItemStack(Material.LEATHER), new ItemStack(Material.RAW_BEEF)),
    HORSE(EntityType.HORSE, new ItemStack(Material.LEATHER)),
    OCELOT(EntityType.OCELOT),
    PIG(EntityType.PIG, new ItemStack(Material.PORK)),
    RABBIT(EntityType.RABBIT, new ItemStack(Material.RABBIT), new ItemStack(Material.RABBIT_HIDE)),
    SHEEP(EntityType.SHEEP, new ItemStack(Material.MUTTON),new ItemStack(Material.WOOL)),
    SNOWMAN(EntityType.SNOWMAN, new ItemStack(Material.SNOW_BALL)),
    SQUID(EntityType.SNOWMAN, new ItemStack(Material.SNOW_BALL)),
    VILLAGER(EntityType.VILLAGER),
    SPIDER(EntityType.SPIDER, new ItemStack(Material.STRING), new ItemStack(Material.SPIDER_EYE)),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, new ItemStack(Material.STRING), new ItemStack(Material.SPIDER_EYE)),
    ENDERMAN(EntityType.ENDERMAN, new ItemStack(Material.ENDER_PEARL)),
    IRON_GOLEM(EntityType.IRON_GOLEM, new ItemStack(Material.IRON_INGOT), new ItemStack(Material.RED_ROSE)),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.ROTTEN_FLESH)),
    WOLF(EntityType.WOLF),
    BLAZE(EntityType.BLAZE, new ItemStack(Material.BLAZE_ROD)),
    CREEPER(EntityType.CREEPER, new ItemStack(Material.SULPHUR)),
    GUARDIAN(EntityType.GUARDIAN, new ItemStack(Material.PRISMARINE_SHARD),new ItemStack(Material.PRISMARINE_CRYSTALS), new ItemStack(Material.RAW_FISH)),
    GHAST(EntityType.GHAST, new ItemStack(Material.SULPHUR), new ItemStack(Material.GHAST_TEAR)),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, new ItemStack(Material.MAGMA_CREAM)),
    SILVERFISH(EntityType.SILVERFISH),
    SKELETON(EntityType.SKELETON, new ItemStack(Material.BONE), new ItemStack(Material.ARROW)),
    SLIME(EntityType.SLIME, new ItemStack(Material.SLIME_BALL)),
    WITCH(EntityType.WITCH, new ItemStack(Material.SUGAR),new ItemStack(Material.GLOWSTONE_DUST), new ItemStack(Material.REDSTONE), new ItemStack(Material.SULPHUR), new ItemStack(Material.STICK)),
    WITHER_SKELETON(EntityType.SKELETON,1, new ItemStack(Material.COAL), new ItemStack(Material.BONE)),
    ZOMBIE(EntityType.ZOMBIE, new ItemStack(Material.ROTTEN_FLESH)),
    ENDERMITE(EntityType.ENDERMITE),
    ;
    private final EntityType entityType;
    private final ItemStack[] basicDrops;
    private final int meta;

    MobType(EntityType entityType) {
        this.entityType = entityType;
        this.meta = 0;
        this.basicDrops = new ItemStack[]{};
    }

    MobType(EntityType entityType, ItemStack... drops) {
        this.entityType = entityType;
        this.meta = 0;
        this.basicDrops = drops;
    }

    MobType(EntityType entityType, int meta, ItemStack... drops) {
        this.entityType = entityType;
        this.meta = meta;
        this.basicDrops = drops;
    }

    public EntityType getType() {
        return entityType;
    }

    public int getMeta() {
        return meta;
    }

    public ItemStack[] getBasicDrops() {
        return basicDrops;
    }
}
