package kr.sul.crackshotaddition.event

import kr.sul.crackshotaddition.infomanager.ammo.Ammo
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerInvAmmoAmtChangedEvent(val player: Player, val updatedAmmo: Ammo, val ammoAmount: Int) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}