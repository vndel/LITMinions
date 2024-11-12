package me.drman.litminions.api;

import me.drman.litminions.*;
import me.drman.litminions.colls.MPlayerColl;
import me.drman.litminions.colls.MinionColl;
import me.drman.litminions.entities.MPlayer;
import me.drman.litminions.entities.Minion;
import me.drman.litminions.events.MinionEngine;
import me.drman.litminions.extra.ExtraKt;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MinionAPI {
    private MinionAPI() {}

    public static MinionEntity getMinion(Location location) {
        return ExtraKt.getMinion(location);
    }

    public static MPlayer getAsMPLayer(OfflinePlayer player) {
        return MPlayerColl.get().get(player);
    }

    public static MPlayer getAsMPLayer(Player player) {
        return MPlayerColl.get().get(player);
    }

    public static List<MinionEntity> getAllOnlineMinions() {
        return ExtraKt.getAllOnlineMinions();
    }

    @NotNull
    public static List<MinionEntity> getAllMinions() {
        return ExtraKt.getAllMinions();
    }

    public static Minion getMinionConf(String type) {
        return MinionEngine.get().getConf(type);
    }

    public static void registerMinion(String id, Minion config, MinionHandler minionHandler) {
        Minion minionObj = MinionColl.get().getOrCreate(id, config);
        MinionEngine.get().register(minionObj, minionHandler);
    }
}
