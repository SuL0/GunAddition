package kr.sul.crackshotaddition.addition

import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.crackshotaddition.CrackShotAddition
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object SpeedOnScope: Listener {
    private const val DEFAULT_WALK_SPEED = 0.2.toFloat()

    @EventHandler
    fun onScope(e: WeaponScopeEvent) {
        val multiplyWalkSpeed = CrackShotAddition.csDirector.getInt("${e.parentNode}.Addition.Multiply_WalkSpeed_By")
        if (multiplyWalkSpeed != 0) {  // 0이 기본값이기 때문
            // 줌 인
            if (e.isZoomIn) {
                e.player.walkSpeed = DEFAULT_WALK_SPEED*multiplyWalkSpeed
            }
            // 줌 아웃
            else {
                e.player.walkSpeed = DEFAULT_WALK_SPEED
            }
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.player.walkSpeed = DEFAULT_WALK_SPEED
    }
}