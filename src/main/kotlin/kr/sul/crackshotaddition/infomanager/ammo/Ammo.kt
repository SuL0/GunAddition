package kr.sul.crackshotaddition.infomanager.ammo

import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// Material이랑 비슷하게 설계했음. Ammo는 "종류별로 객체가 한개씩" 있고, 그걸 가져다 쓰게끔. //
data class Ammo internal constructor(private val id: Int, private val durability: Short, val noteDontUseThisConstructorExceptInternalInitialization: Boolean) {
    val whereToUse = arrayListOf<String>() // ParentNodes
    val itemStack = ItemStack(id, 1, durability)
    val itemInfo = "${itemStack.typeId}~${itemStack.durability}"


    // COMPANION OBJECT //
    companion object {
        val listOfAllAmmo = arrayListOf<Ammo>()

        // INITIALIZE //
        // config에서부터 전체 ammo list 불러와서 ammoList에 넣기 //
        init {
            for (parentNode in csDirector.parentNodeList.values) {
                val ammoInfo = csDirector.getString("${parentNode}.Ammo.Ammo_Item_ID")
                if (ammoInfo != null) {
                    val splitedAmmoInfo = ammoInfo.split("~").map { it.toInt() }.toMutableList()
                    if (splitedAmmoInfo.size == 1) splitedAmmoInfo.add(0)

                    val ammo = getAmmo(splitedAmmoInfo[0], splitedAmmoInfo[1].toShort(), initializeMode = true)!!
                    ammo.whereToUse.add(parentNode)
                    listOfAllAmmo.add(ammo)
                }
            }
        }


        // METHODS //

        // 체크 //
        fun isAmmoItem(item: ItemStack): Boolean {
            return of(item) != null
        }

        // Ammo 가져오기 //
        fun of(item: ItemStack): Ammo? { return getAmmo(item.typeId, item.durability) }
        fun of(id: Int, durability: Short): Ammo? { return getAmmo(id, durability) }

        private fun getAmmo(id: Int, durability: Short, initializeMode: Boolean = false): Ammo? {
            // listOfAllAmmo에서 검색하기
            for (ammo in listOfAllAmmo.filter { id == it.itemStack.typeId && durability == it.itemStack.durability }) {
                return ammo
            }
            // initializeMode: 위의 init에서만 true = 초기화 용도
            if (initializeMode) {
                return Ammo(id, durability, true)
            }
            return null
        }

        // 총기에 필요한 Ammo 가져오기 //
        fun getAmmoNeeded(p: Player, item: ItemStack): Ammo? {
            val parentNode = WeaponInfoExtractor(p, item).parentNode
            return getAmmoNeeded(parentNode)
        }
        fun getAmmoNeeded(parentNode: String): Ammo? {
            val ammoInfo = csDirector.getString("$parentNode.Ammo.Ammo_Item_ID")
            return if (ammoInfo != null) {
                val splitedAmmoInfo = ammoInfo.split("~").map { it.toInt() }.toMutableList()
                if (splitedAmmoInfo.size == 1) splitedAmmoInfo.add(0)
                getAmmo(splitedAmmoInfo[0], splitedAmmoInfo[1].toShort())
            } else null
        }
    }
}