package me.sul.crackshotaddition.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class WeaponSwapEvent(val player: Player, val weaponItem: ItemStack, val weaponTitle: String, val swapDelay: Int) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}