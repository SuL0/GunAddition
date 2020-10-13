package kr.sul.crackshotaddition.infomanager.extractor

import com.shampaggon.crackshot.WeaponNbtParentNodeManager
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csMinion
import kr.sul.crackshotaddition.infomanager.ammo.Ammo
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.util.UniqueIdAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// 크랙샷인 item의 정보를 쉽게 뽑아내주는 클래스(Wrapper?)
class WeaponInfoExtractor(private val p: Player?=null, val item: ItemStack) {
    init {
        if (!isValidCrackShotWeapon(item)) throw Exception("$p $item")
    }
    companion object {
        fun isValidCrackShotWeapon(item: ItemStack): Boolean {
            return CrackShotAdditionAPI.isValidCrackShotWeapon(item)
        }
    }


    // 기본적인 정보 //
    // 웬만하면 parentNode 쓰고, mainFixedParentNode는 Addition: 의 기능들, 즉 메인 악세 상관없이 적용되는 기능에만  사용
    val mainFixedParentNode: String = WeaponNbtParentNodeManager.getWeaponParentNodeFromNbt(item)
    // parentNode의 run {} 안에서 parentNode(재귀) 를 사용하지 않도록 주의
    val parentNode: String
        get () = run {
            // 부착물이 있으며, 부착물을 사용중인 상태일 때
            if (hasAttachment() && csMinion.getWeaponNbtName(item).contains("▶")) {
                csDirector.getAttachment(mainFixedParentNode, null)[1]
            } else {
                mainFixedParentNode
            }
        }

    val uniqueId: String
        get() = UniqueIdAPI.getUniqueID(item)
    val mainFixedConfigName: String
        get() = csDirector.getString("$mainFixedParentNode.Item_Information.Item_Name")
    val configName: String
        get() = csDirector.getString("$parentNode.Item_Information.Item_Name")
    val nbtName: String
        get() = csMinion.getWeaponNbtName(item)
    val bRemoveUnusedTag: Boolean
        get() = csDirector.getBoolean("$parentNode.Item_Information.Remove_Unused_Tag")


    // 총알에 관련된 정보 //
    val reloadEnabled: Boolean
        get() = csDirector.getBoolean("$parentNode.Reload.Enable")  // default가 false
    // reloadEnabled가 false라면, 총알수는 "null"이 아닌 "Infinity"라는 것을 명심
    val leftAmmoAmt: Int
        get() = run {
            if (isDualWield() || hasAttachment())
                csDirector.grabDualAmmo(item, parentNode)[0]
            else
                csDirector.getAmmoBetweenBrackets(p, parentNode, item)
        }
    val rightAmmoAmt: Int?
        get() = run {
            if (isDualWield() || hasAttachment())
                return csDirector.grabDualAmmo(item, parentNode)[1]
            return null
        }
    val reloadCapacity: Int?
        get() = run {
            if (!reloadEnabled) return null;
            csDirector.getReloadAmount(p, parentNode, item)
        }
    val ammoNeeded: Ammo?
        get() = run { if (!reloadEnabled) return null;  Ammo.getAmmoNeeded(parentNode) }
    val takeAsMagazine: Boolean?
        get() = run { if (!reloadEnabled) return null;  csDirector.getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine") } // default가 false


    // 기타 //
    fun isDualWield(): Boolean {
        return csDirector.isDualWield(p, parentNode, item) // 여기서 총알 비교 추가 ㄴㄴ. initializeLeftAmmo에서 오류남
    }
    fun hasAttachment(): Boolean {
        return csDirector.getString("$mainFixedParentNode.Item_Information.Attachments.Type") != null  // return main or accessory
    }
    fun selectIsLeft(): Boolean {
        if (csMinion.getWeaponNbtName(item).contains("▶")) return false
        return true
    }
}