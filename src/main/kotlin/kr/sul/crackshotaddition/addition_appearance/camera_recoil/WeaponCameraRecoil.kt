package kr.sul.crackshotaddition.addition_appearance.camera_recoil

import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object WeaponCameraRecoil : Listener {
    private val set = HashSet(hashSetOf(*PacketPlayOutPosition.EnumPlayerTeleportFlags.values()))
    private val recoilTaskMap = hashMapOf<Player, Thread>()
    private const val NUM_OF_RECOIL_NESTING = "CSA.numOfRecoilNesting"
    private const val HORIZONAL_RECOIL = "CSA.horizonalRecoil"

    @EventHandler
    fun onWeaponShoot(e: WeaponShootEvent) {
        val bCamerarecoil = csDirector.getBoolean("${e.parentNode}.Addition.Camera_Recoil.Enable")
        if (bCamerarecoil) {
            val p = e.player
            val yawValue = csDirector.getDouble("${e.parentNode}.Addition.Camera_Recoil.Yaw")
            val pitchValue = csDirector.getDouble("${e.parentNode}.Addition.Camera_Recoil.Pitch")
            if (yawValue != 0.0 || pitchValue != 0.0) {  // 0, 0 ? 이게 맞는 값인가?  에임이 제일 위쪽에 갔을 때 pitch가 -90이지 않나
                val numOfRecoilNesting = if (p.hasMetadata(NUM_OF_RECOIL_NESTING)) p.getMetadata(NUM_OF_RECOIL_NESTING)[0].asInt() else 0
                p.setMetadata(NUM_OF_RECOIL_NESTING, FixedMetadataValue(plugin, numOfRecoilNesting+1))

                if (numOfRecoilNesting == 1) {
                    Thread() {
                        Thread.sleep(1)
                        cameraRecoil(p, 0F, 0.01F)  // 화면 튕기는거 방지 목적
                    }
                    p.setMetadata(HORIZONAL_RECOIL, FixedMetadataValue(plugin, (Math.random() < 0.5)))
                }
                else if (Math.random() < 0.2) {
                    p.setMetadata(HORIZONAL_RECOIL, FixedMetadataValue(plugin, !p.getMetadata(HORIZONAL_RECOIL)[0].asBoolean()))
                }

                if (p.getMetadata(HORIZONAL_RECOIL)[0].asBoolean()) {
                    cameraRecoil(p, 0.5.toFloat(), pitchValue.toFloat())
                } else {
                    cameraRecoil(p, (-0.5).toFloat(), pitchValue.toFloat())
                }
            }

            // 이전 task는 삭제
            if (recoilTaskMap.containsKey(p)) {
                recoilTaskMap[p]!!.stop()
                recoilTaskMap.remove(p)
            }
            recoilTaskMap[p] = recoilRecovery(p)
        }
    }

    private fun cameraRecoil(player: Player, yaw: Float, pitch: Float) {
        val packetPlayOutPosition = PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, set, 0)
        (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutPosition)
    }

    private fun recoilRecovery(p: Player): Thread {
        val thread = Thread() {
            Thread.sleep(100)
            val multiple = 1 + p.getMetadata(NUM_OF_RECOIL_NESTING)[0].asInt()/5
            for (i in 1..6) {
                runAndSleep({ cameraRecoil(p, 0f, 0.1f*multiple) }, 15)
            }
            //
            for (i in 1..4) {
                runAndSleep({ cameraRecoil(p, 0f, 0.2f*multiple) }, 15)
            }
            p.setMetadata(NUM_OF_RECOIL_NESTING, FixedMetadataValue(plugin, 0))
            //
            for (i in 1..4) {
                runAndSleep({ cameraRecoil(p, 0f, 0.3f*multiple) }, 15)
            }
        }
        thread.start()
        return thread
    }
    private fun runAndSleep(runnable: Runnable, sleep: Long) {
        runnable.run()
        Thread.sleep(sleep)
    }
}