package me.drman.litminions.extra.gui;

import com.massivecraft.massivecore.MassiveException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface IGuiTypeBuilder {

    Inventory build(Player player, String guiName, List<String> guiFormat, String... replacements) throws MassiveException;
    Inventory build(String guiName, List<String> guiFormat, String... replacements) throws MassiveException;

}
