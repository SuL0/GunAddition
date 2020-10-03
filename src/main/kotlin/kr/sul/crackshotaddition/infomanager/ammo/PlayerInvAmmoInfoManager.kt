package kr.sul.crackshotaddition.infomanager.ammo

import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
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
        if (Ammo.of(e.newItemStack) != null && Ammo.listOfAllAmmo.contains(Ammo.of(e.newItemStack))) {
            getInfo(p).update(p, Ammo.of(e.newItemStack)!!)
        }
        else if (e.newItemStack.type == Material.AIR) {
            getInfo(p).updateAll(p)
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(e: PlayerLoginEvent) { // JoinEvent를 쓰면 플레이어를 초기화하는 중, onItemChagned가 먼저 호출받게됨
        playersAmmoInfoMap[e.player] = PlayerInvAmmoInfo(e.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(e: PlayerQuitEvent) { playersAmmoInfoMap.remove(e.player) }
}