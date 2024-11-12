package me.drman.litminions.colls

import com.massivecraft.massivecore.MassiveCore
import com.massivecraft.massivecore.store.Coll
import com.massivecraft.massivecore.store.SenderColl
import com.massivecraft.massivecore.util.MUtil
import me.drman.litminions.Config
import me.drman.litminions.entities.MPlayer
import me.drman.litminions.LITMinionsPlugin
import me.drman.litminions.entities.Minion
import me.drman.litminions.extra.ItemStackWrapper
import org.bukkit.Color
import org.bukkit.Material

open class MPlayerColl: SenderColl<MPlayer>("litminions_players") {
    companion object {
        @JvmStatic
        val inst = MPlayerColl()
        @JvmStatic
        fun get(): MPlayerColl { return inst }
    }
}

class MinionColl: Coll<Minion>("litminions_minionconf"){
    companion object {
        @JvmStatic
        private val INST = MinionColl()
        @JvmStatic
        fun get(): MinionColl { return INST }
    }

    fun getMiner(): Minion {
        return getOrCreate("miner", Minion(
            itemWrapper = ItemStackWrapper(
                Material.MONSTER_EGG,
                1, 96,
                "&4&lMining Minion",
                MUtil.list("&7Mines blocks in front of it.")),
            armorColorRGB = Color.RED.asRGB(),
            handItem = Material.DIAMOND_PICKAXE
        )
        )
    }

    fun getFeeder(): Minion {
        return getOrCreate("feeder", Minion(
            itemWrapper = ItemStackWrapper(
                Material.MONSTER_EGG,
                1, 101,
                "&7&lFeeder Minion",
                MUtil.list("&7Feed surrounding minions. Max hunger is &7100,000&8.")),
            aoeSize = 10,
            actionSpeedTicks = 600,
            hunger = 100000,
            price = 100000,
            armorColorRGB = Color.MAROON.asRGB(),
            handItem = Material.COOKED_BEEF,
            canHaveChestLink = false
        )
        )
    }

    fun getReaper(): Minion {
        return getOrCreate("reaper", Minion(
            itemWrapper = ItemStackWrapper(
                Material.MONSTER_EGG,
                1, 58,
                "&8&lReaper Minion",
                MUtil.list("&7Kills nearby hostile mobs and drops &aXP Bottles&7 instead of loot.")),
            actionSpeedTicks = 20,
            armorColorRGB = Color.BLACK.asRGB(),
            handItem = Material.DIAMOND_SWORD
        )
        )
    }

    fun getButcher(): Minion {
        return getOrCreate("butcher", Minion(
            itemWrapper = ItemStackWrapper(
                Material.MONSTER_EGG,
                1, 91,
                "&2&lButcher Minion",
                MUtil.list("&7Kills nearby passive mobs and drops loot.")),
            actionSpeedTicks = 20,
            armorColorRGB = Color.GREEN.asRGB(),
            handItem = Material.IRON_AXE
        )
        )
    }

    fun getHunter(): Minion {
        return getOrCreate("hunter", Minion(
            itemWrapper = ItemStackWrapper(
                Material.MONSTER_EGG,
                1, 50,
                "&a&lHunter Minion",
                MUtil.list("&7Kills nearby hostile mobs and drops loot.")),
            actionSpeedTicks = 20,
            armorColorRGB = Color.LIME.asRGB(),
            handItem = Material.IRON_SWORD
        )
        )
    }

    fun getOrCreate(id: String, default: Minion): Minion {
        var minion: Minion? = this.get(id, false)
        return if(minion != null) {
            minion
        } else {
            minion = default
            this.attach(minion, id)
            minion.sync()
            minion
        }
    }
}

fun getConf(): Config = Config.get()

class ConfigColl: Coll<Config>("litminions_config", Config::class.java, null, LITMinionsPlugin.get()) {
    companion object {
        @JvmStatic
        private val INST = ConfigColl()
        @JvmStatic
        fun get(): ConfigColl { return INST }
    }

    override fun setActive(active: Boolean) {
        super.setActive(active)
        if (!active) return
        Config.set(get(MassiveCore.INSTANCE, true))
    }
}