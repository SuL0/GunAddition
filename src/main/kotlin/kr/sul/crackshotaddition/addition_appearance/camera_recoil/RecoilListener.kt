package kr.sul.crackshotaddition.addition_appearance.camera_recoil

import com.shampaggon.crackshot.events.WeaponPreShootEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object RecoilListener: Listener {
    private val recoilPlayerMap = hashMapOf<Player, RecoilPlayer>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onGunFire(e: WeaponPreShootEvent) {  // 샷건의 경우, WeaponShootEvent를 사용하면, 산탄 발수에 맞춰서 이벤트가 실행되는 문제가 있어 PreShoot를 사용함
        val p = e.player
        if (!recoilPlayerMap.contains(p)) {
            recoilPlayerMap[p] = RecoilPlayer(p)
        }
        recoilPlayerMap[p]!!.addRecoil(e.parentNode)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        recoilPlayerMap.remove(e.player)
    }
}