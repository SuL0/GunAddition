package kr.sul.crackshotaddition.weaponappearance.item.`object`

import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csMinion
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

object LeftHandManager: Listener {
    // ITEM //
    private const val NBT_KEY = "isHandForCrackShot"
    fun isOffHandAHandItem(p: Player): Boolean {
        return isHandItem(p.inventory.itemInOffHand)
    }
    fun isHandItem(item: ItemStack): Boolean {
        if (item.type == Material.AIR) return false
        val nbti = NBTItem(item); return nbti.hasKey(NBT_KEY)
    }
    fun makeLeftHandItem(node: String, normalDurability: Short): ItemStack {
        val item = csMinion.parseItemStack(csDirector.getString("$node.Item_Type"))
        item.itemMeta.displayName = "§7왼손"
        item.durability = normalDurability
        val nbti = NBTItem(item); nbti.setBoolean(NBT_KEY, true);
        item.itemMeta = nbti.item.itemMeta
        return item
    }


    // LISTENER //

    // 왼손 슬롯 벤 //
    @EventHandler(priority = EventPriority.LOW)
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory.type == InventoryType.PLAYER && e.slot == 40) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (isHandItem(e.player.inventory.itemInOffHand)) {
            e.player.inventory.itemInOffHand = null
        }
    }
}