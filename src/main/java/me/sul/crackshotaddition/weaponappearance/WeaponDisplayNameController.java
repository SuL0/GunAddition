package me.sul.crackshotaddition.weaponappearance;

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.MainCrackShotWeaponInfoMetaManager;
import me.sul.servercore.inventoryevent.InventoryItemChangedEvent;
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class WeaponDisplayNameController implements Listener {
    private static final String AMMO_ICON1 = "§f锄 "; // §f 없으면 색 이상해짐
    private static final int LEFTSIDE_LENGTH = 13;
    private static final int RIGHTSIDE_LENGTH = 14;


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMainItemChanged(PlayerMainItemChangedConsideringUidEvent e) {
        if (e.isChangedToCrackShotWeapon()) {
            updateMainWeaponDisplay(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShoot(WeaponShootEvent e) { updateMainWeaponDisplay(e.getPlayer()); }

    @EventHandler(priority = EventPriority.HIGH)
    public void onReloadComplete(WeaponReloadCompleteEvent e) { updateMainWeaponDisplay(e.getPlayer()); }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemChanged(InventoryItemChangedEvent e) {
        Player p = e.getPlayer();
        Material requiredAmmoMaterial = MainCrackShotWeaponInfoMetaManager.getAmmoItemMaterial(p);
        if (requiredAmmoMaterial != null && e.getItemStack().getType() == requiredAmmoMaterial) {
            updateMainWeaponDisplay(p);
        }
    }



    private void updateMainWeaponDisplay(Player p) {
        ItemStack is = MainCrackShotWeaponInfoMetaManager.getItemStack(p);
        String configName = MainCrackShotWeaponInfoMetaManager.getConfigName(p);
        int currentAmmoAmt = MainCrackShotWeaponInfoMetaManager.getCurrentAmmoAmount(p);
        int possessedExtraAmmoAmt = MainCrackShotWeaponInfoMetaManager.getPossessedExtraAmmoAmount(p);
        makePrettyWeaponDisplayName(p, is, configName, currentAmmoAmt, possessedExtraAmmoAmt);
    }

	public static void makePrettyWeaponDisplayName(@Nullable Player p, ItemStack weapon, String configName, int currentAmmoAmt, @Nullable Integer possessedExtraAmmoAmt) {
        ItemMeta meta = weapon.getItemMeta();
        StringBuilder weaponNameBuilder = new StringBuilder();

        String ammoStr;
        if (possessedExtraAmmoAmt != null) {
            ammoStr = AMMO_ICON1 + "§f" + currentAmmoAmt + "§7/" + possessedExtraAmmoAmt;
        } else {
            ammoStr = AMMO_ICON1 + "§f" + currentAmmoAmt + "§7/" + "n";
        }

        weaponNameBuilder.append(ammoStr);
        for (int i=0; i<LEFTSIDE_LENGTH - ChatColor.stripColor(ammoStr).length(); i++) {
            weaponNameBuilder.append(" ");
        }
        weaponNameBuilder.append(configName);
        for (int i=0; i<RIGHTSIDE_LENGTH; i++) {
            weaponNameBuilder.append(" ");
        }
        meta.setDisplayName(weaponNameBuilder.toString());
        weapon.setItemMeta(meta);

        if (p != null) {
            p.updateInventory();
        }
    }
}
