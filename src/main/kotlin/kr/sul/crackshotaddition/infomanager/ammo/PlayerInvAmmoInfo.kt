package kr.sul.crackshotaddition.infomanager.ammo

import com.shampaggon.crackshot.CSMinion
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PlayerInvAmmoInfo(val p: Player) {
    val allOfPossessedAmmoAmt = hashMapOf<AmmoType, Int>()

    // 초기화 - 보유한 총알 개수 설정
    init {
        for (ammo in AmmoType.listOfAllAmmo) {
            allOfPossessedAmmoAmt[ammo] = 0
            update(p, ammo)
        }
    }

    // GET //
    fun getReloadableAmountPerWeapon(item: ItemStack): Int? {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (weaponInfo.ammoTypeNeeded == null) return null

        val possessedAmmoAmt = allOfPossessedAmmoAmt[weaponInfo.ammoTypeNeeded]!!
        val reloadCapacity = weaponInfo.reloadCapacity!!
        return possessedAmmoAmt * reloadCapacity
    }
    fun getPossessedAmmoAmt(ammoType: AmmoType): Int {
        return allOfPossessedAmmoAmt[ammoType]!!
    }


    // UPDATE //
    fun updateAll(p: Player): AmmoType? { // Ammo: 실질적인 총알 개수의 변화가 있었던 총알. 없었으면 null
        val notUpdatedPossessedAmmoAmtMap = HashMap(allOfPossessedAmmoAmt)
        allOfPossessedAmmoAmt.keys.forEach { update(p, it) }
        for ((key, value) in notUpdatedPossessedAmmoAmtMap) {
            if (allOfPossessedAmmoAmt[key]!! != value) {  // 이전의 Map과 비교해서 총알 개수의 차이가 있는가
                return key
            }
        }
        return null
    }
    fun update(p: Player, ammoType: AmmoType): Boolean { // Boolean: 실질적인 총알 개수의 변화가 있었는가
        val notUpdatedAmmoAmt = allOfPossessedAmmoAmt[ammoType]
        allOfPossessedAmmoAmt[ammoType] = CSMinion.getInstance().countItemStacks(p, ammoType.itemInfo, null)
        return (allOfPossessedAmmoAmt[ammoType]!! != notUpdatedAmmoAmt)
    }
}