package kr.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import kr.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent
import kr.sul.servercore.serialnumber.UniqueIdAPI
import me.sul.customentity.util.DebugUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

// NOTE: 주의) PlayerMainItemChangedConsideringUidEvent의 이벤트 조건 중 하나인 PlayerItemHeldEvent은 cancellable이라서 EventPriority가 HIGHEST로 해놓아서 '무조건 PlayerItemHeldEvent보다 뒤에 호출됨.'
// NOTE: -> PlayerItemHeldEvent에서는 쓰지 마라 ! ! ! !
// NOTE: 주무기에 대한 모든 정보를 Metadata에 넣어서 관리해주는 클래스
// 데이터의 통일성을 위해 여기서 MainItemSlot을 통해 getItemStack() 메소드를 제공하고 있음. (PlayerItemHeldEvent에서 p.getInventory().getItemInMainHand(){=이전템 나옴} 를 하는 등의 실수를 막기위함.)
object MainCrackShotWeaponInfoManager : Listener {
    data class CrackShotWeaponInfo(val p: Player, val mainItemSlot: Int, val parentNode: String, val configName: String, val uniqueId: String,
                                   val ammoInfo: CrackShotWeaponAmmoInfo) {
        fun getMainItem(): ItemStack {
            return p.inventory.getItem(mainItemSlot)
        }

        fun verifyData(): Boolean {
            val mainItem = getMainItem()
            if (!(UniqueIdAPI.hasUniqueID(mainItem) &&
                            UniqueIdAPI.getUniqueID(mainItem) == uniqueId)) { // 메인 아이템이 예상템과 다를 때(UID이용)
                removeCrackShotWeaponInfo(p)
                DebugUtil.printStackTrace()
                return false
            }
            return true
        }
    }
    data class CrackShotWeaponAmmoInfo(val enabled: Boolean, var leftAmmoAmount: Int?=null, var rightAmmoAmount: Int?=null, var reloadAmount: Int?=null,
                                       var ammoMaterial: Material?=null, var takeAsMagazine: Boolean?=null, var possessedExtraAmmoAmount: Int?=null) {
        fun isDualWield(): Boolean {
            return leftAmmoAmount!=null && rightAmmoAmount!=null
        }
    }

    // possessedExtraAmmoAmount: (*reloadAmountPerAmmo 없음) - 이거 없애야 할 듯
    // reloadAmountPerAmmo 대신 Take_Ammo_As_Magazine를 넣어야 할 듯

    private val weaponInfoMap = hashMapOf<Player, CrackShotWeaponInfo?>()


    @JvmStatic
    fun get(p: Player): CrackShotWeaponInfo? {
        return if (isSet(p)) weaponInfoMap[p] else null
    }
    @JvmStatic
    fun isSet(p: Player, passVerifyingData: Boolean = false): Boolean {  // 이 클래스 내부에서는 isSet()을 쓰지 않는게 좋음
        if (weaponInfoMap.containsKey(p) && weaponInfoMap[p] != null) {
            return (passVerifyingData || weaponInfoMap[p]!!.verifyData())
        }
        return false
    }

