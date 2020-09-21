package me.sul.crackshotaddition.projectile

import me.sul.crackshotaddition.CrackShotAddition
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

// 현재 ProjectileManager은 객체 생성부분도 지워놨음
// NOTE: 이건 패키지 없이 ProjectileGraze로 생성해야하지 않을까?
class ProjectileManager {
    // 총알 스쳤을 때
    // FIXME: 씨발 이거 Runnable에 cancel()이 없는데?
    fun projectileGraze(projectile: Entity, shooter: Player?) {
        object : BukkitRunnable() {
            var excludePlayers: MutableList<Player> = ArrayList(listOf(shooter))
            override fun run() {
                if (!projectile.isValid) {
                    cancel()
                    return
                }
                val loc = projectile.location
                val players = loc.world.players.toMutableList()
                for (p in players) {
                    if (p.location.distance(loc) <= 3 && !excludePlayers.contains(p)) {
                        excludePlayers.add(p)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rsp p " + p.name + " MASTER block.enchantment_table.use 1 0 false")
                    }
                }
            }
        }.runTaskTimer(CrackShotAddition.instance as Plugin, 0, 1)
    }
}