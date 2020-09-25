package kr.sul.crackshotaddition.events

import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WeaponProjectileTrailEvent(val entity: Entity, val weaponTitle: String, var particle: Particle) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}