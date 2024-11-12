package me.drman.litminions.integrations

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege
import com.bgsoftware.wildstacker.api.WildStackerAPI
import com.gmail.filoghost.holographicdisplays.`object`.line.CraftTextLine
import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.massivecraft.massivecore.Engine
import com.massivecraft.massivecore.Integration
import com.massivecraft.massivecore.ps.PS
import me.drman.litminions.MinionEntity
import me.drman.litminions.LITMinionsPlugin
import me.drman.litminions.colls.MPlayerColl
import me.drman.litminions.colls.getConf
import me.drman.litminions.extra.TxtUtil
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class IntegrationSuperiorSkyblock: Integration() {
    companion object {
        @JvmStatic
        private val INST = IntegrationSuperiorSkyblock()
        @JvmStatic
        fun get(): IntegrationSuperiorSkyblock { return INST }
    }

    init {
        this.setPluginName("SuperiorSkyblock2")
    }

    override fun getEngine(): Engine {
        return EngineSuperiorSkyblock.get()
    }
}

class EngineSuperiorSkyblock: Engine() {
    companion object {
        @JvmStatic
        private val INST = EngineSuperiorSkyblock()
        @JvmStatic
        fun get(): EngineSuperiorSkyblock { return INST }
    }

    override fun setActive(active: Boolean) {
        if(active) {
            IslandPrivilege.register("MINION_PLACE")
            IslandPrivilege.register("MINION_PICKUP")
            IslandPrivilege.register("MINION_USE")
            setPrivileges(
                IslandPrivilege.getByName("MINION_PLACE"),
                IslandPrivilege.getByName("MINION_PICKUP"),
                IslandPrivilege.getByName("MINION_USE"),
            )
        }
        super.setActive(active)
    }

    private fun setPrivileges(vararg priv: IslandPrivilege) {
        MINION_PLACE = priv[0]
        MINION_PICKUP = priv[1]
        MINION_USE = priv[2]
    }

    private lateinit var MINION_PLACE: IslandPrivilege
    private lateinit var MINION_PICKUP: IslandPrivilege
    private lateinit var MINION_USE: IslandPrivilege

    fun canPlaceMinion(player: Player, location: Location): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(location)
        return island?.hasPermission(SuperiorSkyblockAPI.getPlayer(player), MINION_PLACE) ?: false
    }

    fun canPickUpMinion(player: Player, location: Location): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(location)
        return island?.hasPermission(SuperiorSkyblockAPI.getPlayer(player), MINION_PICKUP) ?: false
    }

    fun canUseMinion(player: Player, location: Location): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(location)
        return island?.hasPermission(SuperiorSkyblockAPI.getPlayer(player), MINION_USE) ?: false
    }

    fun getMemMinion(location: Location): MinionEntity? {
        val island = SuperiorSkyblockAPI.getIslandAt(location)
        var minion: MinionEntity? = null
        island?.getIslandMembers(true)?.forEach {
            val mp = MPlayerColl.get().get(it.asOfflinePlayer())
            minion = mp.getMinionList().firstOrNull { _ -> it.location == location }
        }
        return minion
    }
}

class IntegrationWildStacker: Integration() {
    companion object {
        @JvmStatic
        private val INST = IntegrationWildStacker()
        @JvmStatic
        fun get(): IntegrationWildStacker { return INST }
    }

    init {
        this.setPluginName("WildStacker")
    }

    override fun getEngine(): Engine {
        return EngineWildStacker.get()
    }
}

class EngineWildStacker: Engine() {
    companion object {
        @JvmStatic
        private val INST = EngineWildStacker()
        @JvmStatic
        fun get(): EngineWildStacker { return INST }
    }

    fun killStackAmount(entity: LivingEntity, amount: Int): List<ItemStack> {
        val stack = WildStackerAPI.getStackedEntity(entity)
        stack.decreaseStackAmount(amount, true)
        return stack.getDrops(0,amount)
    }
}

class IntegrationHoloDisplay: Integration() {
    companion object {
        @JvmStatic
        private val INST = IntegrationHoloDisplay()
        @JvmStatic
        fun get(): IntegrationHoloDisplay { return INST }
    }

    init {
        this.setPluginName("HolographicDisplays")
    }

    override fun getEngine(): Engine {
        return EngineHoloDisplay.get()
    }
}

class EngineHoloDisplay: Engine() {
    companion object {
        @JvmStatic
        private val INST = EngineHoloDisplay()
        @JvmStatic
        fun get(): EngineHoloDisplay { return INST }
    }

    private val displayMap: MutableMap<PS, Hologram> = mutableMapOf()

    fun createHolo(entity: MinionEntity) {
        displayMap[entity.getLocation()] = HologramsAPI.createHologram(
            LITMinionsPlugin.get(), entity.getLocation().asBukkitLocation().add(
            Vector(0.0, 2.0, 0.0)
        ))
        displayMap[entity.getLocation()]!!.appendTextLine(TxtUtil.parseAndReplace(getConf().minionNameTag,
            "\$hunger", entity.getHunger().toString(),
            "\$health", entity.getHealth().toString(),
            "\$type", entity.getMinionType()
        ))
    }

    fun removeHolo(entity: MinionEntity) {
        displayMap[entity.getLocation()]!!.delete()
        displayMap.remove(entity.getLocation())
    }

    fun updateHolo(entity: MinionEntity) {
        val a = displayMap[entity.getLocation()]?.getLine(0) ?: return
        if(a is CraftTextLine) {
            a.text = (
                TxtUtil.parseAndReplace(
                    getConf().minionNameTag,
                    "\$hunger", entity.getHunger().toString(),
                    "\$health", entity.getHealth().toString(),
                    "\$type", entity.getMinionType()
                )
            )
        }
    }
}