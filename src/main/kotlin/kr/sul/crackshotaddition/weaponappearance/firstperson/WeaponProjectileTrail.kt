package kr.sul.crackshotaddition.weaponappearance.firstperson

import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.events.WeaponProjectileTrailEvent
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
import kotlin.math.cos
import kotlin.math.sin

object WeaponProjectileTrail : Listener {
    const val DISTORTION_PERIOD1 = 60
    const val DISTORTION_PERIOD2 = 60
    const val FILL_PARTICLE_GAP_PER_LENGTH = 1
    private val DEFAULT_PARTICLE = Particle.SWEEP_ATTACK // SUSPENDED, WATER_BUBBLE 리팩입히면 괜찮을 듯

    @EventHandler
    fun onCEWeaponShootEvent(e: CEWeaponShootEvent) {
        projectileTrail(e.entity, e.projectile, DEFAULT_PARTICLE)
    }

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        var particle = DEFAULT_PARTICLE

        // by 총 파티클
//		String configProjectileTrailEffect = csDirector.getString(parentNode + ".Addition.Projectile_Trail");
//		Particle particle = (configProjectileTrailEffect == null) ? DEFAULT_PARTICLE : Particle.valueOf(configProjectileTrailEffect);

        // by 플레이어 파티클
        val weaponProjectileTrailEvent = WeaponProjectileTrailEvent(e.player, e.parentNode, particle)
        Bukkit.getServer().pluginManager.callEvent(weaponProjectileTrailEvent)
        particle = weaponProjectileTrailEvent.particle
        projectileTrail(e.player, e.projectile, particle)
    }

    private fun projectileTrail(shooter: Entity, projectile: Entity, particle: Particle?) {
        hideEntity(projectile)
        object : BukkitRunnable() {
            val playerYaw = (shooter.location.yaw + 90.0f + 90) * Math.PI / 180.0
            val toRightSideVec: Vector = Vector(cos(playerYaw) * 0.3, -0.2, sin(playerYaw) * 0.3)
            var previousProjLoc: Location? = null
            var cnt = DISTORTION_PERIOD1

            override fun run() {
                if (!projectile.isValid) {
                    cancel(); return
                }

                var projLoc = projectile.location.clone()   // clone안하고, loc에 .add(Location)을 하게되면 projectile에 직접적으로 수정이 가해지게 되는 문제 (add(Location) 은 아래의 총알 왜곡에서 사용됨)
                if (previousProjLoc != null) {  // 첫 번째 총알은 건너뛰기
                    // 청크에 projectile이 막혔을 시 projectile 삭제
                    if (projLoc.distance(previousProjLoc) <= 0.1) {
                        projectile.remove()
                        cancel(); return
                    }

                    val nearbyPlayers = if (shooter is Player) arrayListOf(shooter) else arrayListOf()
                    nearbyPlayers.addAll(Bukkit.getServer().onlinePlayers
                            .filter { it != shooter && it.world == shooter.world && it.location.distance(shooter.location) <= 100 })

                    // 총알 왜곡
                    projLoc = projLoc.add(toRightSideVec.multiply(Math.max(cnt--, 0) / DISTORTION_PERIOD1)) // 총알 궤적 위치 왜곡
                    val projVector = previousProjLoc!!.toVector().subtract(projLoc.toVector())  // 왜곡된 벡터 (!= projectile.velocity)


                    // 총알 파티클 //
                    val locListToSpawnParticle = arrayListOf(projLoc)
                    // 1틱 사이의 공간에 촘촘히 파티클 생성
                    if (projVector.length() > FILL_PARTICLE_GAP_PER_LENGTH*2) {
                        val clonedLocForCalc = projLoc.clone()

                        val division = projVector.length().toInt() / FILL_PARTICLE_GAP_PER_LENGTH
                        val shiftVector = projVector.clone().multiply(-1).multiply(1.0/division)
                        for (i in 0 until (division-1)) {
                            clonedLocForCalc.add(shiftVector)
                            locListToSpawnParticle.add(clonedLocForCalc.clone())
                        }
                    }
                    locListToSpawnParticle.forEach {
                        it.world.spawnParticle(Particle.DRIP_LAVA, nearbyPlayers, if (shooter is Player) shooter else null,
                                it.x, it.y, it.z, 1, 0.0, 0.0, 0.0, 0.0, null, true) // extra가 속도
                    }
                }
                previousProjLoc = projLoc
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun hideEntity(entity: Entity) {
        val packetPlayOutEntityDestroy = PacketPlayOutEntityDestroy(entity.entityId)
        for (player in entity.world.players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutEntityDestroy)
        }
    }
}