package me.sul.crackshotaddition;

import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.servercore.playertoolchangeevent.PlayerMainItemChangedConsideringUidEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeaponItemFlutterFixation implements Listener {
    private final PotionEffectType POTION_EFFECT_TYPE = PotionEffectType.SLOW_DIGGING;
    
    // 서버에 들어왔을 때도 이 이벤트가 실행되기때문에 onJoin은 넣을 필요가 없음.
    @EventHandler
    public void onMainItemChanged(PlayerMainItemChangedConsideringUidEvent e) {
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.getNewItemStack())) {
            e.getPlayer().addPotionEffect(new PotionEffect(POTION_EFFECT_TYPE, 1000000, 256, false, false), true);
            return;
        }
        if (e.getPlayer().hasPotionEffect(POTION_EFFECT_TYPE)) {
            e.getPlayer().removePotionEffect(POTION_EFFECT_TYPE);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer().hasPotionEffect(POTION_EFFECT_TYPE)) {
            e.getPlayer().removePotionEffect(POTION_EFFECT_TYPE);
        }
    }
}
