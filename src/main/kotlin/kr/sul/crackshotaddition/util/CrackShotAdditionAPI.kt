package kr.sul.crackshotaddition.util

import com.shampaggon.crackshot.CSDirector
import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.crackshotaddition.infomanager.ammo.Ammo
import kr.sul.servercore.util.UniqueIdAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


// TODO: ItemStack? 를 ItemStack으로 바꿀까?
object CrackShotAdditionAPI {
    val csDirector = CrackShotAddition.csDirector
    val csUtility = CrackShotAddition.csUtility
    val csMinion = CrackShotAddition.csMinion

    fun isValidCrackShotWeapon(item: ItemStack): Boolean {
        return item.type != Material.AIR
                && csMinion.getWeaponParentNodeFromNbt(item) != null && UniqueIdAPI.hasUniqueID(item)
    }
}