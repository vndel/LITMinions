package me.drman.litminions.gui

import com.massivecraft.massivecore.money.Money
import com.massivecraft.massivecore.ps.PS
import com.massivecraft.massivecore.util.MUtil
import me.drman.litminions.entities.MPlayer
import me.drman.litminions.MinionEntity
import me.drman.litminions.api.MinionAPI
import me.drman.litminions.colls.getConf
import me.drman.litminions.extra.*
import me.drman.litminions.extra.gui.types.RefreshGui
import org.bukkit.Material
import org.bukkit.entity.Player

class MinionShopGUI(player: Player) : RefreshGui(player, getConf().minionShopGui) {
    override fun getRefreshTicks(): Long {
        return 0
    }

    override fun refresh() {
        if(inventory == null) return
        getConf().minionShopGui.updateGuiDisplay(this.inventory, "")

        getConf().minionShopButtons.forEach {
            val a = it.action
            val action = try {
                Actions.valueOf(a)
            } catch (e: IllegalArgumentException) { null }
            when(action) {
                Actions.EGG -> {
                    if(it.args.isEmpty()) {
                        inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lMissing Arg",MUtil.list("&7MinionID")).toItemStack(""))
                        return@forEach
                    }
                    val minion = MinionAPI.getMinionConf(it.args[0])
                    if(minion == null) {
                        inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lUnknown Arg", MUtil.list("&7FoodID")).toItemStack(""))
                        return@forEach
                    }
                    this.setClickable(it.slot) {
                        if(Money.despawn(player, null, minion.price.toDouble())) {
                            if(!player.inventory.contents.any { a -> a == null }) {
                                player.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                                return@setClickable
                            }
                            player.inventory.addItem(minion.spawnItem())
                        } else {
                            player.sendMessage(TxtUtil.parse(getConf().msgNotEnoughFunds))
                        }
                    }
                    inventory.setItem(it.slot, it.displayItem.toItemStack("\$price", NumberUtil.format(minion.price.toDouble())))
                }
                Actions.FOOD -> {
                    if(it.args.isEmpty()) {
                        inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lMissing Arg", MUtil.list("&7FoodID")).toItemStack(""))
                        return@forEach
                    }
                    val food = getConf().minionFoodList[it.args[0]]
                    if(food == null) {
                        inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lUnknown Arg", MUtil.list("&7FoodID")).toItemStack(""))
                        return@forEach
                    }
                    this.setClickable(it.slot) {
                        if(Money.despawn(player, null, food.price.toDouble())) {
                            if(!player.inventory.contents.any { a -> a == null }) {
                                player.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                                return@setClickable
                            }
                            player.inventory.addItem(food.foodItem())
                        } else {
                            player.sendMessage(TxtUtil.parse(getConf().msgNotEnoughFunds))
                        }
                    }
                    inventory.setItem(it.slot, it.displayItem.toItemStack("\$price", NumberUtil.format(food.price.toDouble())))
                }
                Actions.LINK -> {
                    this.setClickable(it.slot) {
                        if(Money.despawn(player, null, getConf().minionChestLinkPrice.toDouble())) {
                            if(!player.inventory.contents.any { a -> a == null }) {
                                player.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                                return@setClickable
                            }
                            player.inventory.addItem(NBT.addItem(getConf().minionChestLink.toItemStack("\$location", "&c&lUnknown"), ItemType.LINK))
                        } else {
                            player.sendMessage(TxtUtil.parse(getConf().msgNotEnoughFunds))
                        }
                    }
                    inventory.setItem(it.slot, it.displayItem.toItemStack("\$price", NumberUtil.format(getConf().minionChestLinkPrice.toDouble())))
                }
                else -> inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lUnknown Action").toItemStack(""))
            }
        }
    }
    enum class Actions {
        EGG,
        FOOD,
        LINK
    }
}

class MinionInteractGUI(player: Player, private var minion: MinionEntity) : RefreshGui(player, getConf().minionInteractGui) {
    override fun isAllowBottomGuiClick(): Boolean {
        return true
    }

    override fun getRefreshTicks(): Long {
        return 0
    }

