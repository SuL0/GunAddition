package kr.sul.crackshotaddition.weaponappearance.item.`object`

//import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
//import com.shampaggon.crackshot.events.WeaponReloadEvent
//import com.shampaggon.crackshot.events.WeaponScopeEvent
//import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
//import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
//import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
//
//object WeaponItem: Object {
//    private fun getConfigNode(weaponParentNode: String): String { return "$weaponParentNode.Item_Information.ItemAppearance_Addition.Weapon_Item" }
//    override fun isConfigEnabled(parentNode: String): Boolean { return csDirector.getBoolean("${getConfigNode(parentNode)}.Enable") }
//
//    private class ConfigExtractor(val parentNode: String) {
//        val normalStateDurability: Short
//            get() = getValueFromConfig("Normal_State_Item_Durability")
//        val zoomInStateDurability: Short
//            get() = getValueFromConfig("ZoomIn_State_Item_Durability")
//        val reloadStateDurability: Short
//            get() = getValueFromConfig("Reload_State_Item_Durability")
//
//        private fun getValueFromConfig(childNode: String): Short {
//            val rtnValue = csDirector.getInt("${getConfigNode(parentNode)}.$childNode", -1).toShort()
//            return if (rtnValue == (-1).toShort()) throw Exception(parentNode) else rtnValue
//        }
//    }
//
//    // 슬롯 //
//    override fun onHeldItemIsChangedToCrackShotWeapon(e: PlayerHeldItemIsChangedToAnotherEvent) {
//        val configExtractor = ConfigExtractor(WeaponInfoExtractor(e.player, e.newItemStack).parentNode)
//        e.newItemStack.durability = configExtractor.normalStateDurability
//    }
//
//    // 줌 //
//    override fun onZoom(e: WeaponScopeEvent) {
//        val configExtractor = ConfigExtractor(e.parentNode)
//        if (e.isZoomIn) {
//            e.player.inventory.itemInMainHand.durability = configExtractor.zoomInStateDurability
//        } else {
//            val heldItem = e.player.inventory.itemInMainHand
//            if (WeaponInfoExtractor(e.player, heldItem).parentNode != e.parentNode) return // 핫바 변경 등(아이템 변경)으로 줌이 취소된 경우는 그냥 return
//            heldItem.durability = configExtractor.normalStateDurability
//        }
//    }
//
//    // 리로드 //
//    override fun onReload(e: WeaponReloadEvent) {
//        val configExtractor = ConfigExtractor(e.parentNode)
//        e.player.inventory.itemInMainHand.durability = configExtractor.reloadStateDurability
//    }
//    override fun onReloadComplete(e: WeaponReloadCompleteEvent) {
//        val configExtractor = ConfigExtractor(e.parentNode)
//        e.player.inventory.itemInMainHand.durability = configExtractor.normalStateDurability
//    }
//}