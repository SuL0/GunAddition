package kr.sul.crackshotaddition.infomanager.extractor

import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csMinion
import kr.sul.crackshotaddition.infomanager.ammo.Ammo
import kr.sul.crackshotaddition.infomanager.nbtleftammo.ItemLeftAmmoAmtNbt
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.util.UniqueIdAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// 크랙샷인 item의 정보를 쉽게 뽑아내주는 클래스(Wrapper?)
class WeaponInfoExtractor(private val p: Player, val item: ItemStack) {
    // 기본적인 정보
    val parentNode: String = csMinion.getWeaponParentNodeFromNbt(item)
    val uniqueId: String
        get() = UniqueIdAPI.getUniqueID(item)
    val configName: String
        get() = csDirector.getString("$parentNode.Item_Information.Item_Name")
    val nbtName: String
        get() = csMinion.getWeaponNbtName(item)


    // 총알에 관련된 정보
    val reloadEnabled: Boolean
        get() = run { csDirector.getBoolean("$parentNode.Ammo.Enable") }  // default가 false
    val leftAmmoAmt: Int?
        get() = run { if (!reloadEnabled) return null;  ItemLeftAmmoAmtNbt.getLeftSideAmmoAmt(p, item) }
    val rightAmmoAmt: Int?
        get() = run { if (!reloadEnabled) return null;  ItemLeftAmmoAmtNbt.getRightSideAmmoAmt(p, item) }
    val reloadCapacity: Int?
        get() = run {
            if (!reloadEnabled) return null;  val retVal = csDirector.getReloadAmount(p, parentNode, item)  // default가 0
            if (retVal != 0) retVal else null
        }
    val ammoNeeded: Ammo?
        get() = run { if (!reloadEnabled) return null;  Ammo.getAmmoNeeded(parentNode) }
    val takeAsMagazine: Boolean?
        get() = run { if (!reloadEnabled) return null;  csDirector.getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine") } // default가 false

    fun isDualWield(): Boolean {
        return csDirector.isDualWield(p, parentNode, item) // 여기서 총알 비교 추가 ㄴㄴ. initializeLeftAmmo에서 오류남
    }
    fun hasAttachment(): Boolean {
        return csDirector.getString("$parentNode.Item_Information.Attachments.Type") != null   // returns main or accessory
    }

    init {
        if (!isValidCrackShotWeapon(item)) throw Exception("$p $item")
    }


    companion object {
        fun isValidCrackShotWeapon(item: ItemStack): Boolean {
            return CrackShotAdditionAPI.isValidCrackShotWeapon(item)
        }
    }

}