package kr.sul.crackshotaddition.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class WeaponSwapCompleteEvent(val player: Player, val weaponItem: ItemStack) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}