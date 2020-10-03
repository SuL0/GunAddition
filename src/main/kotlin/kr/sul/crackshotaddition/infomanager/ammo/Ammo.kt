package kr.sul.crackshotaddition.infomanager.ammo

import kr.sul.crackshotaddition.CrackShotAddition
import org.bukkit.inventory.ItemStack

// Material이랑 비슷하게 설계했음. Ammo는 종류별로 객체가 한개씩 있고, 그걸 계속 가져다 쓰게끔.
data class Ammo(private val id: Int, private val durability: Short, val dontUseConstructor: Boolean) {
    val whereToUse = arrayListOf<String>() // ParentNode
    val itemStack = ItemStack(id, 1, durability)
    val itemInfo = "${itemStack.typeId}~${itemStack.durability}"


    companion object {
        private val csDirector = CrackShotAddition.csDirector

        val listOfAllAmmo = arrayListOf<Ammo>()

        init {
            // config에서부터 전체 ammo list 불러와서 ammoList에 넣기
            for (parentNode in csDirector.parentNodeList.values) {
                val ammoInfo = csDirector.getString("${parentNode}.Ammo.Ammo_Item_ID")
                if (ammoInfo != null) {
                    val splitedAmmoInfo = ammoInfo.split("~").map { it.toInt() }.toMutableList()
                    if (splitedAmmoInfo.size == 1) splitedAmmoInfo.add(0)

                    val ammo = getAmmo(splitedAmmoInfo[0], splitedAmmoInfo[1].toShort(), ifNotExistCreateOne = true)!!
                    ammo.whereToUse.add(parentNode)
                    listOfAllAmmo.add(ammo)
                }
            }
        }

        /* Ammo 가져오기 */
        fun of(itemStack: ItemStack): Ammo? {
            return getAmmo(itemStack.typeId, itemStack.durability)
        }
        fun of(id: Int, durability: Short): Ammo? {
            return getAmmo(id, durability)
        }
        private fun getAmmo(id: Int, durability: Short, ifNotExistCreateOne: Boolean = false): Ammo? {
            for (ammo in listOfAllAmmo.filter { id == it.itemStack.typeId && durability == it.itemStack.durability }) {
                return ammo
            }
            if (ifNotExistCreateOne) {
                return Ammo(id, durability, true)
            }
            return null
        }

        /* parentNode에 필요한 Ammo 가져오기 */
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