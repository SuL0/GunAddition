package kr.sul.crackshotaddition.infomanager.nbtleftammo

import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemLeftAmmoAmtNbt {
    internal const val LEFTSIDE_AMMO_AMT_KEY = "csa.LeftSideAmmoAmt"
    internal const val RIGHTSIDE_AMMO_AMT_KEY = "csa.RightSideAmmoAmt"


    fun getLeftSideAmmoAmt(p: Player, item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (!weaponInfo.reloadEnabled) return null

        val nbti = NBTItem(item)
        if (!nbti.hasKey(LEFTSIDE_AMMO_AMT_KEY)) return null
        return nbti.getInteger(LEFTSIDE_AMMO_AMT_KEY)
    }
    fun getRightSideAmmoAmt(p: Player, item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (!weaponInfo.reloadEnabled) return null

        val nbti = NBTItem(item)
        if (!nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) return null
        return nbti.getInteger(RIGHTSIDE_AMMO_AMT_KEY)
    }


    fun updateLeftAmmoAmtNbt(p: Player, item: ItemStack, howToGetLeftAmmoAmt: ((WeaponInfoExtractor) -> Int)?=null) {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (!weaponInfo.reloadEnabled) throw Exception("$p, $item, $howToGetLeftAmmoAmt")

        val leftSideAmmoAmt: Int
        var rightSideAmmoAmt: Int? = null
        if (weaponInfo.isDualWield() || weaponInfo.hasAttachment()) {
            val dualAmmo = csDirector.grabDualAmmo(item, weaponInfo.parentNode)
            leftSideAmmoAmt = dualAmmo[0]
            rightSideAmmoAmt = dualAmmo[1]
        } else {
            leftSideAmmoAmt = howToGetLeftAmmoAmt?.let { it(weaponInfo) } ?: csDirector.getAmmoBetweenBrackets(p, weaponInfo.parentNode, item)  // default way
        }

        setLeftAmmoAmtNbt(item, leftSideAmmoAmt, rightSideAmmoAmt)
    }
    private fun setLeftAmmoAmtNbt(item: ItemStack, leftAmmoAmt: Int, rightAmmoAmt: Int?) {
        val nbti = NBTItem(item)
        nbti.setInteger(LEFTSIDE_AMMO_AMT_KEY, leftAmmoAmt)
        if (rightAmmoAmt != null)
            nbti.setInteger(RIGHTSIDE_AMMO_AMT_KEY, rightAmmoAmt)
        item.itemMeta = nbti.item.itemMeta
    }
}