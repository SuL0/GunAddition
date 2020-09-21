package me.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import me.sul.crackshotaddition.util.CrackShotAdditionAPI
import me.sul.servercore.inventoryevent.InventoryItemChangedEvent
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent
import me.sul.servercore.serialnumber.UniqueIdAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

// NOTE: 주의) PlayerMainItemChangedConsideringUidEvent의 이벤트 조건 중 하나인 PlayerItemHeldEvent은 cancellable이라서 EventPriority가 HIGHEST로 해놓아서 '무조건 PlayerItemHeldEvent보다 뒤에 호출됨.'
// NOTE: -> PlayerItemHeldEvent에서는 쓰지 마라 ! ! ! !
// NOTE: 주무기에 대한 모든 정보를 Metadata에 넣어서 관리해주는 클래스
// 데이터의 통일성을 위해 여기서 MainItemSlot을 통해 getItemStack() 메소드를 제공하고 있음. (PlayerItemHeldEvent에서 p.getInventory().getItemInMainHand(){=이전템 나옴} 를 하는 등의 실수를 막기위함.)
class MainCrackShotWeaponInfoMetaManager : Listener {
    companion object {
        private val plugin = CrackShotAddition.instance
        private const val MAINITEM_SLOT_META = "csa.mainitem_slot" // 불변
        private const val PARENT_NODE_META = "csa.parent_node" // 불변
        private const val CONFIG_NAME_META = "csa.config_name" // 불변
        private const val LEFT_AMMO_AMOUNT_META = "csa.left_ammo_amount" // 가변
        private const val RIGHT_AMMO_AMOUNT_META = "csa.right_ammo_amount" // 가변
        private const val RELOAD_AMMO_AMOUNT_META = "csa.reload_ammo_amount" // 불변
        private const val AMMO_ITEM_MATERIAL_META = "csa.ammo_item_material" // 불변
        private const val RELOAD_AMOUNT_PER_AMMO_META = "csa.reload_amount_per_ammo" // 불번
        private const val POSSESSED_EXTRA_AMMO_AMOUNT_META = "csa.possessed_extra_ammo_amount" // 가변       // 진짜 총알 아이템 개수(*ReloadAmountPerAmmo 없음)
        private const val UNIQUE_ID_META = "csa.unique_id" // 불변

        private fun checkDataIsValid(p: Player) {
            val mainItem = getItemStack(p)
            if (!(UniqueIdAPI.hasUniqueID(mainItem) && UniqueIdAPI.getUniqueID(mainItem) == getUniqueId(p))) { // 메인 아이템이 예상템과 다를 때(UID이용)
                removeAllOfCrackShotMeta(p)
                throw Exception("메인 아이템이 예상한 메인아이템에서 벗어났음 - 비정상적")
            }
        }

        @JvmStatic
        fun isSet(p: Player, passCheckingData: Boolean = false): Boolean {  // 이 클래스 내부에서는 isSet()을 쓰지 않는게 좋음.
            if (p.hasMetadata(MAINITEM_SLOT_META)) {
                if (!passCheckingData) {
                    checkDataIsValid(p)
                }
                return true
            }
            return false
        }

        @JvmStatic
        fun getItemStack(p: Player): ItemStack {  // Metadata들이 올바르지 않는 경우는 PlayerItemHeldEvent에서 메소드를 호출했을 때 말곤 없음. 근데 모든 데이터가 얘를 기준으로 얻어질테니 엄청난 문제는 없을지도.
            if (!p.hasMetadata(MAINITEM_SLOT_META)) throw Exception()
            val mainItemSlot =  p.getMetadata(MAINITEM_SLOT_META)[0].asInt()
            return if (p.inventory.getItem(mainItemSlot) != null) p.inventory.getItem(mainItemSlot) else ItemStack(Material.AIR)
        }

        @JvmStatic
        fun getParentNode(p: Player): String {
            if (!isSet(p) || !p.hasMetadata(PARENT_NODE_META)) throw Exception()
            return p.getMetadata(PARENT_NODE_META)[0].asString()
        }

        @JvmStatic
        fun getConfigName(p: Player): String {
            if (!isSet(p) || !p.hasMetadata(CONFIG_NAME_META)) throw Exception()
            return p.getMetadata(CONFIG_NAME_META)[0].asString()
        }

        @JvmStatic
        fun isDualWield(p: Player): Boolean {
            if (!isSet(p)) throw Exception()
            return getRightAmmoAmount(p) != -1  // -1이 null 대신 사용되고 있기 때문
        }

        @JvmStatic
        fun getLeftAmmoAmount(p: Player): Int {
            if (!isSet(p) || !p.hasMetadata(LEFT_AMMO_AMOUNT_META)) throw Exception()
            return p.getMetadata(LEFT_AMMO_AMOUNT_META)[0].asInt()
        }

        @JvmStatic
        fun getRightAmmoAmount(p: Player): Int {
            if (!isSet(p) || !p.hasMetadata(RIGHT_AMMO_AMOUNT_META)) throw Exception()
            return p.getMetadata(RIGHT_AMMO_AMOUNT_META)[0].asInt()
        }

        @JvmStatic
        fun getReloadAmmoAmount(p: Player): Int {
            if (!isSet(p) || !p.hasMetadata(RELOAD_AMMO_AMOUNT_META)) throw Exception()
            return p.getMetadata(RELOAD_AMMO_AMOUNT_META)[0].asInt()
        }

        @JvmStatic
        fun getAmmoItemMaterial(p: Player): Material? {
            if (!isSet(p) || !p.hasMetadata(AMMO_ITEM_MATERIAL_META)) throw Exception()
            return p.getMetadata(AMMO_ITEM_MATERIAL_META)[0].value() as Material
        }

        @JvmStatic
        fun getReloadAmountPerAmmo(p: Player): Int {
            if (!isSet(p) || !p.hasMetadata(RELOAD_AMOUNT_PER_AMMO_META)) throw Exception()
            return p.getMetadata(RELOAD_AMOUNT_PER_AMMO_META)[0].asInt()
        }

        @JvmStatic
        fun getPossessedExtraAmmoAmount(p: Player): Int {
            if (!isSet(p) || !p.hasMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META)) throw Exception()
            return p.getMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META)[0].asInt()
        }

        @JvmStatic
        fun getUniqueId(p: Player): String? {
            if (!isSet(p) || !p.hasMetadata(UNIQUE_ID_META)) throw Exception()
            return p.getMetadata(UNIQUE_ID_META)[0].asString()
        }

        private fun removeAllOfCrackShotMeta(p: Player) {
            p.removeMetadata(MAINITEM_SLOT_META, plugin)
            p.removeMetadata(PARENT_NODE_META, plugin)
            p.removeMetadata(CONFIG_NAME_META, plugin)
            p.removeMetadata(LEFT_AMMO_AMOUNT_META, plugin)
            p.removeMetadata(RIGHT_AMMO_AMOUNT_META, plugin)
            p.removeMetadata(RELOAD_AMMO_AMOUNT_META, plugin)
            p.removeMetadata(AMMO_ITEM_MATERIAL_META, plugin)
            p.removeMetadata(RELOAD_AMOUNT_PER_AMMO_META, plugin)
            p.removeMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, plugin)
            p.removeMetadata(UNIQUE_ID_META, plugin)
        }
    }





    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerMainItemChanged(e: PlayerMainItemChangedConsideringUidEvent) {
        val p = e.player
        val newIs = e.newItemStack
        if (!e.isChangedToCrackShotWeapon) {
            if (isSet(p, passCheckingData = true)) { // ItemStack이 다를 수 밖에 없어서 Exception 발생함 -> ignoreException으로 처리
                removeAllOfCrackShotMeta(p)
            }
            return
        }

        val mainItemSlot = e.newSlot
        val parentNode = CrackShotAdditionAPI.getWeaponParentNode(newIs) ?: return
        val configName = CrackShotAdditionAPI.getWeaponConfigName(parentNode)
        val isDualWield = CSDirector.getInstance().isDualWield(p, parentNode, newIs)
        val leftAmmoAmt: Int
        var rightAmmoAmt = -1
        if (isDualWield) {
            val dualAmmo = CSDirector.getInstance().grabDualAmmo(newIs, parentNode)
            leftAmmoAmt = dualAmmo[0]
            rightAmmoAmt = dualAmmo[1]
        } else {
            leftAmmoAmt = CrackShotAdditionAPI.getWeaponAmmoAmount(p, parentNode, newIs)
        }
        val reloadAmmoAmt = CrackShotAdditionAPI.getWeaponReloadAmount(p, parentNode, newIs)
        var ammoMaterial: Material? = null
        var reloadAmtPerAmmo = -1
        var possessedExtraAmmo = -1
        val uniqueId = UniqueIdAPI.getUniqueID(newIs)
        val ammoEnable = CSDirector.getInstance().getBoolean("$parentNode.Ammo.Enable")
        val takeAmmo = CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_On_Reload")
        val takeAsMag = CSDirector.getInstance().getBoolean("$parentNode.Reload.Take_Ammo_As_Magazine")
        if (ammoEnable && takeAmmo) {
            ammoMaterial = Material.getMaterial(CSDirector.getInstance().getString("$parentNode.Ammo.Ammo_Item_ID").split("~").toTypedArray()[0].toInt()) // '~' 를 고려해야함
            if (ammoMaterial != null) {
                reloadAmtPerAmmo = if (takeAsMag) reloadAmmoAmt else 1
                possessedExtraAmmo = countPossessedAmmoAmount(p, ammoMaterial, reloadAmtPerAmmo)
            }
        }
        p.setMetadata(MAINITEM_SLOT_META, FixedMetadataValue(plugin, mainItemSlot))
        p.setMetadata(PARENT_NODE_META, FixedMetadataValue(plugin, parentNode))
        p.setMetadata(CONFIG_NAME_META, FixedMetadataValue(plugin, configName))
        p.setMetadata(LEFT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, leftAmmoAmt))
        p.setMetadata(RIGHT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, rightAmmoAmt))
        p.setMetadata(RELOAD_AMMO_AMOUNT_META, FixedMetadataValue(plugin, reloadAmmoAmt))
        if (ammoMaterial != null) {
            p.setMetadata(AMMO_ITEM_MATERIAL_META, FixedMetadataValue(plugin, ammoMaterial))
        } else {
            p.removeMetadata(AMMO_ITEM_MATERIAL_META, plugin)
        }
        p.setMetadata(RELOAD_AMOUNT_PER_AMMO_META, FixedMetadataValue(plugin, reloadAmtPerAmmo))
        p.setMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, FixedMetadataValue(plugin, possessedExtraAmmo))
        p.setMetadata(UNIQUE_ID_META, FixedMetadataValue(plugin, uniqueId))
    }

    /* 가변적인 META 값 관리 */ // requiredAmmoMaterial이 이벤트 대상이면 POSSESSED_EXTRA_AMMO 값을 업데이트
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemChanged(e: InventoryItemChangedEvent) {
        val requiredAmmoMaterial = getAmmoItemMaterial(e.player)
        val reloadAmtPerAmmo = getReloadAmountPerAmmo(e.player)
        if (requiredAmmoMaterial != null && (e.itemStack.type == requiredAmmoMaterial || e.itemStack.type == Material.AIR)) {
            val possessedExtraAmmoAmount = countPossessedAmmoAmount(e.player, requiredAmmoMaterial, reloadAmtPerAmmo)
            e.player.setMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, FixedMetadataValue(plugin, possessedExtraAmmoAmount))
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onShoot(e: WeaponShootEvent) {
        if (isDualWield(e.player)) {
            val dualAmmo = CSDirector.getInstance().grabDualAmmo(e.player.inventory.itemInMainHand, e.weaponTitle)
            e.player.setMetadata(LEFT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, dualAmmo[0]))
            e.player.setMetadata(RIGHT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, dualAmmo[1]))
        } else {
            e.player.setMetadata(LEFT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, getLeftAmmoAmount(e.player) - 1))
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        val reloadAmmoAmt = getReloadAmmoAmount(e.player)
        if (isDualWield(e.player)) {
            // reloadAmt/2 안해도 됨
            e.player.setMetadata(LEFT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, reloadAmmoAmt))
            e.player.setMetadata(RIGHT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, reloadAmmoAmt))
        } else {
            e.player.setMetadata(LEFT_AMMO_AMOUNT_META, FixedMetadataValue(plugin, reloadAmmoAmt))
        }
    }

    /* 램 관리 */
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) { removeAllOfCrackShotMeta(e.player) }

    private fun countPossessedAmmoAmount(p: Player, ammoMaterial: Material, multiplication: Int): Int {
        var amount = 0
        for (loopIs in p.inventory.contents) {
            if (loopIs == null) continue
            if (loopIs.type == ammoMaterial) {
                amount += loopIs.amount * multiplication
            }
        }
        return amount
    }
}