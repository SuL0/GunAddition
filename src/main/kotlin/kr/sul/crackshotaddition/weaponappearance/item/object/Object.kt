package kr.sul.crackshotaddition.weaponappearance.item.`object`

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent

interface Object {
    fun isConfigEnabled(parentNode: String): Boolean
    // 스왑 //
    fun onHeldItemIsChangedToCrackShotWeapon(e: PlayerHeldItemIsChangedToAnotherEvent)
    // 줌 //
    fun onZoom(e: WeaponScopeEvent)
    // 리로드 //
    fun onReload(e: WeaponReloadEvent)
    fun onReloadComplete(e: WeaponReloadCompleteEvent)
}