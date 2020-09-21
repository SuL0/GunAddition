package me.sul.crackshotaddition.weaponappearance_etc

import com.shampaggon.crackshot.events.WeaponShootEvent
import me.sul.crackshotaddition.CrackShotAddition
import me.sul.crackshotaddition.DebuggingCommand
import me.sul.crackshotaddition.events.WeaponProjectileTrailEvent
import me.sul.customentity.entityweapon.event.CEWeaponShootEvent
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import java.util.stream.Collectors
import kotlin.math.cos
import kotlin.math.sin

class WeaponProjectileTrail : Listener {
    companion object {
        const val DISTORTION_DISTANCE = 60
        const val SHIFTVECTOR_LENGTH = 0.2f
        val DEFAULT_PARTICLE = Particle.SWEEP_ATTACK // SUSPENDED, WATER_BUBBLE 리팩입히면 괜찮을 듯
    }

    @EventHandler
    fun onCEWeaponShootEvent(e: CEWeaponShootEvent) {
        projectileTrail(e.entity, e.projectile, DEFAULT_PARTICLE)
    }

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        var particle = DEFAULT_PARTICLE

        // by 총 파티클
//		String configProjectileTrailEffect = CSDirector.getInstance().getString(weaponTitle + ".Addition.Projectile_Trail");
//		Particle particle = (configProjectileTrailEffect == null) ? DEFAULT_PARTICLE : Particle.valueOf(configProjectileTrailEffect);

        // by 플레이어 파티클
        val weaponProjectileTrailEvent = WeaponProjectileTrailEvent(e.player, e.weaponTitle, particle)
        CrackShotAddition.instance.server.pluginManager.callEvent(weaponProjectileTrailEvent)
        particle = weaponProjectileTrailEvent.particle
        projectileTrail(e.player, e.projectile, particle)
    }

    private fun projectileTrail(shooter: Entity, projectile: Entity, particle: Particle?) {
        hideEntity(projectile)
        object : BukkitRunnable() {
            val playerYaw = (shooter.location.yaw + 90.0f + 90) * Math.PI / 180.0
            val toRightSideVec: Vector = Vector(cos(playerYaw) * 0.3, -0.2, sin(playerYaw) * 0.3)
            var previousLoc: Location? = null
            var cnt = DISTORTION_DISTANCE
            var skipFirstLoc = true

            override fun run() {
                if (!projectile.isValid) {
                    cancel(); return
                }
                var loc = projectile.location
                if (!skipFirstLoc) {
                    val nearbyPlayers: MutableList<Player> = ArrayList()
                    if (shooter is Player) nearbyPlayers.add(shooter)
                    nearbyPlayers.addAll(Bukkit.getServer().onlinePlayers.stream()
                            .filter { loopP: Player -> loopP != shooter && loopP.world == shooter.world && loopP.location.distance(shooter.location) <= 100 }
                            .collect(Collectors.toList()))
                    if (DebuggingCommand.distortion) {
                        loc = loc.clone().add(toRightSideVec.multiply(Math.max(cnt--, 0) / DISTORTION_DISTANCE)) // 총알 궤적 위치 왜곡   // loc에 바로 더하면 projectile에 더해짐
                    }
                    val particleLoc = loc.clone()
                    val shiftVector = projectile.velocity.clone().multiply(-1).multiply(SHIFTVECTOR_LENGTH)
                    var i = 0
                    while (i < 1 / SHIFTVECTOR_LENGTH) {
                        loc.world.spawnParticle(particle, nearbyPlayers, if (shooter is Player) shooter else null, particleLoc.x, particleLoc.y, particleLoc.z, 1, 0.0, 0.0, 0.0, 0.0, null, true) // extra가 속도
                        particleLoc.add(shiftVector)
                        i++
                    }

                    // 청크에 projectile이 막혔을 시 projectile 삭제
                    if (loc.distance(previousLoc) <= 0.1) {
                        projectile.remove()
                        cancel()
                        return
                    }
                } else {
                    skipFirstLoc = false
                }
                previousLoc = loc
            }
        }.runTaskTimer(CrackShotAddition.instance, 0L, 1L)
    }

    private fun hideEntity(entity: Entity) {
        val packetPlayOutEntityDestroy = PacketPlayOutEntityDestroy(entity.entityId)
        for (player in entity.world.players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutEntityDestroy)
        }
    }
}