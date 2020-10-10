package kr.sul.crackshotaddition.events

import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

// Scav때문에 player이 아닌 entity로 해 놓은 듯
class WeaponProjectileTrailEvent(val entity: Entity, val parentNode: String, var particle: Particle) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}