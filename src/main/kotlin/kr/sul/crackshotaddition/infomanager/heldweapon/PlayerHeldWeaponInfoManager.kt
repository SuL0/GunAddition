package kr.sul.crackshotaddition.infomanager.heldweapon

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.infomanager.ammo.Ammo
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToOnotherEvent
import kr.sul.servercore.util.UniqueIdAPI
import me.sul.customentity.util.DebugUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

// NOTE: 주의) PlayerHeldItemIsChangedToOnotherEvent의 이벤트 조건 중 하나인 PlayerItemHeldEvent은 cancellable이라서 EventPriority가 HIGHEST로 해놓아서 '무조건 PlayerItemHeldEvent보다 뒤에 호출됨.'
// NOTE: -> PlayerItemHeldEvent에서는 쓰지 마라 ! ! ! !
// NOTE: 주무기에 대한 모든 정보를 Metadata에 넣어서 관리해주는 클래스
// 데이터의 통일성을 위해 여기서 HeldItemSlot을 통해 getItemStack() 메소드를 제공하고 있음. (PlayerItemHeldEvent에서 p.getInventory().getItemInMainHand(){=이전템 나옴} 를 하는 등의 실수를 막기위함.)
object PlayerHeldWeaponInfoManager : Listener {
    data class CrackShotWeaponInfo(val p: Player, val heldItemSlot: Int, val parentNode: String, val configName: String, val uniqueId: String,  // Essential Info
                                   var reloadEnabled: Boolean=false, var leftAmmoAmount: Int?=null, var rightAmmoAmount: Int?=null, var reloadCapacity: Int?=null,  // Ammo Info
                                   var ammo: Ammo?=null, var takeAsMagazine: Boolean?=null) {
        fun verifyData(): Boolean {
            val heldItem = getHeldItem()
            if (!(UniqueIdAPI.hasUniqueID(heldItem) &&
                            UniqueIdAPI.getUniqueID(heldItem) == uniqueId)) { // 메인 아이템이 예상템과 다를 때(UID이용)
                playersWeaponInfoMap[p] = null
                DebugUtil.printStackTrace()
                return false
            }
            return true
        }

        fun getHeldItem(): ItemStack {
            return p.inventory.getItem(heldItemSlot)
        }
        fun isDualWield(): Boolean {
            return leftAmmoAmount!=null && rightAmmoAmount!=null
        }
    }

    private val playersWeaponInfoMap = hashMapOf<Player, CrackShotWeaponInfo?>()

    @JvmStatic
    fun getInfo(p: Player): CrackShotWeaponInfo? {
        return if (isSet(p)) playersWeaponInfoMap[p] else null
    }
    @JvmStatic
    fun isSet(p: Player, passVerifyingData: Boolean = false): Boolean {  // 이 클래스 내부에서는 isSet()을 쓰지 않는게 좋음
        if (playersWeaponInfoMap.containsKey(p) && playersWeaponInfoMap[p] != null) {
            return (passVerifyingData || playersWeaponInfoMap[p]!!.verifyData())
        }
        return false
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerHeldItemChanged(e: PlayerHeldItemIsChangedToOnotherEvent) {
        val p = e.player
        val newIs = e.newItemStack
        if (!e.isChangedToCrackShotWeapon()) {
            // 단순히 데이터 남아있으면 삭제하는 것
            if (isSet(p, passVerifyingData = true)) { // 아이템이 바뀌어서 호출된 이벤트니, getItemStack이 예상 값과 다를 수 밖에 없어서 Exception 발생함 -> verifyData는 스킵해야 함
                playersWeaponInfoMap[p] = null
            }
            return
        }

        val heldItemSlot = e.newSlot
        val parentNode = CrackShotAdditionAPI.getWeaponParentNode(newIs) ?: throw Exception("$newIs")
        val configName = CrackShotAdditionAPI.getWeaponConfigName(parentNode) ?: throw Exception(parentNode)
        val uniqueId = UniqueIdAPI.getUniqueID(newIs)

        val weaponInfo = CrackShotWeaponInfo(p, heldItemSlot, parentNode, configName, uniqueId)
        // 총알에 관한 정보
        weaponInfo.run {
            reloadEnabled = CSDirector.getInstance().getBoolean("$parentNode.Ammo.Enable")
            if (!reloadEnabled) return@run

            if (CSDirector.getInstance().isDualWield(p, parentNode, newIs)) {
                val dualAmmo = CSDirector.getInstance().grabDualAmmo(newIs, parentNode)
                leftAmmoAmount = dualAmmo[0]
                rightAmmoAmount = dualAmmo[1]
            } else {
                leftAmmoAmount = CrackShotAdditionAPI.getWeaponAmmoAmount(p, parentNode, newIs)
            }

            if (leftAmmoAmount != null) {
                reloadCapacity = CrackShotAdditionAPI.getWeaponReloadCapacity(p, parentNode, newIs)
                takeAsMagazine = CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine")
                if (CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_On_Reload")) {
                    ammo = Ammo.getAmmoNeeded(parentNode)
                }
            }
        }
        playersWeaponInfoMap[p] = weaponInfo
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onShoot(e: WeaponShootEvent) {
        val p = e.player
        val weaponInfo = getInfo(p) ?: throw Exception("$e")
        weaponInfo.run {
            if (isDualWield()) {
                if (leftAmmoAmount == null || rightAmmoAmount == null) throw Exception("$e");
                val dualAmmo = CSDirector.getInstance().grabDualAmmo(p.inventory.itemInMainHand, e.weaponTitle)
                leftAmmoAmount = dualAmmo[0]
                rightAmmoAmount = dualAmmo[1]
            } else {
                if (leftAmmoAmount == null) throw Exception("$e")
                leftAmmoAmount = leftAmmoAmount!! -1
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val p = e.player
        val weaponInfo = getInfo(p) ?: throw Exception("$e")
        weaponInfo.run {
            if (reloadCapacity == null) throw Exception("$e")

            if (isDualWield()) {
                // reloadAmt/2 안해도 됨
                leftAmmoAmount = reloadCapacity
                rightAmmoAmount = reloadCapacity
            } else {
                if (takeAsMagazine == null) throw Exception("$e")
                leftAmmoAmount = if (takeAsMagazine!!) reloadCapacity else leftAmmoAmount?.plus(1)
            }
        }
    }


    @EventHandler
    fun onQuit(e: PlayerQuitEvent) { playersWeaponInfoMap.remove(e.player) }
}
