package kr.sul.crackshotaddition.util

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.CSMinion
import com.shampaggon.crackshot.WeaponNbtParentNodeMgr
import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.servercore.util.UniqueIdAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


// TODO: ItemStack? 를 ItemStack으로 바꿀까?
object CrackShotAdditionAPI {
    val csDirector: CSDirector get() = CrackShotAddition.csDirector
    val csMinion: CSMinion get() = CrackShotAddition.csMinion

    fun isValidCrackShotWeapon(item: ItemStack): Boolean {
        return item.type != Material.AIR
                && WeaponNbtParentNodeMgr.getWeaponParentNodeFromNbt(item) != null && UniqueIdAPI.hasUniqueID(item)
    }

    // 이걸 유닛 테스트 해보면 될 것 같음
    @JvmStatic
    fun getWeaponAmmoAmount(p: Player, parent_node: String, item: ItemStack): Int? {
        // NOTE: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
        // TODO: null도 포함해야 하는데, 이거 반환값이 Integer이 아니라 int인데?
        return csDirector.getAmmoBetweenBrackets(p, parent_node, item)
    }
}