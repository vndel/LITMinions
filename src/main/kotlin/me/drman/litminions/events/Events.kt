package me.drman.litminions.events

import com.massivecraft.massivecore.Engine
import com.massivecraft.massivecore.collections.MassiveMap
import com.massivecraft.massivecore.ps.PS
import me.drman.litminions.MinionEntity
import me.drman.litminions.LITMinionsPlugin
import me.drman.litminions.api.MinionAPI
import me.drman.litminions.api.MinionHandler
import me.drman.litminions.colls.MPlayerColl
import me.drman.litminions.colls.getConf
import me.drman.litminions.commands.Permission
import me.drman.litminions.entities.Minion
import me.drman.litminions.extra.*
import me.drman.litminions.extra.Util
import me.drman.litminions.gui.MinionInteractGUI
import me.drman.litminions.integrations.EngineSuperiorSkyblock
import me.drman.litminions.integrations.IntegrationSuperiorSkyblock
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector


class Events: Engine() {
    companion object {
        @JvmStatic
        private var INST = Events()
        @JvmStatic
        fun get(): Events { return INST }
    }
    @EventHandler
    fun minionSpawn(event: PlayerInteractEvent) {
        if(event.hasItem() && event.action.equals(Action.RIGHT_CLICK_BLOCK) && NBT.hasMetadata(event.item)) {
            if(NBT.getItem(event.item) == ItemType.MINION) {
                val player = MPlayerColl.get().get(event.player)
                if (IntegrationSuperiorSkyblock.get().isActive) {
                    if (!Permission.OVERRIDE.has(event.player) && !EngineSuperiorSkyblock.get().canPlaceMinion(event.player, event.clickedBlock.location)) {
                        event.isCancelled = true
                        player.msg(TxtUtil.parse(getConf().msgIslandPermDenyPlace))
                        return
                    }
                }
                val loc = event.clickedBlock.getRelative(BlockFace.UP)
                if(loc.isEmpty) {
                    if(MinionAPI.getMinion(loc.location) == null) {
                        val rank = LITMinionsPlugin.get().perms.getPrimaryGroup(event.player)
                        val a = getConf().rankLimit[rank]
                        if (a != null && player.listSize() >= a) {
                            event.isCancelled = true
                            player.msg(TxtUtil.parse(getConf().minionLimitReached))
                            return
                        }
                        val type = MinionEngine.get().getConf(NBT.getType(event.item))!!
                        val minion = MinionEntity(
                            PS.valueOf(loc.location.add(Vector(0.5, 0.0, 0.5)))
                                .with(PS.valueOf(loc.location.block))
                                .withYaw(Util.approximateYam(event.player.location.yaw.toInt()))
                                .withPitch(0f),
                            type.id,
                            player.name,
                            NBT.getCustom(event.player.itemInHand, "minionHunger").toLong(),
                            NBT.getCustom(event.player.itemInHand, "minionHealth").toLong()
                        )
                        player.addMinion(minion)
                        minion.spawn()
                        val b = event.player.inventory.itemInHand
                        event.player.inventory.remove(event.player.inventory.itemInHand)
                        b.amount -= 1
                        event.player.inventory.itemInHand = b

                        event.isCancelled = true
                    } else {
                        event.isCancelled = true
                        event.player.sendMessage(TxtUtil.parse(getConf().msgLocationContainsMinion))
                        return
                    }
                } else {
                    event.isCancelled = true
                    return
                }
            } else if(NBT.getItem(event.item) == ItemType.LINK) {
                if(event.clickedBlock.type == Material.CHEST) {
                    event.isCancelled = true
                    event.player.updateInventory()
                    if (!Permission.OVERRIDE.has(event.player) && IntegrationSuperiorSkyblock.get().isActive) {
                        if (!EngineSuperiorSkyblock.get().canPlaceMinion(event.player, event.clickedBlock.location)) {
                            event.isCancelled = true
                            event.player.sendMessage(TxtUtil.parse(getConf().msgIslandPermDenyUse))
                            return
                        }
                    }
                    val link = event.player.itemInHand
                    val loc = PS.valueOf(event.clickedBlock)
                    var link1 = getConf().minionChestLink.toItemStack("\$location", TxtUtil.parse("${loc.blockX}, ${loc.blockY}, ${loc.blockZ}"))
                    link1 = NBT.addCustom(link1, "OGMinionLinkLocation", "${loc.world}, ${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")

                    event.player.inventory.remove(event.player.inventory.itemInHand)
                    link.amount -= 1
                    event.player.inventory.itemInHand = link

                    link1 = NBT.addItem(link1, ItemType.LINK)


                    event.player.inventory.addItem(link1)
                    event.player.sendMessage(TxtUtil.parse(getConf().chestLinkConnected))
                } else {
                    event.isCancelled = true
                    event.player.sendMessage(TxtUtil.parse(getConf().msgLinkMustBeChest))
                }
            }
        }
    }

