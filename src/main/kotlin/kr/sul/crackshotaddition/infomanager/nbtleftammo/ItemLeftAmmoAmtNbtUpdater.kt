package kr.sul.crackshotaddition.infomanager.nbtleftammo

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import kr.sul.crackshotaddition.infomanager.nbtleftammo.ItemLeftAmmoAmtNbt.LEFTSIDE_AMMO_AMT_KEY
import kr.sul.crackshotaddition.infomanager.nbtleftammo.ItemLeftAmmoAmtNbt.RIGHTSIDE_AMMO_AMT_KEY
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

// 여기서 WeaponInfoExtractor이 Left Ammo에 관해 이 클래스에 의존성을 가지니, 여기서 WeaponInfoExtractor의 Left Ammo에 관한 메소드는 쓰면 안됨
object ItemLeftAmmoAmtNbtUpdater: Listener {

    // NBT를 처음 초기화해주는 목적으로 넣었음. (먼저 초기화 안해주면, 밑의 -1, +1에서 문제여부가 있기에) //
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemHeld(e: InventoryItemChangedEvent) {
        val p = e.player
        val newItem = e.newItemStack
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(newItem)) return

        val weaponInfo = WeaponInfoExtractor(p, newItem)
        val nbti = NBTItem(newItem)
        if (weaponInfo.reloadEnabled && nbti.hasKey(LEFTSIDE_AMMO_AMT_KEY) || nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) return  // 무조건 NBT 없을 때만 초기화
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, newItem)  // initialize left ammo nbt
    }

    // 총 쏠 때 Left Ammo NBT 변경 //
    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem) { it.leftAmmoAmt!! - 1 }
    }

    // 장전할 때 Left Ammo NBT 변경 //
    @EventHandler
    fun onReload(e: WeaponReloadCompleteEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem) { 
            if (it.takeAsMagazine!!) {
                it.reloadCapacity!!
            } else {
                it.leftAmmoAmt!! + 1
            }
        }
    }
}