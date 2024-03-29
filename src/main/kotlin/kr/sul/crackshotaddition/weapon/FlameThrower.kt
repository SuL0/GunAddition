package kr.sul.crackshotaddition.weapon

import com.shampaggon.crackshot.events.WeaponHitBlockEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable

object FlameThrower : Listener {
    private const val FLAMETHROWER = "N화염방사기"

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        if (e.parentNode == FLAMETHROWER) {
            val proj = e.projectile
            object : BukkitRunnable() {
                var previousLoc: Location? = null
                var skipFirstLoc = true
                var passedTick = 0
                override fun run() {
                    if (!proj.isValid) {
                        cancel(); return
                    }
                    val loc = proj.location
                    if (!skipFirstLoc) {
                        loc.getWorld().spawnParticle(Particle.LAVA, loc, 1)
                    } else {
                        skipFirstLoc = false
                    }
                    if (previousLoc != null && loc == previousLoc) {
                        cancel(); return
                    }
                    if (passedTick >= 10) {
                        e.projectile.remove()
                        cancel(); return
                    }
                    passedTick += 2
                    previousLoc = loc
                }
            }.runTaskTimer(plugin, 0, 2)
        }
    }

    @EventHandler
    fun onWeaponHitBlock(e: WeaponHitBlockEvent) {
        if (e.parentNode == FLAMETHROWER) {
            val block = e.block
            if (block.getRelative(BlockFace.UP).type == Material.AIR) {
                block.getRelative(BlockFace.UP).type = Material.FIRE
                object : BukkitRunnable() {
                    override fun run() {
                        if (block.getRelative(BlockFace.UP).type == Material.FIRE) {
                            block.getRelative(BlockFace.UP).type = Material.AIR
                        }
                    }
                }.runTaskLater(plugin, 20)
            }
        }
    }
}