package kr.sul.crackshotaddition.addition

import com.shampaggon.crackshot.events.WeaponPrepareShootEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.event.WeaponSwapCompleteEvent
import kr.sul.crackshotaddition.event.WeaponSwapEvent
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object WeaponSwapDelay : Listener {
    private val swapDelayOfPlayers = HashMap<UUID, Long>()

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        swapDelayOfPlayers.remove(e.player.uniqueId)
    }

    @EventHandler
    fun onPlayerHeldItemChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(e.clonedPreviousItemStack)) return  // 총->총 스왑만 딜레이 있게 하는 코드
        val p = e.player

        // 이전 템 쿨타임 적용 중 이였다면, 삭제
        if (p.getCooldown(e.clonedPreviousItemStack.type) > 1) {
            p.setCooldown(e.clonedPreviousItemStack.type, 0)
        }

        if (!WeaponInfoExtractor.isValidCrackShotWeapon(e.newItemStack)) return
        val newlyHeldWeaponInfo = WeaponInfoExtractor(p, e.newItemStack)

        // 쿨타임 설정
        val configSwapDelay = csDirector.getInt("${newlyHeldWeaponInfo.parentNode}.Addition.Weapon_Swap_Delay")
        Bukkit.getServer().pluginManager.callEvent(WeaponSwapEvent(p, e.newItemStack, configSwapDelay))
        val millisecSwapDelay = System.currentTimeMillis() + configSwapDelay * 50 // 1tick = 1ms * 50
        swapDelayOfPlayers[p.uniqueId] = millisecSwapDelay
        p.setCooldown(e.newItemStack.type, configSwapDelay)

        // 쿨타임 지난 후
        Bukkit.getScheduler().runTaskLater(plugin, {
            if (CrackShotAdditionAPI.isValidCrackShotWeapon(p.inventory.itemInMainHand) &&
                    swapDelayOfPlayers.containsKey(p.uniqueId) && swapDelayOfPlayers[p.uniqueId] == millisecSwapDelay) { // 전에 넣은 딜레이 값이랑 똑같은지 확인
                Bukkit.getServer().pluginManager.callEvent(WeaponSwapCompleteEvent(p, e.newItemStack))
            }
        }, configSwapDelay.toLong())
    }

    
    //    크랙샷 무기 모든 행위 캔슬시키기  //
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPrepareShoot(e: WeaponPrepareShootEvent) {
        val p = e.player
        if (swapDelayOfPlayers.containsKey(p.uniqueId)) {
            if (swapDelayOfPlayers[p.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(p.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onReload(e: WeaponReloadEvent) {
        val p = e.player
        if (swapDelayOfPlayers.containsKey(p.uniqueId)) {
            if (swapDelayOfPlayers[p.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(p.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onScope(e: WeaponScopeEvent) {
        val p = e.player
        if (swapDelayOfPlayers.containsKey(p.uniqueId)) {
            if (swapDelayOfPlayers[p.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(p.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }
}