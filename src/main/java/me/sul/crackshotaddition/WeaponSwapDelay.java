package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;
import com.shampaggon.crackshot.events.WeaponReloadEvent;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import me.sul.crackshotaddition.events.WeaponSwapCompleteEvent;
import me.sul.crackshotaddition.events.WeaponSwapEvent;
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class WeaponSwapDelay implements Listener {
    private final int WEAPON_SWAPDELAY = 8; // ticks
    private final HashMap<UUID, Long> swapDelayOfPlayers = new HashMap<UUID, Long>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { swapDelayOfPlayers.remove(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onMainItemChange(PlayerMainItemChangedConsideringUidEvent e) {
        Player p = e.getPlayer();
        String newWeaponParentNode = (MainCrackShotWeaponInfoMetaManager.isSet(p)) ? MainCrackShotWeaponInfoMetaManager.getParentNode(p) : null;
        // 이전 템 쿨타임 적용 중 이였다면, 삭제
        if (p.getCooldown(e.getClonedPreviousItemStack().getType()) > 1) {
            p.setCooldown(e.getClonedPreviousItemStack().getType(), 0);
        }

        if (newWeaponParentNode != null) {
            Bukkit.getServer().getPluginManager().callEvent(new WeaponSwapEvent(p, e.getNewItemStack(), newWeaponParentNode, WEAPON_SWAPDELAY));
            long swapDelay = System.currentTimeMillis() + WEAPON_SWAPDELAY*50; // 1tick = 1ms * 50
            swapDelayOfPlayers.put(p.getUniqueId(), swapDelay);
            p.setCooldown(e.getNewItemStack().getType(), WEAPON_SWAPDELAY);

            Bukkit.getScheduler().runTaskLater(CrackShotAddition.getInstance(), () -> {
                if (MainCrackShotWeaponInfoMetaManager.isSet(p) &&
                        swapDelayOfPlayers.containsKey(p.getUniqueId()) && swapDelayOfPlayers.get(p.getUniqueId()) == swapDelay) { // 전에 넣은 딜레이 값이랑 똑같은지 확인
                    Bukkit.getServer().getPluginManager().callEvent(new WeaponSwapCompleteEvent(p, e.getNewItemStack(), newWeaponParentNode));
                }
            }, WEAPON_SWAPDELAY);
        }
    }

    
//    
//    크랙샷 무기 모든 행위 캔슬시키기
//    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareShoot(WeaponPrepareShootEvent e) {
        if (swapDelayOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (swapDelayOfPlayers.get(e.getPlayer().getUniqueId()) <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.getPlayer().getUniqueId());
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onReload(WeaponReloadEvent e) {
        if (swapDelayOfPlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (swapDelayOfPlayers.get(e.getPlayer().getUniqueId()) <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.getPlayer().getUniqueId());
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
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
