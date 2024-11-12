package me.drman.litminions.entities

import com.massivecraft.massivecore.store.Entity
import com.massivecraft.massivecore.store.SenderEntity
import com.massivecraft.massivecore.util.MUtil
import me.drman.litminions.MinionEntity
import me.drman.litminions.extra.ItemStackWrapper
import me.drman.litminions.extra.ItemType
import me.drman.litminions.extra.NBT
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MPlayer: SenderEntity<MPlayer>() {
    private var minionList: MutableList<MinionEntity> = mutableListOf()

    fun addMinion(type: MinionEntity) {
        minionList.add(type)
        this.changed()
    }

    fun removeMinion(obj: Any) {
        if(obj is Int) {
            minionList[obj].despawn()
            minionList.removeAt(obj)
        }
        else if(obj is MinionEntity) {
            minionList[minionList.indexOf(obj)].despawn()
            minionList.remove(obj)
        }
        this.changed()
    }

    fun getMinion(location: Location): MinionEntity? {
        return minionList.firstOrNull{ it.getLocation().asBukkitLocation() == location }
    }

    fun listSize(): Int {
        return minionList.size
    }

    fun getMinionList(): MutableList<MinionEntity> {
        return minionList
    }
}

open class Minion(
    var itemWrapper: ItemStackWrapper = ItemStackWrapper(Material.MONSTER_EGG, 0, 0, "&eMinion Spawner", MUtil.list("do that")),
    var handItem: Material = Material.STICK,
    var aoeSize: Int = 3,
    var killStackAmount: Int = 1,
    var actionSpeedTicks: Long = 100,
    var animationSpeed: Long = 4,
    var health: Long = 100,
    var timeBetweenHealthLostTicks: Long = 1200,
    var hunger: Long = 1000,
    var price: Long = 1000,
    var armorColorRGB: Int = Color.WHITE.asRGB(),
    var canHaveChestLink: Boolean = true
): Entity<Minion>() {
    fun spawnItem(): ItemStack {
        var item = itemWrapper.toItemStack("")
        item = NBT.addCustom(item, "minionHealth", health.toString())
        item = NBT.addCustom(item, "minionHunger", hunger.toString())
        item = NBT.addItem(item, ItemType.MINION)
        return NBT.addType(item, this.id)
    }
}