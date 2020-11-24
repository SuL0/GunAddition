package kr.sul.crackshotaddition.addition_appearance

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

object GunModelingInOffHandMgr: Listener {
    private fun isConfigEnabled(parentNode: String): Boolean { return CrackShotAddition.csDirector.getBoolean("${getConfigNode(parentNode)}.Enable") }
    private fun getConfigNode(weaponParentNode: String): String { return "$weaponParentNode.Item_Information.ItemAppearance_Addition.Left_Hand" }

    // ConfigExtractor Class //
    private class ConfigExtractor(val parentNode: String) {
        val gunModelingForOffHand : ItemStack
            get() = GunModelingMgr.makeGunModelingForOffHand(getConfigNode(parentNode), normalStateDurability)
        val normalStateDurability: Short
            get() = getValueFromConfig("Normal_State_Durability")
        val shootWhileInNormalStateDurability: Short
            get() = getValueFromConfig("Shoot_While_In_Normal_State_Durability")
        val zoomInStateDurability: Short
            get() = getValueFromConfig("ZoomIn_State_Durability")
        val shootWhileInZoomInStateDurability: Short
            get() = getValueFromConfig("Shoot_While_In_ZoomIn_State_Durability")
        val reloadStateDurability: Short
            get() = getValueFromConfig("Reload_State_Durability")

        private fun getValueFromConfig(childNode: String): Short {
            val rtnValue = CrackShotAddition.csDirector.getInt("${getConfigNode(parentNode)}.$childNode", -1).toShort()
            return if (rtnValue == (-1).toShort()) throw Exception("$parentNode, $childNode") else rtnValue
        }
    }


    // @EventHandler에서 공통적으로 필요한 것들 체크
    private fun checkEssentialThings(e: Event, p: Player, parentNode: String) : Boolean {
        if (e is Cancellable && e.isCancelled) return false
        if (!isConfigEnabled(parentNode)) return false
        if (WeaponInfoExtractor(p, p.inventory.itemInMainHand).parentNode != parentNode) throw Exception("$e - event의 대상이 되는 아이템이 예상(heldItem)과 다름")
        return true
    }

    // TODO: 장전하다가 발사하게 되면, 장전하는 모션 그대로(onReloadComplete가 아니기 때문) 총을 발사하게 되는 버그가 발생하게 됨.
    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        if (!checkEssentialThings(e, e.player, e.parentNode)) return
        if (!GunModelingMgr.hasGunModelingInOffHand(e.player)) throw Exception("예상과 달리 OffHand에 GunModeling 아이템이 없음 \n ${e.player.inventory.itemInOffHand}, $e")
        val configExtractor = ConfigExtractor(e.parentNode)

        // 줌 상태 발사
        if (e.player.inventory.itemInOffHand.durability == configExtractor.zoomInStateDurability) {
            e.player.inventory.itemInOffHand.durability = configExtractor.shootWhileInZoomInStateDurability
            Bukkit.getScheduler().runTaskLater(plugin, {
                if (GunModelingMgr.hasGunModelingInOffHand(e.player)) {
                    if (CSDirector.isZooming(e.player)) {
                        e.player.inventory.itemInOffHand.durability = configExtractor.zoomInStateDurability
                    } else {
                        e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
                    }
                }
            }, 1L)
        }
        // 일반 상태 발사
        else {
            e.player.inventory.itemInOffHand.durability = configExtractor.shootWhileInNormalStateDurability
            Bukkit.getScheduler().runTaskLater(plugin, {
                if (GunModelingMgr.hasGunModelingInOffHand(e.player)) {
                    e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
                }
            }, 1L)
        }
    }


    // 스왑 //
    @EventHandler
    fun onHeldItemIsChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        // 아이템 들 때 총 모델링 생성(Normal State)
        if (e.isChangedToCrackShotWeapon()) {
            if (!isConfigEnabled(WeaponInfoExtractor(e.player, e.newItemStack).parentNode)) return
            val configExtractor = ConfigExtractor(WeaponInfoExtractor(e.player, e.newItemStack).parentNode)

            e.player.inventory.setItemInOffHand(configExtractor.gunModelingForOffHand)  // TODO: 다른 아이템이 있을 때는? - 낙하산
        }
        // 일반 아이템으로 스왑할 때 총 모델링 제거 //
        else {
            if (GunModelingMgr.hasGunModelingInOffHand(e.player)) {
                e.player.inventory.setItemInOffHand(null)
            }
        }
    }



    // 줌 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onZoom(e: WeaponScopeEvent) {
        if (!checkEssentialThings(e, e.player, e.parentNode)) return

        val configExtractor = ConfigExtractor(e.parentNode)
        if (!GunModelingMgr.hasGunModelingInOffHand(e.player)) throw Exception("예상과 달리 OffHand에 GunModeling 아이템이 없음 \n ${e.player.inventory.itemInOffHand}, $e")

        if (e.isZoomIn) {
            e.player.inventory.itemInOffHand.durability = configExtractor.zoomInStateDurability
        } else {
            if (WeaponInfoExtractor(e.player, e.player.inventory.itemInMainHand).parentNode != e.parentNode) return // 핫바 변경 등(아이템 변경)으로 줌이 취소된 경우는 그냥 return
            e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
        }
    }

    // 리로드 //
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (!checkEssentialThings(e, e.player, e.parentNode)) return

        val configExtractor = ConfigExtractor(e.parentNode)
        if (!GunModelingMgr.hasGunModelingInOffHand(e.player)) throw Exception("예상과 달리 OffHand에 GunModeling 아이템이 없음 \n ${e.player.inventory.itemInOffHand}, $e")

        e.player.inventory.itemInOffHand.durability = configExtractor.reloadStateDurability
    }
    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        if (!isConfigEnabled(e.parentNode)) return

        val configExtractor = ConfigExtractor(e.parentNode)
        if (!GunModelingMgr.hasGunModelingInOffHand(e.player)) throw Exception("예상과 달리 OffHand에 GunModeling 아이템이 없음 \n ${e.player.inventory.itemInOffHand}, $e")

        e.player.inventory.itemInOffHand.durability = configExtractor.normalStateDurability
    }
}







object GunModelingMgr: Listener {
    // ITEM //
    private const val NBT_KEY = "GunModelingInOffHand"
    fun hasGunModelingInOffHand(p: Player): Boolean {
        return isGunModelingItem(p.inventory.itemInOffHand)
    }
    private fun isGunModelingItem(item: ItemStack): Boolean {
        if (item.type == Material.AIR) return false
        val nbti = NBTItem(item); return nbti.hasKey(NBT_KEY)
    }
    fun makeGunModelingForOffHand(node: String, normalDurability: Short): ItemStack {
        val item = CrackShotAddition.csMinion.parseItemStack(CrackShotAddition.csDirector.getString("$node.Item_Type"))
        item.itemMeta.displayName = ""
        item.durability = normalDurability
        val nbti = NBTItem(item); nbti.setBoolean(NBT_KEY, true)
        item.itemMeta = nbti.item.itemMeta
        return item
    }


    // LISTENER //

    // 왼손 슬롯 벤 //
    @EventHandler(priority = EventPriority.LOW)
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory != null && e.clickedInventory.type == InventoryType.PLAYER
                && e.slot == 40) {
            e.isCancelled = true
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerSwapHandItem(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true
    }


    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (isGunModelingItem(e.player.inventory.itemInOffHand)) {
            e.player.inventory.itemInOffHand = null
        }
    }
}