package kr.sul.crackshotaddition.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class WeaponSwapEvent(val player: Player, val newItem: ItemStack, val parentNode: String, val swapDelay: Int) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}