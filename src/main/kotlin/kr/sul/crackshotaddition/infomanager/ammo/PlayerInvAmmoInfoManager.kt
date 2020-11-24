package kr.sul.crackshotaddition.infomanager.ammo

import kr.sul.crackshotaddition.event.PlayerInvAmmoAmtChangedEvent
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerInvAmmoInfoManager : Listener {
    private val playersAmmoInfoMap = hashMapOf<Player, PlayerInvAmmoInfo>()

    fun getInfo(p: Player): PlayerInvAmmoInfo {
        return playersAmmoInfoMap[p] ?: throw Exception("$p")
    }


    /* 보유한 Ammo 값 업데이트 */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemChanged(e: InventoryItemChangedEvent) {
        val p = e.player
        val playerInvAmmoInfo = getInfo(p)

        var amtChangedAmmo: Ammo? = null
        if (Ammo.of(e.newItemStack) != null && Ammo.isAmmoItem(e.newItemStack)) {
            val ammo = Ammo.of(e.newItemStack)!!

            val isAmmoChanged = playerInvAmmoInfo.update(p, ammo)  // 총알 보유 개수 업데이트
            if (isAmmoChanged) {
                amtChangedAmmo = ammo
            }
        }
        else if (e.newItemStack.type == Material.AIR) {
            amtChangedAmmo = playerInvAmmoInfo.updateAll(p)  // 총알 보유 개수 모두 업데이트
        }

        // 총알 보유 개수가 바꼇다면, 이벤트 호출
        if (amtChangedAmmo != null) {
            val possessedAmmoAmtUpdatedEvent = PlayerInvAmmoAmtChangedEvent(p, amtChangedAmmo, playerInvAmmoInfo.getPossessedAmmoAmt(amtChangedAmmo))
            Bukkit.getPluginManager().callEvent(possessedAmmoAmtUpdatedEvent)
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(e: PlayerLoginEvent) { // JoinEvent를 쓰면 플레이어를 초기화하기도 전에, onItemChagned가 먼저 호출받게됨
        playersAmmoInfoMap[e.player] = PlayerInvAmmoInfo(e.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(e: PlayerQuitEvent) { playersAmmoInfoMap.remove(e.player) }
}