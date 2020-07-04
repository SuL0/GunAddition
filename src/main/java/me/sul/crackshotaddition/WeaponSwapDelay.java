package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;
import com.shampaggon.crackshot.events.WeaponReloadEvent;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import me.sul.crackshotaddition.events.WeaponHoldEvent;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.servercore.playertoolchangeevent.PlayerMainItemChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class WeaponSwapDelay implements Listener {
    private final int WEAPON_SWAPDELAY = 8; // ticks
    private HashMap<UUID, Long> swapDelayOfPlayers = new HashMap<UUID, Long>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { swapDelayOfPlayers.remove(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onMainItemChange(PlayerMainItemChangeEvent e) {
        String previousWeaponTitle = CrackShotAdditionAPI.getWeaponTitle(e.getPreviousItem());
        String newWeaponTitle = CrackShotAdditionAPI.getWeaponTitle(e.getNewItem());
        if (newWeaponTitle != null) {
            int swapDelay;
            if (previousWeaponTitle != null) {
                swapDelay = WEAPON_SWAPDELAY;
            } else {
                swapDelay = 0;
            }
            Bukkit.getServer().getPluginManager().callEvent(new WeaponHoldEvent(e.getPlayer(), e.getNewItem(), newWeaponTitle, swapDelay));
            if (swapDelay > 0) {
                swapDelayOfPlayers.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + swapDelay*50); // 1tick = 1ms * 50
            }
        }
    }

    
//    
//    크랙샷 무기 모든 행위 캔슬시키기
//    
    @EventHandler(priority = EventPriority.LOW)
    public void onPrepareShoot(WeaponPrepareShootEvent e) {
        if (swapDelayOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (swapDelayOfPlayers.get(e.getPlayer().getUniqueId()) <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.getPlayer().getUniqueId());
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onReload(WeaponReloadEvent e) {
        if (swapDelayOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (swapDelayOfPlayers.get(e.getPlayer().getUniqueId()) <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.getPlayer().getUniqueId());
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onScope(WeaponScopeEvent e) {
        if (swapDelayOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (swapDelayOfPlayers.get(e.getPlayer().getUniqueId()) <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.getPlayer().getUniqueId());
            } else {
                e.setCancelled(true);
            }
        }
    }
}
