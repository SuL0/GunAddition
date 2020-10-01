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

    @JvmStatic
    fun isValidCrackShotWeapon(item: ItemStack): Boolean {
        return item.type != Material.AIR && getWeaponParentNode(item) != null &&
                    UniqueIdAPI.hasUniqueID(item)
    }

    @JvmStatic
    private fun getWeaponNbtName(item: ItemStack): String? {
        return csMinion.getWeaponNbtName(item)
    }

    // ParentNode를 NBT로부터 가져옴
    @JvmStatic
    fun getWeaponParentNode(item: ItemStack): String? {  // NOTE: 현재로선 부착물을 지원하지 않음
        return csMinion.getWeaponParentNodeFromNbt(item)
    }

    @JvmStatic
    fun getWeaponConfigName(item: ItemStack): String? { // 방법1
        return csDirector.getPureName(getWeaponNbtName(item))
    }
    @JvmStatic
    fun getWeaponConfigName(parentNode: String): String? { // 방법2
        return csDirector.getString("$parentNode.Item_Information.Item_Name")
    }

    @JvmStatic
    fun getWeaponReloadCapacity(p: Player, parent_node: String, item: ItemStack): Int {
        return csDirector.getReloadAmount(p, parent_node, item)
    }

    @JvmStatic
    fun getWeaponAmmoAmount(p: Player, parent_node: String, item: ItemStack): Int? {
        // NOTE: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
        // TODO: null도 포함해야 하는데, 이거 반환값이 Integer이 아니라 int인데?
        return csDirector.getAmmoBetweenBrackets(p, parent_node, item)
    }

    @JvmStatic
    fun getAmmoNeeded(parentNode: String): Ammo? {
        return Ammo.getAmmoNeeded(parentNode)
    }

    @JvmStatic
    fun isTakeAmmoAsMagazine(parentNode: String): Boolean {
        return CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine")
    }
}