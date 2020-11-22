package kr.sul.crackshotaddition.weaponappearance.item

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.crackshotaddition.weaponappearance.item.`object`.LeftHand
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object WhenToChangeItemDurability: Listener {
    private val objectSet = setOf(LeftHand)

    // 스왑 //
    @EventHandler
    fun onHeldItemIsChangedToCrackShotWeapon(e: PlayerHeldItemIsChangedToAnotherEvent) {
        if (!e.isChangedToCrackShotWeapon()) return
        val parentNode = WeaponInfoExtractor(e.player, e.newItemStack).parentNode

        for (obj in objectSet) {
            if (!obj.isConfigEnabled(parentNode)) continue
            obj.onHeldItemIsChangedToCrackShotWeapon(e)
        }
    }

    // 줌 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onZoom(e: WeaponScopeEvent) {
        if (e.isCancelled) return
        if (e.isZoomIn && WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
        val parentNode = e.parentNode

        for (obj in objectSet) {
            if (!obj.isConfigEnabled(parentNode)) continue
            obj.onZoom(e)
        }
    }

    // 리로드 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (e.isCancelled) return
        if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
        val parentNode = e.parentNode
        for (obj in objectSet) {
            if (!obj.isConfigEnabled(parentNode)) continue
            obj.onReload(e)
        }
    }
    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val parentNode = e.parentNode
        if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
        for (obj in objectSet) {
            if (!obj.isConfigEnabled(parentNode)) continue
            obj.onReloadComplete(e)
        }
    }
}