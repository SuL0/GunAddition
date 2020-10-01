package kr.sul.crackshotaddition.infomanager.ammo

import com.shampaggon.crackshot.CSMinion
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

object PlayerInvAmmoInfoManager : Listener {
    class AmmoInfo(p: Player) {
        val possessedAmmoList = hashMapOf<Ammo, Int>()

        init {
            for (ammo in Ammo.listOfAllAmmo) {
                possessedAmmoList[ammo] = 0
                update(p, ammo)
            }
        }

        fun getReloadableAmountPerWeapon(p: Player, item: ItemStack, ammo: Ammo): Int {
            val parentNode = CrackShotAdditionAPI.getWeaponParentNode(item)!!
            val capacity = CrackShotAdditionAPI.getWeaponReloadCapacity(p, parentNode, item)
            return capacity * possessedAmmoList[ammo]!!
        }

        fun update(p: Player, ammo: Ammo) {
            possessedAmmoList[ammo] = CSMinion.getInstance().countItemStacks(p, ammo.itemInfo, null)
        }
        fun updateAll(p: Player) {
            Ammo.listOfAllAmmo.forEach { update(p, it) }
        }
    }


    private val playersAmmoInfoMap = hashMapOf<Player, AmmoInfo>()

    fun getInfo(p: Player): AmmoInfo {
        return playersAmmoInfoMap[p] ?: throw Exception("$p")
    }

    /* Ammo 업데이트 */
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
        playersAmmoInfoMap[e.player] = AmmoInfo(e.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(e: PlayerQuitEvent) { playersAmmoInfoMap.remove(e.player) }
}