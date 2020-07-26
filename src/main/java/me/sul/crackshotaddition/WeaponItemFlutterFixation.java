package me.sul.crackshotaddition;

import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.servercore.playertoolchangeevent.PlayerMainItemChangedConsideringUidEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeaponItemFlutterFixation implements Listener {
    // 서버에 들어왔을 때도 이 이벤트가 실행되기때문에 onJoin은 넣을 필요가 없음. -> 아님. 서버 닫고 처음 들어왔을때는 그렇지만, 다시 들어오면 이벤트가 실행이 안됨.
    @EventHandler
    public void onMainItemChanged(PlayerMainItemChangedConsideringUidEvent e) {
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.getNewItemStack())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 256, false, false), true);
            return;
        }
        if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
            e.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.getPlayer().getInventory().getItemInMainHand())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 256, false, false), true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
            e.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }
    }
}
