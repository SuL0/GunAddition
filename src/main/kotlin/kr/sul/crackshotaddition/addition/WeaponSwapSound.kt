package kr.sul.crackshotaddition.addition

import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object WeaponSwapSound : Listener {
    private var previousSwapSound = HashMap<UUID, String>()
    @EventHandler
    fun onPlayerHeldItemChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        val p = e.player
        if (e.newItemStack.type != Material.AIR) {
            val sound = getSwapSound(e.newItemStack)
            if (previousSwapSound.containsKey(p.uniqueId)) {
                p.stopSound(previousSwapSound[p.uniqueId])
            }
            p.playSound(p.location, sound, 1f, 1f)
            previousSwapSound[p.uniqueId] = sound
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        previousSwapSound.remove(e.player.uniqueId)
    }

    fun getSwapSound(item: ItemStack?): String {
//		if (CrackShotAdditionAPI.checkIfItemIsGun(item)) {
//			if (item.getItemMeta().hasLore()) {
//				for (String lore: item.getItemMeta().getLore()) {
//					if (lore.contains("����")) {
//						return "ardraw";
//					} else if (lore.contains("����")) {
//						return "sgpump";
//					} else if (lore.contains("�����")) {
//	//					return "sgshoot";
//					} else if (lore.contains("����")) {
//	//					return "sgshoot";
//					} else if (lore.contains("������")) {
//						return "srdraw";
//					} else if (lore.contains("�������")) {
//						return "smgdraw";
//					}
//				}
//			}
//		}
        return "itemswap"
    }
}