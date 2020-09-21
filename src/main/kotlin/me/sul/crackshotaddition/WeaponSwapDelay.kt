package me.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponScopeEvent
import me.sul.crackshotaddition.events.WeaponSwapCompleteEvent
import me.sul.crackshotaddition.events.WeaponSwapEvent
import me.sul.crackshotaddition.util.CrackShotAdditionAPI
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class WeaponSwapDelay : Listener {
    private val swapDelayOfPlayers = HashMap<UUID, Long>()

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        swapDelayOfPlayers.remove(e.player.uniqueId)
    }

    @EventHandler
    fun onMainItemChange(e: PlayerMainItemChangedConsideringUidEvent) {
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(e.clonedPreviousItemStack)) return
        val p = e.player
        val newWeaponParentNode: String? = if (MainCrackShotWeaponInfoMetaManager.isSet(p)) MainCrackShotWeaponInfoMetaManager.getParentNode(p) else null
        // 이전 템 쿨타임 적용 중 이였다면, 삭제
        if (p.getCooldown(e.clonedPreviousItemStack.type) > 1) {
            p.setCooldown(e.clonedPreviousItemStack.type, 0)
        }
        val configSwapDelay = CSDirector.getInstance().getInt("$newWeaponParentNode.Addition.Weapon_Swap_Delay")
        if (newWeaponParentNode != null) {
            Bukkit.getServer().pluginManager.callEvent(WeaponSwapEvent(p, e.newItemStack, newWeaponParentNode, configSwapDelay))
            val convertedSwapDelay = System.currentTimeMillis() + configSwapDelay * 50 // 1tick = 1ms * 50
            swapDelayOfPlayers[p.uniqueId] = convertedSwapDelay
            p.setCooldown(e.newItemStack.type, configSwapDelay)
            Bukkit.getScheduler().runTaskLater(CrackShotAddition.instance, {
                if (MainCrackShotWeaponInfoMetaManager.isSet(p) &&
                        swapDelayOfPlayers.containsKey(p.uniqueId) && swapDelayOfPlayers[p.uniqueId] == convertedSwapDelay) { // 전에 넣은 딜레이 값이랑 똑같은지 확인
                    Bukkit.getServer().pluginManager.callEvent(WeaponSwapCompleteEvent(p, e.newItemStack, newWeaponParentNode))
                }
            }, configSwapDelay.toLong())
        }
    }

    //    
    //    크랙샷 무기 모든 행위 캔슬시키기
    //    
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPrepareShoot(e: WeaponPrepareShootEvent) {
        if (swapDelayOfPlayers.containsKey(e.player.uniqueId)) {
            if (swapDelayOfPlayers[e.player.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.player.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onReload(e: WeaponReloadEvent) {
        if (swapDelayOfPlayers.containsKey(e.player.uniqueId)) {
            if (swapDelayOfPlayers[e.player.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.player.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onScope(e: WeaponScopeEvent) {
        if (swapDelayOfPlayers.containsKey(e.player.uniqueId)) {
            if (swapDelayOfPlayers[e.player.uniqueId]!! <= System.currentTimeMillis()) {
                swapDelayOfPlayers.remove(e.player.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }
}