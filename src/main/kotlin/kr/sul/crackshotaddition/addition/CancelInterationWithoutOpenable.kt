package kr.sul.crackshotaddition.addition

import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.material.Openable


// 총 쏘다가 땅이 갈아지거나, 징징이에게 말이 걸리는 등의 불상사를 막아줌 (단 Openable은 제외)
object CancelInterationWithoutOpenable: Listener {

    // 그런데 이것이, 스폰 같이 총이 아예 쏴지지 않는 곳에서는 왜 Interaction이 안되지? 라고 혼란을 야기할지도?
    @EventHandler(priority = EventPriority.LOW)
    fun onInteractEntityWithGun(e: PlayerInteractEntityEvent) {
        if (e.isCancelled) return
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.player.inventory.itemInMainHand)) {
            e.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onInteractBlockWithGun(e: PlayerInteractEvent) {
        if (e.isCancelled) return
        if (CrackShotAdditionAPI.isValidCrackShotWeapon(e.player.inventory.itemInMainHand)) {
            // Openable(e.g. Door, Trapdoor..) 이 아니면 Interact 모두 취소 (e.g. 땅갈기, 징징이 말걸기)
            if (e.clickedBlock?.state?.data !is Openable) {
                e.isCancelled = true
            }
        }
    }
}