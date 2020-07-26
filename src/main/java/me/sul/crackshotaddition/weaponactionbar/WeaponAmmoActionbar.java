package me.sul.crackshotaddition.weaponactionbar;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.CrackShotAddition;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WeaponAmmoActionbar implements Listener {
    private static WeaponAmmoActionbar instance;

    public WeaponAmmoActionbar() {
        instance = this;
    }
    public static WeaponAmmoActionbar getInstance() {
        return instance;
    }

    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        int currentAmmo = CrackShotAdditionAPI.getWeaponAmmoAmount(e.getPlayer(), e.getWeaponTitle(), is);
        int reloadAmt = CrackShotAdditionAPI.getWeaponReloadAmount(e.getPlayer(), e.getWeaponTitle(), is);

        indicateAmmoInActionbar(e.getPlayer(), e.getWeaponTitle(), currentAmmo, reloadAmt);
    }

    public void indicateAmmoInActionbar(Player player, String parentNode, int ammo, int reloadAmt) {
        BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.sendActionBar("§f§l" + parentNode + " §e" + ammo + " §f/ §e" + reloadAmt);
                }
            }
        }.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 20);

        WeaponActionbar.getInstance().putRunnableToMapAndCancelLastestOne(player, runnable);
    }
}
