package me.drman.litminions

import com.massivecraft.massivecore.ps.PS
import me.drman.litminions.api.MinionAPI
import me.drman.litminions.colls.MPlayerColl
import me.drman.litminions.colls.getConf
import me.drman.litminions.events.MinionEngine
import me.drman.litminions.extra.*
import me.drman.litminions.integrations.EngineHoloDisplay
import me.drman.litminions.integrations.IntegrationHoloDisplay
import me.drman.litminions.tasks.StartAnimation
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.EulerAngle

data class MinionFood(
    var item: ItemStackWrapper,
    var amount: Long,
    var health: Boolean,
    var price: Long,
) {
    fun foodItem(): ItemStack {
        var item = item.toItemStack("")
        item = NBT.addType(item, getKey(getConf().minionFoodList, this)!!)
        return NBT.addItem(item, ItemType.FOOD)
    }
}

open class MinionEntity (
    private var location: PS,
    private var type: String,
    private var player: String,
    private var hunger: Long,
    private var health: Long
    ) {
    private var hasChestLink = false
    private var chestLocation: PS? = PS.NULL

    fun spawn() {
        val minionType = MinionEngine.get().getConf(type)!!
        val loc = location.asBukkitLocation()
        loc.yaw = location.yaw
        val stand: ArmorStand = Bukkit.getWorld(location.world).spawn(loc, ArmorStand::class.java)

        stand.setArms(true)
        stand.setBasePlate(false)
        stand.setGravity(false)
        stand.isSmall = true

        val chest = ItemStack(Material.LEATHER_CHESTPLATE)
        val legs = ItemStack(Material.LEATHER_LEGGINGS)
        val boots = ItemStack(Material.LEATHER_BOOTS)
        val meta = chest.itemMeta as LeatherArmorMeta
        meta.color = Color.fromRGB(minionType.armorColorRGB)
        chest.itemMeta = meta
        legs.itemMeta = meta
        boots.itemMeta = meta
        stand.chestplate = chest
        stand.leggings = legs
        stand.boots = boots

        val head = NBT.addMinionEntity(ItemStack(Material.SKULL_ITEM,1,3.toShort()))
        val meta1 = head.itemMeta as SkullMeta
        meta1.owner = player
        head.itemMeta = meta1
        stand.helmet = head

        stand.itemInHand = ItemStack(minionType.handItem)
        StartAnimation.get().addMinion(this)

        if(IntegrationHoloDisplay.get().isActive) {
            EngineHoloDisplay.get().createHolo(this)
        }
    }

    fun despawn() {
        stopAllTask()
        if(IntegrationHoloDisplay.get().isActive) {
            EngineHoloDisplay.get().removeHolo(this)
        }
        val stand: ArmorStand? = Bukkit.getWorld(location.world).getNearbyEntities(location.asBukkitLocation(), 0.5, 0.5, 0.5).firstOrNull { it.type == EntityType.ARMOR_STAND } as ArmorStand?
        stand?.remove()
    }

    private fun stopAllTask() {
        StartAnimation.get().removeMinion(this)
        if(animationTaskID != -1) Bukkit.getScheduler().cancelTask(animationTaskID)
        if(healthDepletionTask != -1) Bukkit.getScheduler().cancelTask(healthDepletionTask)
    }

    private fun getArmorStand() = Bukkit.getWorld(location.world).getNearbyEntities(location.asBukkitLocation(), 0.5, 0.5, 0.5).firstOrNull { it.type == EntityType.ARMOR_STAND } as ArmorStand?

    @Transient
    private var animationStatus = false
    @Transient
    private var animationProgress = 0
    @Transient
    private var animationTaskID = -1

    fun performAnimation() {
        if(startAnimationStatus) {
            return
        }
        if(!animationStatus) {
            animationStatus = true
            animationTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(LITMinionsPlugin.get(), {
                performAnimation()
            }, 2L, MinionEngine.get().getConf(type)!!.animationSpeed)
        } else {
            if(animationProgress > 25) {
                animationStatus = false
                animationProgress = 0
                if(animationTaskID != -1) Bukkit.getScheduler().cancelTask(animationTaskID)
                animationTaskID = -1
            }
            getArmorStand()?.rightArmPose = Animation.rightHandMovement[animationProgress]
            animationProgress++
        }
    }
    @Transient
    private var startAnimationStatus = false
    @Transient
    private var startAnimationProgress = 0

    internal fun startAnimation() {
        if(!startAnimationStatus) startAnimationStatus = true
        else {
            if(startAnimationProgress > 18) {
                startAnimationStatus = false
                startAnimationProgress = 0
                StartAnimation.get().removeMinion(this)
            }
            getArmorStand()?.rightArmPose = Animation.rightArm[startAnimationProgress]
            getArmorStand()?.leftArmPose = Animation.leftArm[startAnimationProgress]
            getArmorStand()?.headPose = Animation.head[startAnimationProgress]
            startAnimationProgress++
        }
    }

    @Transient
    private var healthDepletionTask = -1

    fun startDeathClock() {
        if(hunger <= 0) {
            if(healthDepletionTask == -1) {
                Bukkit.getPlayer(player).sendMessage(TxtUtil.parseAndReplace(getConf().msgMinionOutOfHunger, "\$location", "${location.blockX}, ${location.blockY}, ${location.blockZ}"))
                healthDepletionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(LITMinionsPlugin.get(), {
                    if(health <= 0) {
                        despawn()
                        MPlayerColl.get().get(Bukkit.getPlayer(player)).removeMinion(this)
                        if(healthDepletionTask != -1) Bukkit.getScheduler().cancelTask(healthDepletionTask)
                        healthDepletionTask = -1
                        Bukkit.getPlayer(player).sendMessage(TxtUtil.parse(getConf().msgMinionDied))
                    }
                    if(hunger <= 0) {
                        health--
                    } else {
                        if(healthDepletionTask != -1) Bukkit.getScheduler().cancelTask(healthDepletionTask)
                        healthDepletionTask = -1
                    }
                }, MinionAPI.getMinionConf(type).timeBetweenHealthLostTicks, MinionAPI.getMinionConf(type).timeBetweenHealthLostTicks)
            }
        }
    }

    fun getLocation() = location
    fun getMinionType() = type
    fun getHealth() = health
    fun setHealth(num: Long) {
        health = num
    }
    fun getHunger() = hunger
    fun setHunger(num: Long) {
        hunger = num
    }
    fun hasChestLink() = hasChestLink
    fun setChestLink(set: Boolean) {
        hasChestLink = set
    }
    fun getChestInv(): Inventory? {
        return if(hasChestLink) {
            if(chestLocation!!.asBukkitBlock().type != Material.CHEST) null
            else {
                (chestLocation?.asBukkitBlock()?.state as Chest).blockInventory ?:null
            }
        } else null
    }
    fun setChestLoc(loc: PS) {
        chestLocation = loc
        MPlayerColl.get().get(Bukkit.getPlayer(player)).changed()
    }
    fun getChestLoc() = chestLocation

    fun spawnItem(): ItemStack {
        var item = MinionEngine.get().getConf(type)!!.itemWrapper.toItemStack("")
        item = NBT.addCustom(item, "minionHealth", health.toString())
        item = NBT.addCustom(item, "minionHunger", hunger.toString())
        item = NBT.addItem(item, ItemType.MINION)
        return NBT.addType(item, type)
    }
}

