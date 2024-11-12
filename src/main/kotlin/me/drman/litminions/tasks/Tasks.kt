package me.drman.litminions.tasks

import com.massivecraft.massivecore.ps.PS
import me.drman.litminions.events.MinionEngine
import me.drman.litminions.MinionEntity
import me.drman.litminions.extra.getAllOnlineMinions
import me.drman.litminions.extra.getMinion
import me.drman.litminions.integrations.EngineHoloDisplay
import me.drman.litminions.integrations.IntegrationHoloDisplay

class MinerActionTask: Runnable {
    override fun run() {
        val handler = MinionEngine.get().getHandler("miner")!!
        getAllOnlineMinions().filter { it.getMinionType() == "miner" }.forEach {
            if(it.getMinionType() == "miner") handler.action(it)
        }
    }
}

class FeederActionTask: Runnable {
    override fun run() {
        val handler = MinionEngine.get().getHandler("feeder")!!
        getAllOnlineMinions().filter { it.getMinionType() == "feeder" }.forEach {
            if(it.getMinionType() == "feeder") handler.action(it)
        }
    }
}

class ReaperActionTask: Runnable {
    override fun run() {
        val handler = MinionEngine.get().getHandler("reaper")!!
        getAllOnlineMinions().filter { it.getMinionType() == "reaper" }.forEach {
            if(it.getMinionType() == "reaper") handler.action(it)
        }
    }
}

class HunterActionTask: Runnable {
    override fun run() {
        val handler = MinionEngine.get().getHandler("hunter")!!
        getAllOnlineMinions().filter { it.getMinionType() == "hunter" }.forEach {
            if(it.getMinionType() == "hunter") handler.action(it)
        }
    }
}

class ButcherActionTask: Runnable {
    override fun run() {
        val handler = MinionEngine.get().getHandler("butcher")!!
        getAllOnlineMinions().filter { it.getMinionType() == "butcher" }.forEach {
            if(it.getMinionType() == "butcher") handler.action(it)
        }
    }
}

class StartAnimation: Runnable {
    companion object {
        private val inst = StartAnimation()
        @JvmStatic
        fun get() = inst
    }

    private val minions: MutableList<PS> = mutableListOf()
    fun addMinion(entity: MinionEntity) {
        minions.add(entity.getLocation())
    }
    fun removeMinion(entity: MinionEntity) {
        minions.remove(entity.getLocation())
    }

    override fun run() {
        val a = mutableListOf<PS>()
        a.addAll(minions)
        a.forEach {
            val minion = getMinion(it)
            minion!!.startAnimation()
        }
    }
}

class HoloUpdateTask: Runnable {
    override fun run() {
        getAllOnlineMinions().forEach{
            if(IntegrationHoloDisplay.get().isActive) {
                EngineHoloDisplay.get().updateHolo(it)
            }
        }
    }
}