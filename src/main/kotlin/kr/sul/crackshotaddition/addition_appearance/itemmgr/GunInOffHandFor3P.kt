package kr.sul.crackshotaddition.addition_appearance.itemmgr

import kr.sul.crackshotaddition.CrackShotAddition
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import kr.sul.servercore.nbtapi.NbtItem
import kr.sul.servercore.util.ItemBuilder.nameIB
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

// LeftHand 아이템을 뺄 수 있는 버그가 있었음. 나중에 쓰게되면 확인 후 고치기
// TODO: GunItem_Modeling_Addition에서 Off_Hand_Item을 선택사항으로 변경
object GunInOffHandFor3P : Listener {
    private const val gunModelingNode = "Item_Information.GunItem_Modeling_Addition"
    private fun isConfigEnabled(parentNode: String): Boolean { return CrackShotAddition.csDirector.getBoolean("$parentNode.$gunModelingNode.Enable") }

    // 3인칭 모델링 아이템 //
    private const val NBT_KEY = "GunInOffHandFor3P"
    private fun Player.hasGunModeling(): Boolean {
        return this.inventory.itemInOffHand.isGunModelingItem()
    }
    private fun ItemStack.isGunModelingItem(): Boolean {
        if (this.type == Material.AIR) return false
        val nbti = NbtItem(this)
        return nbti.tag.hasKey(NBT_KEY)
    }
    private fun makeGunModelingForOffHand(parentNode: String): ItemStack {
        val item = CrackShotAddition.csMinion.parseItemStack(CrackShotAddition.csDirector.getString("$parentNode.$gunModelingNode.Off_Hand_Item"))
        item.nameIB("§f")
        val nbti = NbtItem(item)
        nbti.tag.setBoolean(NBT_KEY, true)
        nbti.applyToOriginal()
        return item
    }


    // 스왑 //
    @EventHandler
    fun onHeldItemIsChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        // 아이템 들 때 총 모델링 생성(Normal State)
        if (e.isChangedToCrackShotWeapon()) {
            val parentNode = WeaponInfoExtractor(e.player, e.newItemStack).parentNode
            if (!isConfigEnabled(parentNode)) return
            e.player.inventory.setItemInOffHand(makeGunModelingForOffHand(parentNode))  // TODO: 다른 아이템이 있을 때는? - 낙하산
        }
        // 일반 아이템으로 스왑할 때 총 모델링 제거 //
        else {
            if (e.player.hasGunModeling()) {
                e.player.inventory.setItemInOffHand(null)
            }
        }
    }


    // 왼손 슬롯 벤 //
    @EventHandler(priority = EventPriority.LOW)
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory != null && e.clickedInventory.type == InventoryType.PLAYER
                && e.slot == 40) {
            e.isCancelled = true
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerSwapHandItem(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true
    }
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (e.player.inventory.itemInOffHand.isGunModelingItem()) {
            e.player.inventory.itemInOffHand = null
        }
    }
}