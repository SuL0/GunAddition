package me.sul.crackshotaddition.weaponappearance_item

import me.sul.crackshotaddition.util.CrackShotAdditionAPI
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class WeaponItemFlutterFixation : Listener {
    companion object {
        private val POTION_EFFECT_TYPE = PotionEffectType.SLOW_DIGGING
    }

    // 서버에 들어왔을 때도 이 이벤트가 실행되기때문에 onJoin은 넣을 필요가 없음.
    @EventHandler
    fun onMainItemChanged(e: PlayerMainItemChangedConsideringUidEvent) {
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.newItemStack)) {
            e.player.addPotionEffect(PotionEffect(POTION_EFFECT_TYPE, 1000000, 256, false, false), true)
            return
        }
        if (e.player.hasPotionEffect(POTION_EFFECT_TYPE)) {
            e.player.removePotionEffect(POTION_EFFECT_TYPE)
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (e.player.hasPotionEffect(POTION_EFFECT_TYPE)) {
            e.player.removePotionEffect(POTION_EFFECT_TYPE)
        }
    }
}