    @EventHandler
    fun minionEdit(event: PlayerArmorStandManipulateEvent) {
        val stand = event.rightClicked as ArmorStand
        if(stand.helmet == null || !NBT.isMinionEntity(stand)) return
        event.isCancelled = true
        val mp = MPlayerColl.get().get(event.player as Player)
        var minion = mp.getMinion(event.rightClicked.location)
        if (IntegrationSuperiorSkyblock.get().isActive) {
            if (!Permission.OVERRIDE.has(mp.sender) && !EngineSuperiorSkyblock.get().canUseMinion(event.player, event.rightClicked.location)) {
                event.isCancelled = true
                mp.msg(TxtUtil.parse(getConf().msgIslandPermDenyUse))
                return
            }
            if(minion == null) {
                minion = EngineSuperiorSkyblock.get().getMemMinion(event.rightClicked.location)
            }
        }
        if (minion != null) {
            MinionInteractGUI(mp.player,minion).open()
        }
    }

    @EventHandler
    fun minionDelete(event: EntityDamageByEntityEvent) {
        if(event.entityType == EntityType.ARMOR_STAND) {
            val stand = event.entity as ArmorStand
            if(stand.helmet == null || !NBT.isMinionEntity(stand)) return
            event.isCancelled = true
            val loc = event.entity.location
            if(event.damager !is Player) return
            val mp = MPlayerColl.get().get(event.damager as Player)
            var minion = MinionAPI.getMinion(loc)
            if (IntegrationSuperiorSkyblock.get().isActive) {
                if (!Permission.OVERRIDE.has(mp.player) && !EngineSuperiorSkyblock.get().canPickUpMinion(mp.player, loc)) {
                    event.isCancelled = true
                    mp.msg(TxtUtil.parse(getConf().msgIslandPermDenyPickUp))
                    return
                }
                if(minion == null) {
                    minion = EngineSuperiorSkyblock.get().getMemMinion(loc)
                }
            }
            if(minion != null) {
                if(!mp.player.inventory.contents.any { it == null }) {
                    mp.msg(TxtUtil.parse(getConf().msgInvIsFull))
                    return
                }
                mp.removeMinion(minion)
                mp.player.inventory.addItem(minion.spawnItem())
                if(minion.hasChestLink()) {
                    var link = getConf().minionChestLink.toItemStack("\$location", TxtUtil.parse("&c&lUnknown"))
                    link = NBT.addItem(link, ItemType.LINK)
                    mp.player.inventory.addItem(link)
                }
            } else {
                mp.msg(TxtUtil.parse(getConf().msgCantBreakOthersMinion))
                return
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val mPlayer = MPlayerColl.get().get(event.player)
        Bukkit.getScheduler().runTaskLater(LITMinionsPlugin.get(),{
            mPlayer.getMinionList().forEach { it.spawn() }
        }, 20L)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val mPlayer = MPlayerColl.get().get(event.player)
        mPlayer.getMinionList().forEach { it.despawn() }
    }
}

class MinionEngine : Engine() {
    private val minionHandlerMap: MassiveMap<String, MinionHandler> = MassiveMap()
    private val minionEntityMap: MassiveMap<String, Minion> = MassiveMap()
    fun getHandler(id: String): MinionHandler? {
        return minionHandlerMap.getOrDefault(id, null)
    }

    fun getConf(id: String): Minion? {
        return minionEntityMap.getOrDefault(id, null)
    }

    fun register(minionConf: Minion, minionHandler: MinionHandler) {
        minionHandlerMap[minionConf.id] = minionHandler
        minionEntityMap[minionConf.id] = minionConf
    }

    companion object {
        @JvmStatic
        private val i = MinionEngine()
        @JvmStatic
        fun get(): MinionEngine {
            return i
        }
    }
}