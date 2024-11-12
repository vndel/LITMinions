package me.drman.litminions.extra

import com.massivecraft.massivecore.ps.PS
import io.github.bananapuncher714.nbteditor.NBTEditor
import me.drman.litminions.MinionEntity
import me.drman.litminions.colls.MPlayerColl
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

fun createInventory(inventoryHolder: InventoryHolder, inventoryType: InventoryType, title: String): Inventory {
    return Bukkit.createInventory(inventoryHolder, inventoryType, title)
}

fun <K, V> getKey(map: Map<K, V>, value: V): K? {
    for ((key, value1) in map) {
        if (value1 == value) {
            return key
        }
    }
    return null
}

fun getMinion(location: Location): MinionEntity? {
    val locPS = PS.valueOf(location.block)
    return getAllMinions().firstOrNull{
        val loc = it.getLocation()
        loc.blockX == locPS.blockX && loc.blockY == locPS.blockY && loc.blockZ == locPS.blockZ && loc.world == locPS.world
    }
}

internal fun getMinion(location: PS): MinionEntity? {
    return getAllMinions().firstOrNull{
        it.getLocation() == location
    }
}

fun getAllMinions(): MutableList<MinionEntity> {
    val list = mutableListOf<MinionEntity>()
    MPlayerColl.get().all.forEach {
        list += it.getMinionList()
    }
    return list
}

fun getAllOnlineMinions(): MutableList<MinionEntity> {
    val list = mutableListOf<MinionEntity>()
    MPlayerColl.get().allOnline.forEach {
        list += it.getMinionList()
    }
    return list
}

enum class ItemType {
    MINION,
    LINK,
    FOOD
}

internal class Util {
    companion object {
        @JvmStatic
        fun approximateYam(num: Int): Float {
            return when (num) {
                in 45..134 -> 90f
                in -134..-45 -> 90f
                in 135..224 -> 180f
                in -224..-135 -> 180f
                in 225..314 -> 270f
                in -314..-225 -> 270f
                else -> 0f
            }
        }
        @JvmStatic
        fun getDirFromYam(num: Float) = when(num) {
            90f -> BlockFace.WEST
            180f -> BlockFace.NORTH
            270f -> BlockFace.EAST
            else -> BlockFace.SOUTH
        }
    }
}

class NBT {
    companion object {
        fun addType(item: ItemStack, type: String): ItemStack {
            return NBTEditor.set(item, type, "LITMinionsType")
        }

        fun addItem(item: ItemStack, type: ItemType): ItemStack {
            return NBTEditor.set(item, type.toString(), "LITMinionsItem")
        }

        fun addCustom(item: ItemStack, key: String, value: String): ItemStack {
            return NBTEditor.set(item, value, key)
        }

        fun getCustom(item: ItemStack?, key: String): String {
            return NBTEditor.getString(item, key)
        }

        fun hasCustom(item: ItemStack?, key: String): Boolean {
            return try {
                NBTEditor.contains(item, key)
            } catch (e: NullPointerException) {
                false
            }
        }

        fun hasMetadata(item: ItemStack?): Boolean {
            return try {
                NBTEditor.contains(item, "LITMinionsItem")
            } catch (e: NullPointerException) {
                false
            }
        }

        fun getType(item: ItemStack): String {
            return NBTEditor.getString(item, "LITMinionsType")
        }

        fun getItem(item: ItemStack): ItemType? {
            return try {
                ItemType.valueOf(NBTEditor.getString(item, "LITMinionsItem"))
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        fun addMinionEntity(item: ItemStack): ItemStack {
            return NBTEditor.set(item, 1, "MinionEntity")
        }

        fun isMinionEntity(stand: ArmorStand): Boolean {
            return try {
                NBTEditor.contains(stand.helmet, "MinionEntity")
            } catch (e: NullPointerException) {
                false
            }
        }
    }
}