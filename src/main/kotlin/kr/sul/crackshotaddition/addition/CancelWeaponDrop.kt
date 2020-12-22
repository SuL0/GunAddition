package kr.sul.crackshotaddition.addition

import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI.csDirector
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI.isValidCrackShotWeapon
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.metadata.FixedMetadataValue

object CancelWeaponDrop: Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player) {
            val p = e.whoClicked as Player
            if (e.slot == -999 && csDirector.itemParentNode(e.cursor, p) != null) {
                p.setMetadata("dr0p_authorised", FixedMetadataValue(plugin, true))
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                    p.removeMetadata("dr0p_authorised", plugin)
                }, 1)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDrop(e: PlayerDropItemEvent) {
        // 총알 수가 Infinity 인 총을 들고 Q키 누르면 드랍되는거 방지. 단, GUI -999 를 통해 아이템 드랍은 허용
        if (!e.player.hasMetadata("dr0p_authorised") && isValidCrackShotWeapon(e.itemDrop.itemStack) && !e.isCancelled) {
            e.isCancelled = true
        }
    }
}