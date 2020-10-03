package kr.sul.crackshotaddition.infomanager.nbtleftammo

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToOnotherEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

// 여기서 WeaponInfoExtractor이 Left Ammo에 관해 이 클래스에 의존성을 가지니, 여기서 WeaponInfoExtractor의 Left Ammo에 관한 메소드는 쓰면 안됨
object ItemLeftAmmoAmtNbtUpdater: Listener {
    @EventHandler
    fun onItemHeld(e: PlayerHeldItemIsChangedToOnotherEvent) {  // NBT를 처음 초기화해주는 목적으로 넣었음. (먼저 초기화 안해주면, 밑의 -1, +1에서 문제여부가 있기에)
        val p = e.player
        val heldItem = e.newItemStack
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(heldItem)) return
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem)
    }
    
    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem) { it.leftAmmoAmt!! - 1 }
    }

    @EventHandler
    fun onReload(e: WeaponReloadCompleteEvent) {
        val p = e.player
        val heldItem = p.inventory.itemInMainHand
        ItemLeftAmmoAmtNbt.updateLeftAmmoAmtNbt(p, heldItem) { 
            if (it.takeAsMagazine!!) it.reloadCapacity!! else it.leftAmmoAmt!! + 1
        }
    }
}