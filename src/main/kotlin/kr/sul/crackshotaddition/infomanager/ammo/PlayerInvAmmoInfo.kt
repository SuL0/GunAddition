package kr.sul.crackshotaddition.infomanager.ammo

import com.shampaggon.crackshot.CSMinion
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PlayerInvAmmoInfo(p: Player) {
    val possessedAmmoAmount = hashMapOf<Ammo, Int>()

    init {
        for (ammo in Ammo.listOfAllAmmo) {
            possessedAmmoAmount[ammo] = 0
            update(p, ammo)
        }
    }

    fun getReloadableAmountPerWeapon(p: Player, item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (weaponInfo.ammoNeeded == null) return null

        val possessedAmmoAmt = possessedAmmoAmount[weaponInfo.ammoNeeded]!!
        val reloadCapacity = weaponInfo.reloadCapacity!!
        return possessedAmmoAmt * reloadCapacity
    }

    fun updateAll(p: Player) {
        possessedAmmoAmount.keys.forEach { update(p, it) }
    }
    fun update(p: Player, ammo: Ammo) {
        possessedAmmoAmount[ammo] = CSMinion.getInstance().countItemStacks(p, ammo.itemInfo, null)
    }
}