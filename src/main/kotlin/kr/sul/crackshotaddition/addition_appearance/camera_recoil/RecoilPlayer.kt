package kr.sul.crackshotaddition.addition_appearance.camera_recoil

import net.minecraft.server.v1_12_R1.PacketPlayOutPosition
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class RecoilPlayer(private val p: Player) {
    private var pitchDistanceBetweenInitial = 0F  // 텀을 두지 않은 연속된 반동 시에, 마지막 반동 후 에임이 되돌아갈 때 쓰임
    private var leftOrRight = (random.nextBoolean())


    fun addRecoil(parentNode: String) {
//        val yaw = CrackShotAddition.csDirector.getDouble("$parentNode.Addition.Camera_Recoil.Yaw").toFloat()  // 가로
//        val pitch = CrackShotAddition.csDirector.getDouble("$parentNode.Addition.Camera_Recoil.Pitch").toFloat()  // 세로. Pitch에 있는 P의 작대기를 이용해서 암기 ㄱ
        val yaw = 0F
        val pitch = -1.5F

        if (yaw != 0F || pitch != 0F) {
            if (random.nextFloat() <= 0.2) {  // 반동이 향하는 방향 바꾸기
                leftOrRight = !leftOrRight  // 랜덤 방향 Yaw에 곱해 말어
            }

            // 마지막 반동을 받은 시점에서부터 어느정도의 텀을 가지고, 반동을 받았을 때
            if (pitchDistanceBetweenInitial == 0F) {
                pitchDistanceBetweenInitial = pitch
                cameraRecoil(p, yaw, 0.01F)  // 화면 튕기기 방지용
            } else {
                pitchDistanceBetweenInitial += pitch
            }


            if (parentNode == "WWW") {
                p.sendMessage("원래 의도")
                var leftCnt = 4
                Timer().schedule(object: TimerTask() {   // TODO: 샷건, 스나같은 단발총으로 일단 테스트
                    override fun run() {
                        if (leftCnt <= 0) {
                            cancel()  // 스케줄러 취소
                            recoveryRecoil(parentNode)  // 반동 회복으로
                        }

                        val pitchToApply = when (leftCnt) {
                            4 -> pitch*0.4F
                            3 -> pitch*0.35F
                            2 -> pitch*0.15F
                            1 -> pitch*0.1F
                            else -> throw Exception("$leftCnt")
                        }
                        cameraRecoil(p, yaw, pitchToApply)  // TODO: 원활한 테스트를 위해, yaw는 일단 배제

                        leftCnt--
                    }
                }, 1L, PERIOD)
            }


            else {
                p.sendMessage("기존 컨펌")
                var leftCnt = 4
                Timer().schedule(object: TimerTask() {   // TODO: 샷건, 스나같은 단발총으로 일단 테스트
                    override fun run() {
                        if (leftCnt <= 0) {
                            cancel()  // 스케줄러 취소
                            recoveryRecoil(parentNode)  // 반동 회복으로
                        }

                        val pitchToApply = when (leftCnt) {
                            1 -> pitch*0.4F
                            2 -> pitch*0.35F
                            3 -> pitch*0.15F
                            4 -> pitch*0.1F
                            else -> throw Exception("$leftCnt")
                        }
                        cameraRecoil(p, yaw, pitchToApply)  // TODO: 원활한 테스트를 위해, yaw는 일단 배제

                        leftCnt--
                    }
                }, 1L, PERIOD)
            }
        }
    }

    private fun recoveryRecoil(parentNode: String) {
        if (pitchDistanceBetweenInitial != 0F) {
            val pitch = pitchDistanceBetweenInitial * -1F
            pitchDistanceBetweenInitial = 0F

            if (parentNode == "WWW") {
                var leftCnt = 10
                Timer().schedule(object: TimerTask() {
                    override fun run() {
                        val pitchToApply = run {
                            if (leftCnt > 4) {
                                pitch*0.04F
                            } else {
                                pitch*0.02F
                            }
                        }
                        if (--leftCnt > 0) {
                            cameraRecoil(p, 0F, pitchToApply)  // TODO: 원활한 테스트를 위해, yaw는 일단 배제
                        } else {
                            cancel()  // 스케줄러 취소
                        }
                    }
                }, PERIOD*3, PERIOD*2)
            }

            else {
                var leftCnt = 10
                Timer().schedule(object: TimerTask() {
                    override fun run() {
                        val pitchToApply = run {
                            if (leftCnt > 4) {
                                pitch*0.04F
                            } else {
                                pitch*0.02F
                            }
                        }
                        if (--leftCnt > 0) {
                            cameraRecoil(p, 0F, pitchToApply)  // TODO: 원활한 테스트를 위해, yaw는 일단 배제
                        } else {
                            cancel()  // 스케줄러 취소
                        }
                    }
                }, PERIOD*5, PERIOD*2)
            }
        }
    }



    companion object {
        private val random = Random()
        private const val PERIOD = 20L
        private val set = HashSet(hashSetOf(*PacketPlayOutPosition.EnumPlayerTeleportFlags.values()))
        private fun cameraRecoil(player: Player, yaw: Float, pitch: Float) {
            val packetPlayOutPosition = PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, set, 0)
            (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutPosition)
        }
    }
}