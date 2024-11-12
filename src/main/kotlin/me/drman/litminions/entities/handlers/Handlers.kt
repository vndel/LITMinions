package me.drman.litminions.entities.handlers

import com.massivecraft.massivecore.ps.PS
import me.drman.litminions.integrations.EngineWildStacker
import me.drman.litminions.integrations.IntegrationWildStacker
import me.drman.litminions.MinionEntity
import me.drman.litminions.api.MinionAPI
import me.drman.litminions.api.MinionHandler
import me.drman.litminions.colls.getConf
import me.drman.litminions.extra.MobType
import me.drman.litminions.extra.Util
import me.drman.litminions.extra.getMinion
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack

class MinerHandler: MinionHandler {
    override fun action(minion: MinionEntity): Boolean {
        if(minion.getHunger() <= 0) {
            minion.startDeathClock()
            return false
        }
        val block: Block = minion.getLocation().with(PS.valueOf(minion.getLocation().asBukkitBlock().getRelative(Util.getDirFromYam(minion.getLocation().yaw)))).asBukkitBlock()
        //If minion has a chest link place items in chest else break naturally
        if(block.isEmpty) return false
        minion.performAnimation()
        if(minion.hasChestLink()) {
            val inv = minion.getChestInv()
            if(inv != null) {
                if(!inv.contents.any { it == null }) {
                    block.breakNaturally()
                    return true
                }
                block.drops.forEach{ inv.addItem(it) }
                block.type = Material.AIR
            } else block.breakNaturally()
        } else block.breakNaturally()
        minion.setHunger(minion.getHunger() - 1)
        return true
    }
}

class FeederHandler: MinionHandler {
    override fun action(minion: MinionEntity): Boolean {
        if(minion.getHunger() <= 0) {
            minion.startDeathClock()
            return false
        }
        val a = Bukkit.getWorld(minion.getLocation().world).getNearbyEntities(minion.getLocation().asBukkitLocation(), MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5, 0.5, MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5)
        a.removeIf{ it.type != EntityType.ARMOR_STAND }
        if(a.isEmpty()) return false
        minion.performAnimation()
        a.forEach {
            if(getMinion(it.location) != null) {
                val toFeed = getMinion(it.location) ?: return@forEach
                val minionConf = MinionAPI.getMinionConf(toFeed.getMinionType())
                val feedAmount = minionConf.hunger - toFeed.getHunger()
                toFeed.setHunger(toFeed.getHunger() + feedAmount)
                minion.setHunger(minion.getHunger() - feedAmount)
            }
        }
        return true
    }
}

open class ReaperHandler: MinionHandler {
    override fun action(minion: MinionEntity): Boolean {
        if(minion.getHunger() <= 0) {
            minion.startDeathClock()
            return false
        }
        val a = Bukkit.getWorld(minion.getLocation().world).getNearbyEntities(minion.getLocation().asBukkitLocation(), MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5, 0.5, MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5)
        a.removeIf{ it !is LivingEntity || it.type == EntityType.PLAYER || it.type == EntityType.ARMOR_STAND || getConf().reaperKillBlacklist.any { a -> a.type == it.type} }
        if(a.isEmpty()) return false
        minion.performAnimation()
        a.forEach {
            var amount = 1
            if(IntegrationWildStacker.get().isActive) {
                EngineWildStacker.get().killStackAmount(it as LivingEntity, MinionAPI.getMinionConf(minion.getMinionType()).killStackAmount)
                amount = MinionAPI.getMinionConf(minion.getMinionType()).killStackAmount
            } else it.remove()
            if (minion.hasChestLink()) {
                val inv = minion.getChestInv()
                if (inv != null) {
                    if(!inv.contents.any { e -> e == null }) {
                        minion.getLocation().asBukkitLocation().world.dropItem(
                            minion.getLocation().asBukkitLocation(),
                            ItemStack(Material.EXP_BOTTLE, amount)
                        )
                        return@forEach
                    }
                    inv.addItem(ItemStack(Material.EXP_BOTTLE, amount))
                } else minion.getLocation().asBukkitLocation().world.dropItem(
                    minion.getLocation().asBukkitLocation(),
                    ItemStack(Material.EXP_BOTTLE, amount)
                )
            } else minion.getLocation().asBukkitLocation().world.dropItem(
                minion.getLocation().asBukkitLocation(),
                ItemStack(Material.EXP_BOTTLE, amount)
            )
            minion.setHunger(minion.getHunger() - 1)
        }
        return true
    }
}

