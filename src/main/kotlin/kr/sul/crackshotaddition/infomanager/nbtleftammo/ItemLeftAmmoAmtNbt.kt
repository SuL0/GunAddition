package kr.sul.crackshotaddition.infomanager.nbtleftammo

import com.shampaggon.crackshot.CSDirector
import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemLeftAmmoAmtNbt {
    private val csDirector = CrackShotAddition.csDirector

    private const val LEFTSIDE_AMMO_AMT_KEY = "csa.LeftSideAmmoAmt"
    private const val RIGHTSIDE_AMMO_AMT_KEY = "csa.RightSideAmmoAmt"


    fun getLeftSideAmmoAmt(p: Player, item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (!weaponInfo.reloadEnabled) return null

        val nbti = NBTItem(item)
        // NBT에 Left Ammo에 관한 값이 아예 없을 때 -> 아이템에 NBT 넣기
        if (!nbti.hasKey(LEFTSIDE_AMMO_AMT_KEY)) {
            updateLeftAmmoAmtNbt(p, item) { csDirector.getAmmoBetweenBrackets(p, it.parentNode, item) }
        }
        return nbti.getInteger(LEFTSIDE_AMMO_AMT_KEY)
    }
    fun getRightSideAmmoAmt(p: Player, item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (!weaponInfo.reloadEnabled) return null

        val nbti = NBTItem(item)
        // NBT에 Left Ammo에 관한 값이 아예 없을 때 -> 아이템에 NBT 넣기
        if (!nbti.hasKey(RIGHTSIDE_AMMO_AMT_KEY)) {
            updateLeftAmmoAmtNbt(p, item)
        }
        return nbti.getInteger(RIGHTSIDE_AMMO_AMT_KEY)
    }



    fun updateLeftAmmoAmtNbt(p: Player, item: ItemStack, howToGetLeftAmmoAmt: ((WeaponInfoExtractor) -> Int)?=null) {
        val weaponInfo = WeaponInfoExtractor(p, item)

        var leftSideAmmoAmt: Int? = null
        var rightSideAmmoAmt: Int? = null
        if (weaponInfo.reloadEnabled) {
            if (weaponInfo.isDualWield() || weaponInfo.hasAttachment()) {
                val dualAmmo = CSDirector.getInstance().grabDualAmmo(item, weaponInfo.parentNode)
                leftSideAmmoAmt = dualAmmo[0]
                rightSideAmmoAmt = dualAmmo[1]
            } else {
                leftSideAmmoAmt = run {
                    if (howToGetLeftAmmoAmt != null)
                        howToGetLeftAmmoAmt(weaponInfo)
                    else
                        csDirector.getAmmoBetweenBrackets(p, weaponInfo.parentNode, item)  // default way
                }
            }
        }
        setLeftAmmoAmtNbt(item, leftSideAmmoAmt!!, rightSideAmmoAmt)
    }
    private fun setLeftAmmoAmtNbt(item: ItemStack, leftAmmoAmt: Int, rightAmmoAmt: Int?) {
        val nbti = NBTItem(item)
        nbti.setInteger(LEFTSIDE_AMMO_AMT_KEY, leftAmmoAmt)
        if (rightAmmoAmt != null) nbti.setInteger(RIGHTSIDE_AMMO_AMT_KEY, rightAmmoAmt)
        item.itemMeta = nbti.item.itemMeta
    }
}