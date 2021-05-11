package kr.sul.crackshotaddition.addition_appearance

import com.shampaggon.crackshot.events.WeaponPreShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object WeaponMuzzleFlash : Listener {
    private val MUZZLE_FLASH_PARTICLE = Particle.REDSTONE
    private const val RIGHT = 90
    private const val LEFT = -90

    @EventHandler(priority = EventPriority.MONITOR)
    fun onGunFire(e: WeaponPreShootEvent) {
        val bMuzzleflash = csDirector.getBoolean(e.parentNode + ".Addition.Muzzle_Flash.Enable")
        if (bMuzzleflash) {
            val multiplyToRightSideUnitVec = csDirector.getDouble(e.parentNode + ".Addition.Muzzle_Flash.MultiplyToRightSideUnitVec")
            val sumToY = csDirector.getDouble(e.parentNode + ".Addition.Muzzle_Flash.SumToY")
            val multiplyToForwardSideUnitVec = csDirector.getDouble(e.parentNode + ".Addition.Muzzle_Flash.MultiplyToForwardSideUnitVec")
            spawnMuzzleFlashParticle(e.player, RIGHT, multiplyToRightSideUnitVec, sumToY, multiplyToForwardSideUnitVec)
        }
    }

    private fun spawnMuzzleFlashParticle(p: Player, rightOrLeft: Int, multiplyToRightSideUnitVec: Double, sumToY: Double, multiplyToForwardSideUnitVec: Double) {
        val playerYaw = (p.location.yaw + 90.0f + rightOrLeft) * Math.PI / 180.0 // toRadian
        val toRightSideVec = Vector(cos(playerYaw) * multiplyToRightSideUnitVec, sumToY, sin(playerYaw) * multiplyToRightSideUnitVec)
        val toForwardSideVec = p.location.direction.normalize().multiply(multiplyToForwardSideUnitVec)

        // 벡터 보정
        val value: Double
        if (toForwardSideVec.y > 0) {
            value = 0.185 * (toForwardSideVec.y / toForwardSideVec.length()).pow(3.0)
            toForwardSideVec.add(toForwardSideVec.clone().multiply(Vector(0, -1, 0)).normalize().multiply(value))
        } else {
            value = 0.14 * (abs(toForwardSideVec.y) / toForwardSideVec.length()).pow(2.0)
            toForwardSideVec.add(toForwardSideVec.clone().multiply(Vector(-1, 0, -1)).normalize().multiply(value))
        }

        // 최종 위치
        val loc = p.eyeLocation.toVector().add(toRightSideVec).add(toForwardSideVec).toLocation(p.world)
        loc.world.spawnParticle<Any?>(MUZZLE_FLASH_PARTICLE, listOf(p), p, loc.x, loc.y, loc.z, 1, 0.0, 0.0, 0.0, 0.0, null)
    }
}