package kr.sul.crackshotaddition.weaponappearance.item.`object`

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.crackshotaddition.weaponappearance.item.LeftHandManager
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object LeftHand: Object, Listener {
    override fun isConfigEnabled(parentNode: String): Boolean { return csDirector.getBoolean("${getConfigNode(parentNode)}.Enable") }
    private fun getConfigNode(weaponParentNode: String): String { return "$weaponParentNode.Item_Information.ItemAppearance_Addition.Left_Hand" }

    // ConfigExtractor Class //
    private class ConfigExtractor(val parentNode: String) {
        val leftHandItem : ItemStack
            get() = LeftHandManager.makeLeftHandItem(getConfigNode(parentNode), normalStateDurability)
        val normalStateDurability: Short
            get() = getValueFromConfig("Normal_State_Item_Durability")
        val zoomInStateDurability: Short
            get() = getValueFromConfig("ZoomIn_State_Item_Durability")
        val reloadStateDurability: Short
            get() = getValueFromConfig("Reload_State_Item_Durability")

        private fun getValueFromConfig(childNode: String): Short {
            val rtnValue = csDirector.getInt("${getConfigNode(parentNode)}.$childNode", -1).toShort()
            return if (rtnValue == (-1).toShort()) throw Exception(parentNode) else rtnValue
        }
    }



    // 스왑 //
    // 일반 아이템으로 스왑할 때 손 제거 //
    @EventHandler
    fun onHeldItemIsChangedToNormalItem(e: PlayerHeldItemIsChangedToAnotherEvent) {
        if (!e.isChangedToCrackShotWeapon() && LeftHandManager.isHandItem(e.player.inventory.itemInOffHand)) {
            e.player.inventory.itemInOffHand = null
        }
    }
    // 아이템 들 때 손 생성(Normal State)
    override fun onHeldItemIsChangedToCrackShotWeapon(e: PlayerHeldItemIsChangedToAnotherEvent) {
        val configExtractor = ConfigExtractor(WeaponInfoExtractor(e.player, e.newItemStack).parentNode)
        e.player.inventory.itemInOffHand = configExtractor.leftHandItem  // TODO: 다른 아이템이 있을 때는? - 낙하산
    }

    // 줌 //
    override fun onZoom(e: WeaponScopeEvent) {
        val configExtractor = ConfigExtractor(e.parentNode)
        if (!LeftHandManager.isOffHandAHandItem(e.player)) throw Exception("$e - 예상과 달리 offHand에 왼손 아이템이 없음")
        if (e.isZoomIn) {
            e.player.inventory.itemInOffHand.durability = configExtractor.zoomInStateDurability
        } else {
            if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) return // 핫바 변경 등(아이템 변경)으로 줌이 취소된 경우는 그냥 return
            e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
        }
    }

    // 리로드 //
    override fun onReload(e: WeaponReloadEvent) {
        val configExtractor = ConfigExtractor(e.parentNode)
        if (!LeftHandManager.isOffHandAHandItem(e.player)) throw Exception("$e - 예상과 달리 offHand에 왼손 아이템이 없음")
        e.player.inventory.itemInOffHand.durability = configExtractor.reloadStateDurability
    }
    override fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val configExtractor = ConfigExtractor(e.parentNode)
        if (!LeftHandManager.isOffHandAHandItem(e.player)) throw Exception("$e - 예상과 달리 offHand에 왼손 아이템이 없음")
        e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
    }
}