package kr.sul.crackshotaddition.addition_appearance.itemmgr

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.servercore.extensionfunction.UpdateInventorySlot
import kr.sul.servercore.extensionfunction.UpdateInventorySlot.updateInventorySlot
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object GunInMainHandFor1P: Listener {
    private const val gunModelingNode = "Item_Information.GunItem_Modeling_Addition"
    private fun isConfigEnabled(parentNode: String): Boolean { return CrackShotAddition.csDirector.getBoolean("$parentNode.$gunModelingNode.Enable") }

    // ConfigExtractor Class //
    private class ConfigExtractor(val parentNode: String) {
        val normalStateDurability: Short
            get() = getValueFromConfig("Normal")
        val shootWhileInNormalStateDurability: Short
            get() = getValueFromConfig("Shoot_WhileIn_Normal")
        val zoomInStateDurability: Short
            get() = getValueFromConfig("ZoomIn")
        val shootWhileInZoomInStateDurability: Short
            get() = getValueFromConfig("Shoot_WhileIn_ZoomIn")
        val reloadStateDurability: Short
            get() = getValueFromConfig("Reload")

        private fun getValueFromConfig(childNode: String): Short {
            val rtnValue = CrackShotAddition.csDirector.getInt("$parentNode.$gunModelingNode.Main_Hand_Item_DurabilityMgr.$childNode", -1).toShort()
            return if (rtnValue == (-1).toShort()) throw Exception("$parentNode, $childNode") else rtnValue
        }
    }


    // 스왑 //
    @EventHandler
    fun onHeldItemIsChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        // 총 아이템 들 때 NormalState Durability로 설정
        if (e.isChangedToCrackShotWeapon()) {
            val parentNode = WeaponInfoExtractor(e.player, e.newItemStack).parentNode
            if (!isConfigEnabled(parentNode)) return
            val configExtractor = ConfigExtractor(parentNode)
            e.newItemStack.durability = configExtractor.normalStateDurability
        }
    }

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        if (!isConfigEnabled(e.parentNode)) return
        if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
        val configExtractor = ConfigExtractor(e.parentNode)

        // 리팩상 반동 주기
        if (CSDirector.isZooming(e.player)) {
            e.player.inventory.itemInMainHand.durability = configExtractor.shootWhileInZoomInStateDurability
        } else {
            e.player.inventory.itemInMainHand.durability = configExtractor.shootWhileInNormalStateDurability
        }
        e.player.updateInventorySlot(UpdateInventorySlot.HandType.MAIN_HAND)
        // 1틱 뒤 반동 되돌리기
        Bukkit.getScheduler().runTaskLater(plugin, {
            val oneTickLaterMainItemParentNode = WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode
            if (e.parentNode == oneTickLaterMainItemParentNode &&
                    e.player.inventory.itemInMainHand.durability == configExtractor.shootWhileInZoomInStateDurability ||
                    e.player.inventory.itemInMainHand.durability == configExtractor.shootWhileInNormalStateDurability ) {
                if (CSDirector.isZooming(e.player)) {
                    e.player.inventory.itemInMainHand.durability = configExtractor.zoomInStateDurability
                } else {
                    e.player.inventory.itemInMainHand.durability = configExtractor.normalStateDurability
                }
                e.player.updateInventorySlot(UpdateInventorySlot.HandType.MAIN_HAND)
            }
        }, 1L)
    }



    // 줌 //
    // 핫바 변경땜에 얘는 checkEssentialThings 제외
    @EventHandler(priority = EventPriority.HIGH)
    fun onZoom(e: WeaponScopeEvent) {
        if (e.isCancelled || !isConfigEnabled(e.parentNode)) return
        val configExtractor = ConfigExtractor(e.parentNode)

        if (e.isZoomIn) {
            e.player.inventory.itemInMainHand.durability = configExtractor.zoomInStateDurability
        } else {
            if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) return // 핫바 변경 등(아이템 변경)으로 줌이 취소된 경우는 그냥 return
            e.player.inventory.itemInMainHand.durability = configExtractor.normalStateDurability
        }
    }

    // 리로드 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (e.isCancelled || !isConfigEnabled(e.parentNode)) return
        if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")

        e.player.inventory.itemInMainHand.durability = ConfigExtractor(e.parentNode).reloadStateDurability
    }
    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        if (!isConfigEnabled(e.parentNode)) return
        if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")

        e.player.inventory.itemInMainHand.durability = ConfigExtractor(e.parentNode).normalStateDurability
    }
}