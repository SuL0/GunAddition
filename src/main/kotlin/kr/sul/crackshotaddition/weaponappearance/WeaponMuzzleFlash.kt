package kr.sul.crackshotaddition.weaponappearance

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponShootEvent
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.pow

object WeaponMuzzleFlash : Listener {
    private val MUZZLE_FLASH_PARTICLE = Particle.REDSTONE
    private const val RIGHT = 90
    private const val LEFT = -90

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        val bMuzzleflash = CSDirector.getInstance().getBoolean(e.weaponTitle + ".Addition.Muzzle_Flash.Enable")
        if (bMuzzleflash) {
            val multiplyToRightSideVec = CSDirector.getInstance().getDouble(e.weaponTitle + ".Addition.Muzzle_Flash.MultiplyToRightSideVec")
            val sumToY = CSDirector.getInstance().getDouble(e.weaponTitle + ".Addition.Muzzle_Flash.SumToY")
            val multiplyToForwardSideVec = CSDirector.getInstance().getDouble(e.weaponTitle + ".Addition.Muzzle_Flash.MultiplyToForwardSideVec")
            spawnMuzzleFlashParticle(e.player, RIGHT, multiplyToRightSideVec, sumToY, multiplyToForwardSideVec)
        }
    }

    private fun spawnMuzzleFlashParticle(p: Player, rightOrLeft: Int, multiplyToRightSideVec: Double, sumToY: Double, multiplyToForwardSideVec: Double) {
        val playerYaw = (p.location.yaw + 90.0f + rightOrLeft) * Math.PI / 180.0 // toRadian
        val toRightSideVec = Vector(Math.cos(playerYaw) * multiplyToRightSideVec, sumToY, Math.sin(playerYaw) * multiplyToRightSideVec)
        val toForwardSideVec = p.location.direction.normalize().multiply(multiplyToForwardSideVec)

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