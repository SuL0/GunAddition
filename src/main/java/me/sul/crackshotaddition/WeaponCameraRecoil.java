package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WeaponCameraRecoil implements Listener {
    Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = new HashSet<PacketPlayOutPosition.EnumPlayerTeleportFlags>(Arrays.asList(new PacketPlayOutPosition.EnumPlayerTeleportFlags[]{PacketPlayOutPosition.EnumPlayerTeleportFlags.X, PacketPlayOutPosition.EnumPlayerTeleportFlags.Y, PacketPlayOutPosition.EnumPlayerTeleportFlags.Z, PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT, PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT}));

	@EventHandler()
	public void onWeaponShoot(WeaponShootEvent e) {
		if (e.getWeaponTitle().equals("N화염방사기")) {
			cameraRecoil(e.getPlayer(), 0, 0);
		} else {
			cameraRecoil(e.getPlayer(), 0, -0.5F);
		}
	}
	
    public void cameraRecoil(Player player, float f, float f2) {
        PacketPlayOutPosition packetPlayOutPosition = new PacketPlayOutPosition(0.0, 0.0, 0.0, f, f2, this.set, 0);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutPosition);
    }
}
