package me.drman.litminions.commands

import com.massivecraft.massivecore.Identified
import com.massivecraft.massivecore.command.MassiveCommand
import com.massivecraft.massivecore.command.type.Type
import com.massivecraft.massivecore.command.type.TypeAbstract
import com.massivecraft.massivecore.command.type.primitive.TypeInteger
import com.massivecraft.massivecore.pager.Msonifier
import com.massivecraft.massivecore.pager.Pager
import com.massivecraft.massivecore.util.PermissionUtil
import me.drman.litminions.*
import me.drman.litminions.colls.MPlayerColl
import me.drman.litminions.colls.MinionColl
import me.drman.litminions.colls.getConf
import me.drman.litminions.entities.MPlayer
import me.drman.litminions.entities.Minion
import me.drman.litminions.events.MinionEngine
import me.drman.litminions.extra.ItemType
import me.drman.litminions.extra.NBT
import me.drman.litminions.extra.TxtUtil
import me.drman.litminions.extra.Util
import me.drman.litminions.gui.MinionShopGUI
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permissible

open class MinionCommand: MassiveCommand() {
    init {
        this.isSetupEnabled = true
        this.setSetupPermClass(Permission::class.java)
    }

    protected var senderMPlayer: MPlayer? = null

    override fun senderFields(set: Boolean) {
        senderMPlayer = if(this.senderIsConsole) {
            null
        } else {
            if(set) MPlayerColl.get().get(sender) else null
        }
    }
}

class TypeMPlayer {
    companion object {
        @JvmStatic
        fun get(): Type<MPlayer> {
            return MPlayerColl.get().typeEntity
        }
    }
}

class TypeMinion: TypeAbstract<Minion>(Minion::class.java) {
    companion object {
        @JvmStatic
        private var INST = TypeMinion()
        @JvmStatic
        fun get(): TypeMinion { return INST }
    }

    override fun read(s: String, commandSender: CommandSender): Minion? {
        return MinionEngine.get().getConf(s)
    }

    override fun getTabList(commandSender: CommandSender, s: String): Collection<String> {
        return MinionColl.get().ids
    }
}

class TypeMinionItem: TypeAbstract<String>(String::class.java) {
    companion object {
        @JvmStatic
        private var INST = TypeMinionItem()
        @JvmStatic
        fun get(): TypeMinionItem { return INST }
    }

    override fun read(s: String, commandSender: CommandSender): String {
        return s
    }

    override fun getTabList(commandSender: CommandSender, s: String): Collection<String> {
        return getConf().minionFoodList.keys + "link"
    }
}

open class CmdMinions: MinionCommand() {
    companion object {
        @JvmStatic
        private var INST = CmdMinions()
        @JvmStatic
        fun get(): CmdMinions { return INST }
    }
    //Commands
    val cmdMinionsRemove: CmdMinionsRemove = CmdMinionsRemove()
    val cmdMinionsGive: CmdMinionsGive = CmdMinionsGive()
    val cmdMinionsInfo: CmdMinionsInfo = CmdMinionsInfo()
    val cmdMinionsShop: CmdMinionsShop = CmdMinionsShop()
    val cmdMinionsGiveItem: CmdMinionsGiveItem = CmdMinionsGiveItem()
    //Overrides
    override fun getAliases(): MutableList<String> {
        return mutableListOf("minions", "litminions")
    }
}

class CmdMinionsGive: MinionCommand() {
    init {
        this.setDesc("Gives the player a minion spawn item.")
        this.addParameter(TypeMPlayer.get(), "player")
        this.addParameter(TypeMinion.get(), "minionType")
        this.addParameter(1, TypeInteger.get(), "amount")
    }

    override fun perform() {
        val player = this.readArg(senderMPlayer)
        val minionType = this.readArg<Minion>()
        val amount = this.readArg<Int>()
        if (player != null) {
            if(minionType != null) {
                val a = minionType.spawnItem()
                a.amount = amount
                if(!player.player.inventory.contents.any { it == null }) {
                    if(senderIsConsole) player.player.location.world.dropItem(player.player.location, a)
                    else {
                        sender.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                        return
                    }
                } else player.player.inventory.addItem(a)
                if (player == senderMPlayer) {
                    player.msg(TxtUtil.parse(getConf().msgGiveSelf))
                } else {
                    player.msg(TxtUtil.parse(getConf().msgGiveOther))
                    senderMPlayer?.msg(TxtUtil.parse(getConf().msgGiveSelf)) ?: return
                }
            } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownMinion))
        } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownPlayer))
    }
}

class CmdMinionsGiveItem: MinionCommand() {
    init {
        this.setDesc("Gives the player a minion item item.")
        this.addParameter(TypeMPlayer.get(), "player")
        this.addParameter(TypeMinionItem.get(), "minionItem")
        this.addParameter(1, TypeInteger.get(), "amount")
    }

