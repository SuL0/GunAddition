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

    // NBT를 처음 초기화해주는 목적으로 넣었음. (먼저 초기화를 안해주면, 다른 곳에서 getLeftSideAmmoAmt() 에서 null이 나오기 때문) //
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemHeld(e: InventoryItemChangedEvent) {
        val p = e.player
        val newItem = e.newItemStack
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(newItem)) return

        val nbti = NBTItem(newItem)
        // 무조건 NBT 없을 때만 초기화
        if (!nbti.hasKey(LEFTSIDE_AMMO_AMT_KEY) && !nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) {
            ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, newItem)  // Initialize left ammo nbt
        }
    }

    // 총 쏠 때 Left Ammo NBT 변경 //
    // TODO: AirStrike같은 일회성 아이템은 onShoot이 실행된다고 하면, 아이템이 파괴되니 오류가 발생할 것임. 즉 airStrike는 onShoot 이벤트에 걸리지 않아야 함. 또는 밑에 예외를 추가하거나
    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem)
    }

    // 장전할 때 Left Ammo NBT 변경 //
    @EventHandler
    fun onReload(e: WeaponReloadCompleteEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem)
    }
}