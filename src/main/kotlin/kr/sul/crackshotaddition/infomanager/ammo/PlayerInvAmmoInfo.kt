package kr.sul.crackshotaddition.infomanager.ammo

import com.shampaggon.crackshot.CSMinion
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PlayerInvAmmoInfo(val p: Player) {
    val allOfPossessedAmmoAmt = hashMapOf<Ammo, Int>()

    // 초기화 - 보유한 총알 개수 설정
    init {
        for (ammo in Ammo.listOfAllAmmo) {
            allOfPossessedAmmoAmt[ammo] = 0
            update(p, ammo)
        }
    }

    // GET //
    fun getReloadableAmountPerWeapon(item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (weaponInfo.ammoNeeded == null) return null

        val possessedAmmoAmt = allOfPossessedAmmoAmt[weaponInfo.ammoNeeded]!!
        val reloadCapacity = weaponInfo.reloadCapacity!!
        return possessedAmmoAmt * reloadCapacity
    }
    fun getPossessedAmmoAmt(ammo: Ammo): Int {
        return allOfPossessedAmmoAmt[ammo]!!
    }


    // UPDATE //
    fun updateAll(p: Player): Ammo? { // Ammo: 실질적인 총알 개수의 변화가 있었던 총알. 없었으면 null
        val notUpdatedPossessedAmmoAmtMap = HashMap(allOfPossessedAmmoAmt)
        allOfPossessedAmmoAmt.keys.forEach { update(p, it) }
        for ((key, value) in notUpdatedPossessedAmmoAmtMap) {
            if (allOfPossessedAmmoAmt[key]!! != value) {  // 이전의 Map과 비교해서 총알 개수의 차이가 있는가
                return key
            }
        }
        return null
    }
    fun update(p: Player, ammo: Ammo): Boolean { // Boolean: 실질적인 총알 개수의 변화가 있었는가
        val notUpdatedAmmoAmt = allOfPossessedAmmoAmt[ammo]
        allOfPossessedAmmoAmt[ammo] = CSMinion.getInstance().countItemStacks(p, ammo.itemInfo, null)
        return (allOfPossessedAmmoAmt[ammo]!! != notUpdatedAmmoAmt)
    }
}