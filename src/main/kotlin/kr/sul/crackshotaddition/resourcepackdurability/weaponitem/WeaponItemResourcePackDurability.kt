package kr.sul.crackshotaddition.resourcepackdurability.weaponitem

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object WeaponItemResourcePackDurability: Listener {
    private fun getConfigNode(weaponParentNode: String): String { return "$weaponParentNode.Item_Information.ResourcePack_Addition.Weapon_Item" }
    private class ConfigExtractor(val parentNode: String) {
        val enabled = csDirector.getBoolean("${getConfigNode(parentNode)}.Enable")
        val normalStateDurability: Short?
            get() = run {
                val rtnValue = csDirector.getInt("${getConfigNode(parentNode)}.Normal_State_Item_Durability", -1).toShort()
                return if (rtnValue == (-1).toShort()) null else rtnValue
            }
        val zoomInStateDurability: Short?
            get() = run {
                val rtnValue = csDirector.getInt("${getConfigNode(parentNode)}.ZoomIn_State_Item_Durability", -1).toShort()
                return if (rtnValue == (-1).toShort()) null else rtnValue
            }
        val reloadStateDurability: Short?
            get() = run {
                val rtnValue = csDirector.getInt("${getConfigNode(parentNode)}.Reload_State_Item_Durability", -1).toShort()
                return if (rtnValue == (-1).toShort()) null else rtnValue
            }
    }

    // 슬롯 //
    // 아이템 들 때 Normal State로 바꾸기 //
    @EventHandler
    fun onHeldItemIsChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        if (!e.isChangedToCrackShotWeapon()) return
        val configExtractor = ConfigExtractor(WeaponInfoExtractor(e.player, e.newItemStack).parentNode)

        if (configExtractor.enabled) {
            val normalDur = configExtractor.normalStateDurability
            if (normalDur != null) {
                e.newItemStack.durability = normalDur
            }
        }
    }

    // 줌 //
    // 줌 할 때 ZoomIn State로 바꾸기 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onZoom(e: WeaponScopeEvent) {
        if (e.isCancelled) return
        val configExtractor = ConfigExtractor(e.parentNode)

        if (!configExtractor.enabled) return
        if (e.isZoomIn) {
            val zoomInDur = configExtractor.zoomInStateDurability
            if (zoomInDur != null) {
                val heldItem = e.player.inventory.itemInMainHand
                if (WeaponInfoExtractor(e.player, heldItem).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
                heldItem.durability = zoomInDur
            }
        } else {
            val normalDur = configExtractor.normalStateDurability
            if (normalDur != null) {
                val heldItem = e.player.inventory.itemInMainHand
                if (WeaponInfoExtractor(e.player, heldItem).parentNode != e.parentNode) return // 핫바 변경 등(아이템 변경)으로 줌이 취소된 경우는 그냥 return
                heldItem.durability = normalDur
            }
        }
    }

    // 리로드 //
    // 리로드 할 때 Reload State로 바꾸기 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (e.isCancelled) return
        val configExtractor = ConfigExtractor(e.parentNode)

        if (configExtractor.enabled) {
            val reloadDur = configExtractor.reloadStateDurability
            if (reloadDur != null) {
                val heldItem = e.player.inventory.itemInMainHand
                if (WeaponInfoExtractor(e.player, heldItem).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
                heldItem.durability = reloadDur
            }
        }
    }
    // 리로드 끝났을 때 Normal State로 바꾸기 //
    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val configExtractor = ConfigExtractor(e.parentNode)

        if (configExtractor.enabled ) {
            val normalDur = configExtractor.normalStateDurability
            if (normalDur != null) {
                val heldItem = e.player.inventory.itemInMainHand
                if (WeaponInfoExtractor(e.player, heldItem).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
                heldItem.durability = normalDur
            }
        }
    }
}