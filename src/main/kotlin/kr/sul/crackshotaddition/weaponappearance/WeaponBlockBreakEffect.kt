package kr.sul.crackshotaddition.weaponappearance

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.events.WeaponHitBlockEvent
import me.sul.customentity.entityweapon.event.CEWeaponHitBlockEvent
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import java.util.*
import java.util.stream.Collectors

object WeaponBlockBreakEffect : Listener {
    @EventHandler
    fun onCEWeaponHitBlockEvent(e: CEWeaponHitBlockEvent) {
        blockBreakEffect(e.entity, e.projectile)
    }

    @EventHandler
    fun onWeaponHitBlock(e: WeaponHitBlockEvent) {
        val bBlockbreakeffect = CSDirector.getInstance().getBoolean(e.parentNode + ".Addition.Block_Break_Effect")
        if (bBlockbreakeffect) {
            blockBreakEffect(e.player, e.projectile)
        }
    }

    private fun blockBreakEffect(shooter: Entity, projectile: Entity) {
        val projStruckLoc = calcProjectileStruckLocation(projectile)
        val projStruckBlock = projStruckLoc.block
        val world = projStruckLoc.world
        if (projStruckBlock.type == Material.AIR) return
        val nearbyPlayers: MutableList<Player> = ArrayList()
        if (shooter is Player) nearbyPlayers.add(shooter) // 거리가 멀더라도 p는 무조건 포함
        nearbyPlayers.addAll(Bukkit.getServer().onlinePlayers.stream()
                .filter { loopP: Player -> loopP != shooter && loopP.world == shooter.world && loopP.location.distance(shooter.location) <= 100 }
                .collect(Collectors.toList()))
        world.spawnParticle(Particle.BLOCK_CRACK, nearbyPlayers, if (shooter is Player) shooter else null,
                projStruckLoc.x, projStruckLoc.y, projStruckLoc.z,
                20, 0.0, 0.0, 0.0, 1.0,
                MaterialData(projStruckBlock.type), true) // 1.15버전은 new MaterialData(...) -> block.getType() 만 해도됨
        world.playSound(projStruckLoc, Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.5f, 1f)
    }

    private fun calcProjectileStruckLocation(proj: Entity): Location {
        val lastestLoc = proj.location
        val lastestVec = proj.velocity
        val tempLoc = lastestLoc.clone()
        val lastestNormalizedVec = lastestVec.clone().normalize()
        var i = 1
        while (i <= Math.ceil(lastestVec.length())) {
            tempLoc.add(lastestNormalizedVec)
            if (isStruckableMaterial(tempLoc.world.getBlockAt(tempLoc).type)) {  // ProjectileHitEvent의 기준을 알아내야 함 -> 그냥 Proj가 죽었으면 호출됨
                return getBlockCornerLocation(tempLoc, lastestNormalizedVec.clone().multiply(-1))
            }
            i++
        }
        return Location(proj.world, 0.0, 0.0, 0.0)
    }

    private fun getBlockCornerLocation(loc: Location, vecParam: Vector): Location {
        var vec = vecParam
        val origMaterial = loc.block.type
        val maxI = 10 // 블럭을 10등분
        vec = vec.multiply(vec.length() / maxI)
        var previousLoc = loc.clone()
        for (i in 1..maxI + 1) {
            loc.add(vec)
            if (loc.block.type != origMaterial) {
                return previousLoc
            }
            previousLoc = loc.clone()
        }
        return Location(loc.world, 0.0, 0.0, 0.0)
    }

    private fun isStruckableMaterial(material: Material): Boolean {
        return when (material) {
            Material.AIR, Material.WATER, Material.LAVA -> false
            else -> true
        }
    }
    //	private Sound getBlockBreakSound(Block block) {
    //		WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
    //		BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
    //		nmsWorld.
    //	}
}