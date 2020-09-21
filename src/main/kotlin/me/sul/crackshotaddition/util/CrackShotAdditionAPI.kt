package me.sul.crackshotaddition.util

import com.shampaggon.crackshot.CSDirector
import me.sul.crackshotaddition.CrackShotAddition
import me.sul.servercore.serialnumber.UniqueIdAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


// TODO: ItemStack? 를 ItemStack으로 바꿀까?
object CrackShotAdditionAPI {
    private val csDirector = CrackShotAddition.csDirector
    private val csUtility = CrackShotAddition.csUtility
    private val csMinion = CrackShotAddition.csMinion

    fun isValidCrackShotWeapon(item: ItemStack): Boolean {
        return item.type != Material.AIR && getWeaponParentNode(item) != null &&
                UniqueIdAPI.hasUniqueID(item)
    }

    private fun getWeaponNbtName(item: ItemStack): String? {
        return csMinion.getWeaponNbtName(item)
    }

    fun getWeaponParentNode(item: ItemStack): String? {  // NOTE: 현재로선 부착물을 지원하지 않음
        return csMinion.getWeaponParentNodeFromNbt(item)
    }

    fun getWeaponConfigName(item: ItemStack): String? { // 방법1
        return csDirector.getPureName(getWeaponNbtName(item))
    }

    fun getWeaponConfigName(parentNode: String): String? { // 방법2
        return csDirector.getString("$parentNode.Item_Information.Item_Name")
    }

    fun getWeaponReloadAmount(player: Player, parent_node: String, item: ItemStack): Int {
        return csDirector.getReloadAmount(player, parent_node, item)
    }

    fun getWeaponAmmoAmount(player: Player, parent_node: String, item: ItemStack): Int {
        // TODO: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
        return csDirector.getAmmoBetweenBrackets(player, parent_node, item)
    }
}