private class Animation {
    companion object {
        internal val rightArm = arrayOf(
            EulerAngle(0.0, 0.0, 0.0),
            EulerAngle(Math.toRadians(-10.0), Math.toRadians(3.75), 0.0),
            EulerAngle(Math.toRadians(-25.0), Math.toRadians(7.5), 0.0),
            EulerAngle(Math.toRadians(-40.0), Math.toRadians(11.25), 0.0),
            EulerAngle(Math.toRadians(-55.0), Math.toRadians(15.0), 0.0),
            EulerAngle(Math.toRadians(-70.0), Math.toRadians(18.75), 0.0),
            EulerAngle(Math.toRadians(-85.0), Math.toRadians(22.5), 0.0),
            EulerAngle(Math.toRadians(-100.0), Math.toRadians(26.25), 0.0),
            EulerAngle(Math.toRadians(-115.0), Math.toRadians(30.0), 0.0),
            EulerAngle(Math.toRadians(-130.0), Math.toRadians(30.0), 0.0),
            EulerAngle(Math.toRadians(-115.0), Math.toRadians(30.0), 0.0),
            EulerAngle(Math.toRadians(-100.0), Math.toRadians(26.25), 0.0),
            EulerAngle(Math.toRadians(-85.0), Math.toRadians(22.5), 0.0),
            EulerAngle(Math.toRadians(-70.0), Math.toRadians(18.75), 0.0),
            EulerAngle(Math.toRadians(-55.0), Math.toRadians(15.0), 0.0),
            EulerAngle(Math.toRadians(-40.0), Math.toRadians(11.25), 0.0),
            EulerAngle(Math.toRadians(-25.0), Math.toRadians(7.5), 0.0),
            EulerAngle(Math.toRadians(-10.0), Math.toRadians(3.75), 0.0),
            EulerAngle(0.0, 0.0, 0.0)
        )
        internal val leftArm = arrayOf(
            EulerAngle(0.0, 0.0, 0.0),
            EulerAngle(Math.toRadians(-10.0), Math.toRadians(-3.75), 0.0),
            EulerAngle(Math.toRadians(-25.0), Math.toRadians(-7.5), 0.0),
            EulerAngle(Math.toRadians(-40.0), Math.toRadians(-11.25), 0.0),
            EulerAngle(Math.toRadians(-55.0), Math.toRadians(-15.0), 0.0),
            EulerAngle(Math.toRadians(-70.0), Math.toRadians(-18.75), 0.0),
            EulerAngle(Math.toRadians(-85.0), Math.toRadians(-22.5), 0.0),
            EulerAngle(Math.toRadians(-100.0), Math.toRadians(-26.25), 0.0),
            EulerAngle(Math.toRadians(-115.0), Math.toRadians(-30.0), 0.0),
            EulerAngle(Math.toRadians(-130.0), Math.toRadians(-30.0), 0.0),
            EulerAngle(Math.toRadians(-115.0), Math.toRadians(-30.0), 0.0),
            EulerAngle(Math.toRadians(-100.0), Math.toRadians(-26.25), 0.0),
            EulerAngle(Math.toRadians(-85.0), Math.toRadians(-22.5), 0.0),
            EulerAngle(Math.toRadians(-70.0), Math.toRadians(-18.75), 0.0),
            EulerAngle(Math.toRadians(-55.0), Math.toRadians(-15.0), 0.0),
            EulerAngle(Math.toRadians(-40.0), Math.toRadians(-11.25), 0.0),
            EulerAngle(Math.toRadians(-25.0), Math.toRadians(-7.5), 0.0),
            EulerAngle(Math.toRadians(-10.0), Math.toRadians(-3.75), 0.0),
            EulerAngle(0.0, 0.0, 0.0)
        )
        internal val head = arrayOf(
            EulerAngle(0.0, 0.0, 0.0),
            EulerAngle(Math.toRadians(-3.75), 0.0, 0.0),
            EulerAngle(Math.toRadians(-7.5), 0.0, 0.0),
            EulerAngle(Math.toRadians(-11.25), 0.0, 0.0),
            EulerAngle(Math.toRadians(-15.0), 0.0, 0.0),
            EulerAngle(Math.toRadians(-18.75), 0.0, 0.0),
            EulerAngle(Math.toRadians(-22.5), 0.0, 0.0),
            EulerAngle(Math.toRadians(-26.25), 0.0, 0.0),
            EulerAngle(Math.toRadians(-30.0), 0.0, 0.0),
            EulerAngle(Math.toRadians(-30.0), 0.0, 0.0),
            EulerAngle(Math.toRadians(-30.0), 0.0, 0.0),
            EulerAngle(Math.toRadians(-26.25), 0.0, 0.0),
            EulerAngle(Math.toRadians(-22.5), 0.0, 0.0),
            EulerAngle(Math.toRadians(-18.75), 0.0, 0.0),
            EulerAngle(Math.toRadians(-15.0), 0.0, 0.0),
            EulerAngle(Math.toRadians(-11.25), 0.0, 0.0),
            EulerAngle(Math.toRadians(-7.5), 0.0, 0.0),
            EulerAngle(Math.toRadians(-3.75), 0.0, 0.0),
            EulerAngle(0.0, 0.0, 0.0)
        )
        internal val rightHandMovement = arrayOf(
            EulerAngle(5.6, 0.0, 0.0),
            EulerAngle(5.5, 0.0, 0.0),
            EulerAngle(5.4, 0.0, 0.0),
            EulerAngle(5.3, 0.0, 0.0),
            EulerAngle(5.2, 0.0, 0.0),
            EulerAngle(5.1, 0.0, 0.0),
            EulerAngle(5.0, 0.0, 0.0),
            EulerAngle(4.9, 0.0, 0.0),
            EulerAngle(4.8, 0.0, 0.0),
            EulerAngle(4.7, 0.0, 0.0),
            EulerAngle(4.6, 0.0, 0.0),
            EulerAngle(4.5, 0.0, 0.0),
            EulerAngle(4.4, 0.0, 0.0),
            EulerAngle(4.3, 0.0, 0.0),
            EulerAngle(4.5, 0.0, 0.0),
            EulerAngle(4.6, 0.0, 0.0),
            EulerAngle(4.7, 0.0, 0.0),
            EulerAngle(4.8, 0.0, 0.0),
            EulerAngle(4.9, 0.0, 0.0),
            EulerAngle(5.0, 0.0, 0.0),
            EulerAngle(5.1, 0.0, 0.0),
            EulerAngle(5.2, 0.0, 0.0),
            EulerAngle(5.3, 0.0, 0.0),
            EulerAngle(5.4, 0.0, 0.0),
            EulerAngle(5.5, 0.0, 0.0),
            EulerAngle(5.6, 0.0, 0.0)
        )
    }
}