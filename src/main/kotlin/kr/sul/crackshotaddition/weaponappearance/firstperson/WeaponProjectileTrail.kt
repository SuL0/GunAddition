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
    const val DISTORTION_DISTANCE_1 = 40
    const val DISTORTION_DISTANCE_2 = 80
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

    private fun projectileTrail(shooter: Entity, proj: Entity, particle: Particle?) {
        hideEntity(proj)
        object : BukkitRunnable() {
            val playerYaw = (shooter.location.yaw + 90.0f + 90) * Math.PI / 180.0
            val toRightSideVec = Vector(cos(playerYaw) * 0.3, -0.2, sin(playerYaw) * 0.3)
            // 삼각형 세 개를 그린다고 생각
            val toRightSideVec_Sections = arrayListOf<Vector>().run {
                add(toRightSideVec.clone().multiply(2/3.0))  // 0: 왜곡 가장 앞 부분의 최대 벡터(여기에 * n(<=1) 을 하기 때문)   *  Sec2 + Sec1 + Sec0
                add(toRightSideVec.clone().multiply(0.8/3.0))  // 1: 왜곡 가장 뒷 부분의 최대 벡터    * Sec2 + Sec1
                add(toRightSideVec.clone().multiply(0.2/3.0))  // 2: 왜곡 끝나고 완전 직선이면, 궤도가 전혀 안보여서 어색해서 약간 오른쪽으로 왜곡시켜줌
                this
            }

            val firstProjLoc = proj.location.clone()
            var previousProjLoc: Location? = null

            override fun run() {
                if (!proj.isValid) {
                    cancel(); return
                }

                var projLoc = proj.location.clone()   // clone안하고, loc에 .add(Location)을 하게되면 projectile에 직접적으로 수정이 가해지게 되는 문제 (add(Location) 은 아래의 총알 왜곡에서 사용됨)
                if (previousProjLoc != null) {  // 첫 번째 총알은 건너뛰기
                    // 청크에 projectile이 막혔을 시 projectile 삭제
                    if (projLoc.distance(previousProjLoc) <= 0.1) {
                        proj.remove()
                        cancel(); return
                    }

                    val nearbyPlayers = if (shooter is Player) arrayListOf(shooter) else arrayListOf()
                    nearbyPlayers.addAll(Bukkit.getServer().onlinePlayers
                            .filter { it != shooter && it.world == shooter.world && it.location.distance(shooter.location) <= 100 })


                    // 총알 위치 왜곡
                    projLoc = applyDistortion(proj, firstProjLoc, toRightSideVec_Sections)
                    val projVector = projLoc.toVector().subtract(previousProjLoc!!.toVector())  // 왜곡된 벡터 (!= projectile.velocity)


                    // 총알 파티클 //
                    var locListToSpawnParticle = arrayListOf(projLoc)
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
                    // 첫번째 Particle Line이라면, 파티클 중 플레이어 화면과 너무 가까운 것들은 제거
                    if (firstProjLoc.distance(previousProjLoc) <= 2) {
                        locListToSpawnParticle = locListToSpawnParticle.filter { it.distance(firstProjLoc) > 2 } as ArrayList<Location>
                    }
                    // 파티클 소환
                    locListToSpawnParticle.forEach {
                        it.world.spawnParticle(Particle.DRIP_LAVA, nearbyPlayers, if (shooter is Player) shooter else null,
                                it.x, it.y, it.z, 1, 0.0, 0.0, 0.0, 0.0, null, true) // extra가 속도
                    }
                }
                previousProjLoc = applyDistortion(proj, firstProjLoc, toRightSideVec_Sections)  // 왜곡된 이전 위치
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun hideEntity(entity: Entity) {
        val packetPlayOutEntityDestroy = PacketPlayOutEntityDestroy(entity.entityId)
        for (player in entity.world.players) {
            (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutEntityDestroy)
        }
    }

    // 총알 위치 왜곡 (삼각형 세 개를 그린다고 생각)
    private fun applyDistortion(proj: Entity, firstProjLoc: Location, toRightSideVec_Sections: List<Vector>): Location {
        return when (val disFromFirst = firstProjLoc.distance(proj.location).toInt()) {
            in 0..DISTORTION_DISTANCE_1 -> {
                val vec = toRightSideVec_Sections[0].clone().multiply((DISTORTION_DISTANCE_1-disFromFirst)/ DISTORTION_DISTANCE_1) // dis가 올라갈수록 0에 가까워짐
                proj.location.clone().add(toRightSideVec_Sections[2]).add(toRightSideVec_Sections[1]).add(vec)
            }
            in DISTORTION_DISTANCE_1..DISTORTION_DISTANCE_2 -> {
                val vec = toRightSideVec_Sections[1].clone().multiply((DISTORTION_DISTANCE_2-(disFromFirst-DISTORTION_DISTANCE_1))/ DISTORTION_DISTANCE_2)
                proj.location.clone().add(toRightSideVec_Sections[2]).add(vec)
            }
            else -> {
                proj.location.clone().add(toRightSideVec_Sections[2])
            }
        }
    }
}