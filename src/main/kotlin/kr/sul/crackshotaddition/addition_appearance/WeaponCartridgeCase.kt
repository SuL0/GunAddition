package kr.sul.crackshotaddition.addition_appearance

import com.shampaggon.crackshot.events.WeaponPreShootEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object WeaponCartridgeCase : Listener {
    private const val RIGHT = 90
    private const val LEFT = -90

    @EventHandler(priority = EventPriority.MONITOR)
    fun onGunFire(e: WeaponPreShootEvent) {
        val p = e.player
        val playerYaw = (p.location.yaw + 90.0f + RIGHT) * Math.PI / 180.0 // toRadian
        val toRightSideVec = Vector(cos(playerYaw) * 0.3, -0.55, sin(playerYaw) * 0.3)
        val toForwardSideVec = p.location.direction.normalize().multiply(0.8)

        // 벡터 보정
        val value: Double
        if (toForwardSideVec.y > 0) {
            value = 0.185 * (toForwardSideVec.y / toForwardSideVec.length()).pow(3.0)
            toForwardSideVec.add(toForwardSideVec.clone().multiply(Vector(0, -1, 0)).normalize().multiply(value))
        } else {
            value = 0.14 * (abs(toForwardSideVec.y) / toForwardSideVec.length()).pow(2.0)
            toForwardSideVec.add(toForwardSideVec.clone().multiply(Vector(-1, 0, -1)).normalize().multiply(value))
        }

        val loc = p.eyeLocation.toVector().add(toRightSideVec).add(toForwardSideVec).toLocation(p.world)

        val entity = loc.world.dropItem(loc, ItemStack(Material.RED_ROSE, 1, 4))
        entity.velocity = Vector(cos(playerYaw) * 0.25, 0.0, sin(playerYaw) * 0.25).multiply(1.3).add(p.location.direction.normalize().multiply(0.15))
        entity.velocity = entity.velocity.multiply(3.0)
        Bukkit.getScheduler().runTaskLater(plugin, {
            entity.remove()
        }, 1L)
    }

    @EventHandler
    fun onPickUp(e: EntityPickupItemEvent) {
        if (e.item.itemStack.type == Material.RED_ROSE) {
            e.isCancelled = true
            e.item.remove()
        }
    }
}