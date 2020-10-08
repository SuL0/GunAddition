package kr.sul.crackshotaddition.infomanager.nbtleftammo

import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


// reloadEnabled 체크는 하면안됨(throw exception). false라면 총알수는 "null"이 아닌 "Infinity"이기 때문
object ItemLeftAmmoAmtNbt {
    internal const val LEFTSIDE_AMMO_AMT_KEY = "csa.LeftSideAmmoAmt"
    internal const val RIGHTSIDE_AMMO_AMT_KEY = "csa.RightSideAmmoAmt"

    // GETTER //
    fun getLeftSideAmmoAmt(item: ItemStack): Int {
        val nbti = NBTItem(item)
        if (!nbti.hasKey(LEFTSIDE_AMMO_AMT_KEY)) throw Exception("$item")
        return nbti.getInteger(LEFTSIDE_AMMO_AMT_KEY)
    }
    fun getRightSideAmmoAmt(item: ItemStack): Int? {
        val nbti = NBTItem(item)
        if (!nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) return null
        return nbti.getInteger(RIGHTSIDE_AMMO_AMT_KEY)
    }


    // Item NBT에 Left Ammo를 기록하기 //
    fun updateLeftAmmoAmtNbt(p: Player, item: ItemStack) {
        val weaponInfo = WeaponInfoExtractor(p, item)
        val leftSideAmmoAmt: Int
        var rightSideAmmoAmt: Int? = null
        if (weaponInfo.isDualWield() || weaponInfo.hasAttachment()) {
            val dualAmmo = csDirector.grabDualAmmo(item, weaponInfo.parentNode)
            leftSideAmmoAmt = dualAmmo[0]
            rightSideAmmoAmt = dualAmmo[1]
        } else {
            leftSideAmmoAmt = csDirector.getAmmoBetweenBrackets(p, weaponInfo.parentNode, item)
        }

        setLeftAmmoAmtNbt(item, leftSideAmmoAmt, rightSideAmmoAmt)
    }
    private fun setLeftAmmoAmtNbt(item: ItemStack, leftAmmoAmt: Int, rightAmmoAmt: Int?) {
        val nbti = NBTItem(item)
        nbti.setInteger(LEFTSIDE_AMMO_AMT_KEY, leftAmmoAmt)
        if (rightAmmoAmt != null) {
            nbti.setInteger(RIGHTSIDE_AMMO_AMT_KEY, rightAmmoAmt)
        } else if (nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) { // config가 바껴서 RightSideAmmo가 있다가 없어진 상황을 위함
            nbti.removeKey(RIGHTSIDE_AMMO_AMT_KEY)
        }
        item.itemMeta = nbti.item.itemMeta
    }
}