    @JvmStatic
    private fun removeCrackShotWeaponInfo(p: Player) {
        Bukkit.getServer().broadcastMessage("§csetToNull")
        weaponInfoMap[p] = null
    }




    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerMainItemChanged(e: PlayerMainItemChangedConsideringUidEvent) {
        val p = e.player
        val newIs = e.newItemStack
        if (!e.isChangedToCrackShotWeapon) {
            // 단순히 데이터 남아있으면 삭제하는 것
            if (isSet(p, passVerifyingData = true)) { // 아이템이 바뀌어서 호출된 이벤트니, getItemStack이 예상 값과 다를 수 밖에 없어서 Exception 발생함 -> verifyData는 스킵해야 함
                removeCrackShotWeaponInfo(p)
            }
            return
        }

        val mainItemSlot = e.newSlot
        val parentNode = CrackShotAdditionAPI.getWeaponParentNode(newIs) ?: throw Exception("$newIs")
        val configName = CrackShotAdditionAPI.getWeaponConfigName(parentNode) ?: throw Exception(parentNode)
        val uniqueId = UniqueIdAPI.getUniqueID(newIs) ?: throw Exception("$newIs")

        // CrackShotWeaponAmmoInfo 설정
        val ammoInfo = CrackShotWeaponAmmoInfo(CSDirector.getInstance().getBoolean("$parentNode.Ammo.Enable"))
        ammoInfo.run {
            if (enabled) {
                if (CSDirector.getInstance().isDualWield(p, parentNode, newIs)) {
                    val dualAmmo = CSDirector.getInstance().grabDualAmmo(newIs, parentNode)
                    leftAmmoAmount = dualAmmo[0]
                    rightAmmoAmount = dualAmmo[1]
                } else {
                    leftAmmoAmount = CrackShotAdditionAPI.getWeaponAmmoAmount(p, parentNode, newIs)
                }

                if (leftAmmoAmount != null) {
                    reloadAmount = CrackShotAdditionAPI.getWeaponReloadAmount(p, parentNode, newIs)
                    takeAsMagazine = CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine")
                    if (CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_On_Reload")) {
                        ammoMaterial = Material.getMaterial(CSDirector.getInstance().getString("$parentNode.Ammo.Ammo_Item_ID").split("~").toTypedArray()[0].toInt()) // '~' 를 고려해야함
                        if (ammoMaterial != null && reloadAmount != null) {
                            possessedExtraAmmoAmount = countPossessedAmmoAmount(p, ammoMaterial!!, reloadAmount!!)
                        }
                    }
                }
            }
        }

        Bukkit.getServer().broadcastMessage("§aput weaponInfoMap")
        weaponInfoMap[p] = CrackShotWeaponInfo(p, mainItemSlot, parentNode, configName, uniqueId, ammoInfo)
    }

    /* 가변적인 값 관리 */  // requiredAmmoMaterial이 이벤트 대상이면 POSSESSED_EXTRA_AMMO 값을 업데이트
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemChanged(e: InventoryItemChangedEvent) {
        val p = e.player
        val weaponInfo = get(p) ?: return
        val requiredAmmoMaterial = weaponInfo.ammoInfo.ammoMaterial
        val reloadAmtPerAmmo = weaponInfo.ammoInfo.reloadAmount
        if (requiredAmmoMaterial != null && reloadAmtPerAmmo != null && (e.itemStack.type == requiredAmmoMaterial || e.itemStack.type == Material.AIR)) {
            weaponInfoMap[p]?.ammoInfo?.possessedExtraAmmoAmount = countPossessedAmmoAmount(p, requiredAmmoMaterial, reloadAmtPerAmmo)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onShoot(e: WeaponShootEvent) {
        val p = e.player
        val weaponInfo = get(p) ?: throw Exception()
        weaponInfo.ammoInfo.run {
            if (isDualWield()) {
                if (leftAmmoAmount == null || rightAmmoAmount == null) throw Exception();
                val dualAmmo = CSDirector.getInstance().grabDualAmmo(p.inventory.itemInMainHand, e.weaponTitle)
                leftAmmoAmount = dualAmmo[0]
                rightAmmoAmount = dualAmmo[1]
            } else {
                if (leftAmmoAmount == null) throw Exception();
                leftAmmoAmount = leftAmmoAmount!! -1;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val p = e.player
        val weaponInfo = get(p) ?: throw Exception()
        weaponInfo.ammoInfo.run {
            if (reloadAmount == null) throw Exception()

            if (isDualWield()) {
                // reloadAmt/2 안해도 됨
                leftAmmoAmount = reloadAmount
                rightAmmoAmount = reloadAmount
            } else {
                if (takeAsMagazine == null) throw Exception()
                leftAmmoAmount = if (takeAsMagazine!!) reloadAmount else leftAmmoAmount?.plus(1)
            }
        }
    }

    /* 램 관리 */
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) { removeCrackShotWeaponInfo(e.player) }

    private fun countPossessedAmmoAmount(p: Player, ammoMaterial: Material, multiplication: Int): Int {
        var amount = 0
        for (loopIs in p.inventory.contents) {
            if (loopIs == null) continue
            if (loopIs.type == ammoMaterial) {
                amount += loopIs.amount * multiplication
            }
        }
        return amount
    }
}