    override fun refresh() {
        if(inventory == null) return
        getConf().minionInteractGui.updateGuiDisplay(this.inventory, "")

        getConf().minionInteractButtons.forEach {
            val a = it.action
            val action = try {
                Actions.valueOf(a)
            } catch (e: IllegalArgumentException) { null }
            when(action) {
                Actions.NONE -> {
                    inventory.setItem(it.slot, it.displayItem.toItemStack(
                        "\$location", "${minion.getLocation().blockX},${minion.getLocation().blockY},${minion.getLocation().blockZ}",
                        "\$type", minion.getMinionType(),
                        "\$hunger", minion.getHunger().toString(),
                        "\$health", minion.getHealth().toString()
                    ))
                }
                Actions.FEED -> {
                    setClickable(it.slot) { e ->
                        val item = e.cursor
                        if(item != null && item.type != Material.AIR) {
                            if(NBT.hasMetadata(item) && NBT.getItem(item) == ItemType.FOOD) {
                                val foodConf = getConf().minionFoodList[NBT.getType(item)]
                                val minionConf = MinionAPI.getMinionConf(minion.getMinionType())
                                if(foodConf != null && minionConf != null) {
                                    for(i in 1..item.amount) {
                                        if(foodConf.health) {
                                            if(minion.getHealth() == minionConf.health) break
                                            minion.setHealth(if(minion.getHealth() + foodConf.amount > minionConf.health) minionConf.health else minion.getHealth() + foodConf.amount)
                                        } else {
                                            if(minion.getHunger() == minionConf.hunger) break
                                            minion.setHunger(if(minion.getHunger() + foodConf.amount > minionConf.hunger) minionConf.hunger else minion.getHunger() + foodConf.amount)
                                        }
                                        item.amount--
                                    }
                                    player.inventory.addItem(item)
                                    item.amount = 0
                                    e.cursor = item
                                    MinionInteractGUI(player, minion).open()
                                }
                            }
                        } else player.sendMessage(TxtUtil.parse(getConf().msgMustHaveFoodInHand))
                    }
                    inventory.setItem(it.slot, it.displayItem.toItemStack(""))
                }
                Actions.LINK -> {
                    val conf = MinionAPI.getMinionConf(minion.getMinionType()) ?: return@forEach
                    setClickable(it.slot) { e ->
                        if(!conf.canHaveChestLink) {
                            return@setClickable
                        }
                        val item = e.cursor
                        if(item != null && item.type != Material.AIR) {
                            if(NBT.hasMetadata(item) && NBT.getItem(item) == ItemType.LINK) {
                                if(NBT.hasCustom(item, "OGMinionLinkLocation")) {
                                    if(minion.hasChestLink()) {
                                        var link = getConf().minionChestLink.toItemStack("\$location", TxtUtil.parse("&c&lUnknown"))
                                        link = NBT.addItem(link, ItemType.LINK)
                                        player.player.inventory.addItem(link)
                                    }
                                    val locString = NBT.getCustom(item, "OGMinionLinkLocation")
                                    val xyzList = locString.split(", ")
                                    //world, x, y, z
                                    val blockPS = PS.valueOf(xyzList[0], xyzList[1].toInt(), xyzList[2].toInt(), xyzList[3].toInt(),
                                        xyzList[1].toDouble(),xyzList[2].toDouble(),xyzList[3].toDouble(),0,0,0f,0f, 0.0,0.0,0.0)
                                    minion.setChestLink(true)
                                    minion.setChestLoc(blockPS)
                                    item.amount--
                                    e.cursor = item
                                    MinionInteractGUI(player, minion).open()
                                } else player.sendMessage(TxtUtil.parse(getConf().msgLinkMustBeLinked))
                            } else player.sendMessage(TxtUtil.parse(getConf().msgMustHaveLinkInHand))
                        } else player.sendMessage(TxtUtil.parse(getConf().msgMustHaveLinkInHand))
                    }
                    inventory.setItem(it.slot,
                        if(conf.canHaveChestLink) it.displayItem.toItemStack("\$location", if(minion.hasChestLink()) "${minion.getChestLoc()?.blockX}, ${minion.getChestLoc()?.blockY}, ${minion.getChestLoc()?.blockZ}" else "&c&lUnknown")
                        else getConf().cannotHaveLinkItem.toItemStack(""))
                }
                else -> inventory.setItem(it.slot, ItemStackWrapper(Material.BARRIER, 1, 0, "&4&lUnknown Action").toItemStack(""))
            }
        }
    }
    enum class Actions {
        LINK,
        FEED,
        NONE
    }
}