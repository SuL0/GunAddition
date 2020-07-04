package me.sul.crackshotaddition.weaponactionbar;

import com.shampaggon.crackshot.events.WeaponReloadEvent;
import me.sul.crackshotaddition.CrackShotAddition;
import me.sul.crackshotaddition.events.WeaponHoldEvent;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WeaponSwappingOrReloadingActionbar implements Listener {
    public enum EventType {
        Swapping, Reloading
    }

    @EventHandler
    public void onWeaponHold(WeaponHoldEvent e) {
        if (e.isWeaponSwap()) {
            indicateSwappingOrReloadingInActionbar(e.getPlayer(), EventType.Swapping, e.getWeaponItem(), e.getWeaponTitle(), e.getSwapDelay());
        } else {
            int currentAmmo = CrackShotAdditionAPI.getWeaponAmmoAmount(e.getPlayer(), e.getWeaponTitle(), e.getWeaponItem());
            int reloadAmt = CrackShotAdditionAPI.getWeaponReloadAmount(e.getPlayer(), e.getWeaponTitle(), e.getWeaponItem());
            WeaponAmmoActionbar.getInstance().indicateAmmoInActionbar(e.getPlayer(), e.getWeaponTitle(), currentAmmo, reloadAmt);
        }
    }

    @EventHandler
    public void onWeaponReload(WeaponReloadEvent e) {
        indicateSwappingOrReloadingInActionbar(e.getPlayer(), EventType.Reloading, e.getPlayer().getInventory().getItemInMainHand(), e.getWeaponTitle(), e.getReloadDuration());
    }


    public void indicateSwappingOrReloadingInActionbar(Player player, EventType eventType, ItemStack weaponItem, String weaponTitle, int totalTime) {
        BukkitTask task = new BukkitRunnable() {
            float leftTime = totalTime/20;

            @Override
            public void run() {
                // swap 끝나면 ammo 액션바로 교체
                if (leftTime <= 0) {
                    int ammo;
                    int reloadAmt = CrackShotAdditionAPI.getWeaponReloadAmount(player, weaponTitle, weaponItem);
                    if (eventType.equals(EventType.Swapping)) {
                        ammo = CrackShotAdditionAPI.getWeaponAmmoAmount(player, weaponTitle, weaponItem);
                    } else {
                        ammo = reloadAmt;
                    }
                    WeaponAmmoActionbar.getInstance().indicateAmmoInActionbar(player, weaponTitle, ammo, reloadAmt);
                }
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    if (eventType.equals(EventType.Swapping)) {
                        player.sendActionBar("§f§l" + weaponTitle + " §fSwap §e" + leftTime + "§es");
                    } else {
                        player.sendActionBar("§f§l" + weaponTitle + " §fReload §e" + leftTime + "§es");
                    }
                }
                leftTime = Math.round(leftTime - 0.1F); // 2틱/20, round는 부동소수점 오류 때문에 사용함.
            }
        }.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 2);

        WeaponActionbar.getInstance().putRunnableToMapAndCancelLastestOne(player, task);
    }
}
