package kr.sul.crackshotaddition.addition

import kr.sul.crackshotaddition.util.CrackShotAdditionAPI.isValidCrackShotWeapon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object CancelWeaponDrop: Listener {
    @EventHandler
    fun onDrop(e: PlayerDropItemEvent) {
        // 총알 수가 Infinity 인 총을 들고 Q키 누르면 드랍되는거 방지
        if (isValidCrackShotWeapon(e.itemDrop.itemStack) && !e.isCancelled) e.isCancelled = true
    }
}