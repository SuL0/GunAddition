package me.sul.crackshotaddition.weaponactionbar;

import com.shampaggon.crackshot.events.WeaponReloadEvent;
import me.sul.crackshotaddition.CrackShotAddition;
import me.sul.crackshotaddition.events.CrackShotWeaponHeldEvent;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import org.bukkit.Bukkit;
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

    // 총기 스왑
    @EventHandler
    public void onWeaponHeld(CrackShotWeaponHeldEvent e) {
        Player p = e.getPlayer();
        if (e.isWeaponSwap()) {
            indicateSwappingOrReloadingInActionbar(e.getPlayer(), EventType.Swapping, e.getWeaponItem(), e.getWeaponTitle(), e.getSwapDelay());
        } else {
            int currentAmmo = CrackShotAdditionAPI.getWeaponAmmoAmount(p, e.getWeaponTitle(), e.getWeaponItem());
            int reloadAmt = CrackShotAdditionAPI.getWeaponReloadAmount(p, e.getWeaponTitle(), e.getWeaponItem());
            WeaponAmmoActionbar.getInstance().indicateAmmoInActionbar(p, e.getWeaponTitle(), currentAmmo, reloadAmt);
        }
    }

    // 총기 리로드
    @EventHandler
    public void onWeaponReload(WeaponReloadEvent e) {
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        indicateSwappingOrReloadingInActionbar(e.getPlayer(), EventType.Reloading, is, e.getWeaponTitle(), e.getReloadDuration());
    }


    public void indicateSwappingOrReloadingInActionbar(Player player, EventType eventType, ItemStack weaponItem, String parentNode, int totalTime) {
        BukkitTask task = new BukkitRunnable() {
            float leftTime = totalTime/20F;

            @Override
            public void run() {
                // Swap/Reload 끝나면 Ammo 액션바로 교체
                if (leftTime <= 0) {
                    int ammo = 0;
                    int reloadAmt = CrackShotAdditionAPI.getWeaponReloadAmount(player, parentNode, weaponItem);
                    if (eventType.equals(EventType.Swapping)) {
                        ammo = CrackShotAdditionAPI.getWeaponAmmoAmount(player, parentNode, weaponItem);
                    } else if (eventType.equals(EventType.Reloading)){
                        ammo = reloadAmt;
                    }
                    WeaponAmmoActionbar.getInstance().indicateAmmoInActionbar(player, parentNode, ammo, reloadAmt); // 여기서 이 task cancel 시킴
                }
                // Swap/Reload 액션바
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    if (eventType.equals(EventType.Swapping)) {
                        player.sendActionBar("§f§l" + parentNode + " §fSwap §e" + leftTime + "§es");
                    } else if (eventType.equals(EventType.Reloading)){
                        player.sendActionBar("§f§l" + parentNode + " §fReload §e" + leftTime + "§es");
                    }
                }
                leftTime = Math.round((leftTime-0.1)*10)/10F; // -2틱, 부동소수점 때문에 round로 소수점 1자리까지 반올림 하게 만듦.
            }
        }.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 2);

        WeaponActionbar.getInstance().putRunnableToMapAndCancelLastestOne(player, task); // run()이 실행되는동안 이게 실행이 안되는게 아님.
    }
}
