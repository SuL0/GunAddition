package me.sul.crackshotaddition.weaponappearance_etc;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WeaponCameraRecoil implements Listener {
    Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = new HashSet<>(Arrays.asList(new PacketPlayOutPosition.EnumPlayerTeleportFlags[]{PacketPlayOutPosition.EnumPlayerTeleportFlags.X, PacketPlayOutPosition.EnumPlayerTeleportFlags.Y, PacketPlayOutPosition.EnumPlayerTeleportFlags.Z, PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT, PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT}));

	@EventHandler()
	public void onWeaponShoot(WeaponShootEvent e) {
		double yawValue = CSDirector.getInstance().getDouble(e.getWeaponTitle() + ".Addition.Camera_Recoil_Yaw");
		double pitchValue = CSDirector.getInstance().getDouble(e.getWeaponTitle() + ".Addition.Camera_Recoil_Pitch");
		if (yawValue != 0 || pitchValue != 0) {
			cameraRecoil(e.getPlayer(), (float) yawValue, (float) pitchValue);
		}
	}
	
    public void cameraRecoil(Player player, float f, float f2) {
        PacketPlayOutPosition packetPlayOutPosition = new PacketPlayOutPosition(0.0, 0.0, 0.0, f, f2, this.set, 0);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutPosition);
    }
}