class ButcherHandler: MinionHandler {
    override fun action(minion: MinionEntity): Boolean {
        if(minion.getHunger() <= 0) {
            minion.startDeathClock()
            return false
        }
        val a = Bukkit.getWorld(minion.getLocation().world).getNearbyEntities(minion.getLocation().asBukkitLocation(), MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5, 0.5, MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5)
        a.removeIf{ it !is LivingEntity || !getConf().butcherKillWhitelist.any { a -> a.type == it.type} }
        if(a.isEmpty()) return false
        minion.performAnimation()
        a.forEach {
            val drops: MutableList<ItemStack>
            if(IntegrationWildStacker.get().isActive) {
                drops = EngineWildStacker.get().killStackAmount(it as LivingEntity, MinionAPI.getMinionConf(minion.getMinionType()).killStackAmount).toMutableList()
            } else {
                drops = if(it.type == EntityType.SKELETON) {
                    val meta = it as Skeleton
                    if(meta.skeletonType.id == 1) {
                        MobType.WITHER_SKELETON.basicDrops.toMutableList()
                    } else MobType.valueOf(it.type.toString()).basicDrops.toMutableList()
                } else MobType.valueOf(it.type.toString()).basicDrops.toMutableList()
                it.remove()
            }
            if (minion.hasChestLink()) {
                val inv = minion.getChestInv()
                if (inv != null) {
                    if(!inv.contents.any { e -> e == null }) {
                        drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
                        return@forEach
                    }
                    drops.forEach{ a -> inv.addItem(a) }
                } else drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
            } else drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
            minion.setHunger(minion.getHunger() - 1)
        }
        return true
    }
}

class HunterHandler: MinionHandler {
    override fun action(minion: MinionEntity): Boolean {
        if(minion.getHunger() <= 0) {
            minion.startDeathClock()
            return false
        }
        val a = Bukkit.getWorld(minion.getLocation().world).getNearbyEntities(minion.getLocation().asBukkitLocation(), MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5, 0.5, MinionAPI.getMinionConf(minion.getMinionType()).aoeSize.toDouble() + .5)
        a.removeIf{ it !is LivingEntity || !getConf().hunterKillWhitelist.any { a -> a.type == it.type} }
        if(a.isEmpty()) return false
        minion.performAnimation()
        a.forEach {
            val drops: MutableList<ItemStack>
            if(IntegrationWildStacker.get().isActive) {
                drops = EngineWildStacker.get().killStackAmount(it as LivingEntity, MinionAPI.getMinionConf(minion.getMinionType()).killStackAmount).toMutableList()
            } else {
                drops = if(it.type == EntityType.SKELETON) {
                    val meta = it as Skeleton
                    if(meta.skeletonType.id == 1) {
                        MobType.WITHER_SKELETON.basicDrops.toMutableList()
                    } else MobType.valueOf(it.type.toString()).basicDrops.toMutableList()
                } else MobType.valueOf(it.type.toString()).basicDrops.toMutableList()
                it.remove()
            }
            if (minion.hasChestLink()) {
                val inv = minion.getChestInv()
                if (inv != null) {
                    if(!inv.contents.any { e -> e == null }) {
                        drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
                        return@forEach
                    }
                    drops.forEach{ a -> inv.addItem(a) }
                } else drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
            } else drops.forEach{ a -> minion.getLocation().asBukkitLocation().world.dropItem(it.location, a) }
            minion.setHunger(minion.getHunger() - 1)
        }
        return true
    }
}