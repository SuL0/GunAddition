package kr.sul.crackshotaddition.weaponappearance.firstperson

import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

object WeaponCameraRecoil : Listener {
    var set: Set<EnumPlayerTeleportFlags> = HashSet(listOf(EnumPlayerTeleportFlags.X, EnumPlayerTeleportFlags.Y, EnumPlayerTeleportFlags.Z, EnumPlayerTeleportFlags.X_ROT, EnumPlayerTeleportFlags.Y_ROT))

    @EventHandler
    fun onWeaponShoot(e: WeaponShootEvent) {
        val bCamerarecoil = csDirector.getBoolean("${e.parentNode}.Addition.Camera_Recoil.Enable")
        if (bCamerarecoil) {
            val yawValue = csDirector.getDouble("${e.parentNode}.Addition.Camera_Recoil.Yaw")
            val pitchValue = csDirector.getDouble("${e.parentNode}.Addition.Camera_Recoil.Pitch")
            if (yawValue != 0.0 || pitchValue != 0.0) cameraRecoil(e.player, yawValue.toFloat(), pitchValue.toFloat())
        }
    }

    private fun cameraRecoil(player: Player, f: Float, f2: Float) {
        val packetPlayOutPosition = PacketPlayOutPosition(0.0, 0.0, 0.0, f, f2, set, 0)
        (player as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutPosition)
    }
}