package me.sul.crackshotaddition.weaponactionbar;

import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.servercore.playertoolchangeevent.PlayerMainItemChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class WeaponActionbar implements Listener {
    private static WeaponActionbar instance;
    private HashMap<UUID, BukkitTask> weaponActionbarRunnableOfPlayers = new HashMap<UUID, BukkitTask>();

    public WeaponActionbar() { instance = this; }
    public static WeaponActionbar getInstance() { return instance; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (weaponActionbarRunnableOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            weaponActionbarRunnableOfPlayers.get(e.getPlayer().getUniqueId()).cancel();
            weaponActionbarRunnableOfPlayers.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onMainItemChange(PlayerMainItemChangeEvent e) {
        if (!CrackShotAdditionAPI.checkIfItemIsGun(e.getNewItem())) {
            // Runnable 캔슬
            BukkitTask playerActionbarTask = weaponActionbarRunnableOfPlayers.get(e.getPlayer().getUniqueId());
            if (playerActionbarTask != null) {
                if (!playerActionbarTask.isCancelled()) {
                    playerActionbarTask.cancel();
                }
            }
            // 기존 액션바 지우기
            if (CrackShotAdditionAPI.checkIfItemIsGun(e.getPreviousItem())) {
                e.getPlayer().sendActionBar("§f");
            }
        }
    }


    public void putRunnableToMapAndCancelLastestOne(Player p, BukkitTask t) {
        if (weaponActionbarRunnableOfPlayers.containsKey(p.getUniqueId())) {
            if (weaponActionbarRunnableOfPlayers.get(p.getUniqueId()).isCancelled() == false) {
                weaponActionbarRunnableOfPlayers.get(p.getUniqueId()).cancel(); // 이전에 돌던 Runnable 캔슬
            }
        }
        weaponActionbarRunnableOfPlayers.put(p.getUniqueId(), t);   // put은 중복되는 key가 있으면 덮어쓰기 함
    }
}