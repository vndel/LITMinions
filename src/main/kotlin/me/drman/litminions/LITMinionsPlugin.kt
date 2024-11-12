package me.drman.litminions

import com.massivecraft.massivecore.MassivePlugin
import me.drman.litminions.colls.ConfigColl
import me.drman.litminions.colls.MPlayerColl
import me.drman.litminions.colls.MinionColl
import me.drman.litminions.colls.getConf
import me.drman.litminions.commands.CmdMinions
import me.drman.litminions.entities.handlers.*
import me.drman.litminions.events.Events
import me.drman.litminions.events.MinionEngine
import me.drman.litminions.extra.EngineGui
import me.drman.litminions.extra.getAllOnlineMinions
import me.drman.litminions.integrations.IntegrationHoloDisplay
import me.drman.litminions.integrations.IntegrationSuperiorSkyblock
import me.drman.litminions.integrations.IntegrationWildStacker
import me.drman.litminions.tasks.*
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider

class LITMinionsPlugin: MassivePlugin() {
    init {
        inst = this
    }

    companion object {
        private lateinit var inst: LITMinionsPlugin
        @JvmStatic
        fun get(): LITMinionsPlugin { return inst }
    }

    override fun onEnableInner() {
        activate(
            MPlayerColl::class.java,
            ConfigColl::class.java,
            MinionColl::class.java,

            IntegrationSuperiorSkyblock::class.java,
            IntegrationWildStacker::class.java,
            IntegrationHoloDisplay::class.java,

            EngineGui::class.java,
            Events::class.java,
            MinionEngine::class.java,

            CmdMinions::class.java
        )
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)
        getAllOnlineMinions().forEach { it.despawn() }
        this.onDisable()
    }

    override fun onEnablePost() {
        MinionEngine.get().register(MinionColl.get().getMiner(), MinerHandler())
        MinionEngine.get().register(MinionColl.get().getButcher(), ButcherHandler())
        MinionEngine.get().register(MinionColl.get().getFeeder(), FeederHandler())
        MinionEngine.get().register(MinionColl.get().getHunter(), HunterHandler())
        MinionEngine.get().register(MinionColl.get().getReaper(), ReaperHandler())
        setupPermissions()
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MinerActionTask(), 200L, MinionEngine.get().getConf("miner")!!.actionSpeedTicks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, FeederActionTask(), 200L, MinionEngine.get().getConf("feeder")!!.actionSpeedTicks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ReaperActionTask(), 200L, MinionEngine.get().getConf("reaper")!!.actionSpeedTicks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, HunterActionTask(), 200L, MinionEngine.get().getConf("hunter")!!.actionSpeedTicks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ButcherActionTask(), 200L, MinionEngine.get().getConf("butcher")!!.actionSpeedTicks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, StartAnimation.get(), 25L, 2L)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, HoloUpdateTask(), getConf().minionNameTagUpdateTick, getConf().minionNameTagUpdateTick)
        super.onEnablePost()
    }

    lateinit var perms: Permission

    private fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission> = this.server.servicesManager.getRegistration(
            Permission::class.java)
        perms = rsp.provider
        return true
    }
}