package me.sul.crackshotaddition.weaponappearance;

import com.shampaggon.crackshot.CSDirector;
import de.tr7zw.nbtapi.NBTItem;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WeaponDisplayNameFixation {
    private final String NBT_FOR_NAME_FIXATION = "NbtForNameFixation";
    private boolean bool = true;
    public WeaponDisplayNameFixation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                bool = !bool;
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    ItemStack mainIs = p.getInventory().getItemInMainHand();
                    if (CrackShotAdditionAPI.isValidCrackShotWeapon(mainIs)) {
                        NBTItem nbti = new NBTItem(mainIs);
                        nbti.setBoolean(NBT_FOR_NAME_FIXATION, bool);
                        mainIs.setItemMeta(nbti.getItem().getItemMeta());
                    }
                }
            }
        }.runTaskTimer(CSDirector.getInstance(), 20L, 20L);
    }
}