    override fun perform() {
        val player = this.readArg<MPlayer>()
        val item = this.readArg<String>()
        val amount = this.readArg<Int>()
        if (player != null) {
            if(item == "link") {
                var link = getConf().minionChestLink.toItemStack("\$location", TxtUtil.parse("&c&lUnknown"))
                link.amount = amount
                link = NBT.addItem(link, ItemType.LINK)
                if(!player.player.inventory.contents.any { it == null }) {
                    if(senderIsConsole) player.player.location.world.dropItem(player.player.location, link)
                    else {
                        sender.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                        return
                    }
                } else player.player.inventory.addItem(link)
                if (player == senderMPlayer) {
                    player.msg(TxtUtil.parse(getConf().msgGiveSelf))
                } else {
                    player.msg(TxtUtil.parse(getConf().msgGiveOther))
                    senderMPlayer?.msg(TxtUtil.parse(getConf().msgGiveSelf)) ?: return
                }
            } else if(getConf().minionFoodList.containsKey(item)) {
                val foodConf = getConf().minionFoodList[item]
                val foodItem = foodConf!!.foodItem()
                foodItem.amount = amount
                if(!player.player.inventory.contents.any { it == null }) {
                    if(senderIsConsole) player.player.location.world.dropItem(player.player.location, foodItem)
                    else {
                        sender.sendMessage(TxtUtil.parse(getConf().msgInvIsFull))
                        return
                    }
                } else player.player.inventory.addItem(NBT.addType(foodItem, item))
                if (player == senderMPlayer) {
                    player.msg(TxtUtil.parse(getConf().msgGiveSelf))
                } else {
                    player.msg(TxtUtil.parse(getConf().msgGiveOther))
                    senderMPlayer?.msg(TxtUtil.parse(getConf().msgGiveSelf)) ?: return
                }
            } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownMinionItem))
        } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownPlayer))
    }
}

class CmdMinionsRemove: MinionCommand() {
    init {
        this.setDesc("Removes a player's specific or all minions.")
        this.addParameter(TypeMPlayer.get(), "player")
        this.addParameter(-1, TypeInteger.get(), "minionID")
    }

    override fun perform() {
        val player: MPlayer? = this.readArg(senderMPlayer)
        val minionID: Int = this.readArg()
        if (player != null) {
            if(player.listSize() == 0) {
                senderMPlayer?.msg(TxtUtil.parse(getConf().msgHasNoMinion)) ?: return
                return
            }
            player.removeMinion(minionID)
            if(player == senderMPlayer) {
                player.msg(TxtUtil.parse(getConf().msgRemoveSelf))
            } else {
                player.msg(TxtUtil.parse(getConf().msgRemoveOther))
                senderMPlayer?.msg(TxtUtil.parse(getConf().msgRemoveSelf)) ?: return
            }
        } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownPlayer))
    }
}

class CmdMinionsInfo: MinionCommand() {
    init {
        this.setDesc("Shows a player's minion locations.")
        this.addParameter(senderMPlayer, TypeMPlayer.get(), "player")
        this.addParameter(1, TypeInteger.get(), "page")
    }

    override fun perform() {
        val player = this.readArg(senderMPlayer)
        val page = this.readArg<Int>()
        if(player != null) {
            val pager = Pager(this, "Minion Info", page, player.getMinionList(), Msonifier { t: MinionEntity, i ->
                var line = getConf().minionsInfoFormat

                if(line.contains("\$id")) {
                    line = line.replace("\$id",i.toString())
                }
                if(line.contains("\$location")) {
                    val loc = t.getLocation()
                    line = line.replace("\$location","${loc.blockX},${loc.blockY},${loc.blockZ}")
                }
                if(line.contains("\$type")) {
                    line = line.replace("\$type",t.getMinionType())
                }
                if(line.contains("\$hunger")) {
                    line = line.replace("\$hunger",t.getHunger().toString())
                }
                if(line.contains("\$health")) {
                    line = line.replace("\$health",t.getHealth().toString())
                }

                return@Msonifier mson(TxtUtil.parse(line))
            })
            pager.setArgs(player.name, page.toString())
            if(!senderIsConsole && player == senderMPlayer) pager.message()
            else {
                if (!senderIsConsole && !Permission.INFO_OTHER.has(sender)) {
                    sender.sendMessage(TxtUtil.parse("&cmust have &fother &cpermisisons node to do that."))
                    return
                }
                pager.message()
            }
        } else sender.sendMessage(TxtUtil.parse(getConf().msgUnknownPlayer))
    }
}

class CmdMinionsShop: MinionCommand() {
    init {
        this.setDesc("Open the minion shop.")
        this.addParameter(senderMPlayer, TypeMPlayer.get(), "player")
    }

    override fun perform() {
        val player = this.readArg(senderMPlayer)
        if(player != null) {
            if(!senderIsConsole && player == senderMPlayer) MinionShopGUI(senderMPlayer!!.player).open()
            else {
                if(!senderIsConsole && !Permission.SHOP_OTHER.has(sender)) {
                    sender.sendMessage(TxtUtil.parse("&cmust have &fother &cpermisisons node to do that."))
                    return
                }
                MinionShopGUI(player.player).open()
            }
        }
    }
}

enum class Permission: Identified {
    BASECOMMAND,

    //Command Perms
    GIVE,
    GIVE_OTHER,
    GIVE_ITEM,
    REMOVE,
    INFO,
    INFO_OTHER,
    SHOP,
    SHOP_OTHER,

    //Action Perms
    OVERRIDE
    ;

    private var id: String = PermissionUtil.createPermissionId(LITMinionsPlugin.get(), this)
    override fun getId(): String {
        return id
    }

    open fun has(permissible: Permissible?, verbose: Boolean): Boolean {
        return PermissionUtil.hasPermission(permissible, this, verbose)
    }

    open fun has(permissible: Permissible?): Boolean {
        return PermissionUtil.hasPermission(permissible, this)
    }
}

