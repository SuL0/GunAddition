package me.sul.crackshotaddition;

import me.sul.crackshotaddition.util.CrackShotAPI;
import me.sul.servercore.playertoolchangeevent.PlayerMainItemChangeEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class WeaponSounds implements Listener {
	HashMap<UUID, String> previousSwapSound = new HashMap<UUID, String>();

	public static String getSwapSound(ItemStack item) {
		if (CrackShotAPI.checkIfItemIsGun(item)) {
			if (item.getItemMeta().hasLore()) {
				for (String lore: item.getItemMeta().getLore()) {
					if (lore.contains("����")) {
						return "ardraw";
					} else if (lore.contains("����")) {
						return "sgpump";
					} else if (lore.contains("�����")) {
	//					return "sgshoot";
					} else if (lore.contains("����")) {
	//					return "sgshoot";
					} else if (lore.contains("������")) {
						return "srdraw";
					} else if (lore.contains("�������")) {
						return "smgdraw";
					}
				}
			}
		}
		return "itemswap";
	}
	

	@EventHandler
	public void onSwap(PlayerMainItemChangeEvent e) {
		Player p = e.getPlayer();	
		if (!(e.getNewItem().getType().equals(Material.AIR))) {
			String sound = getSwapSound(e.getNewItem());
			if (previousSwapSound.containsKey(p.getUniqueId())) {
				p.stopSound(previousSwapSound.get(p.getUniqueId()));
			}
			p.playSound(p.getLocation(), sound, 1, 1);
			previousSwapSound.put(p.getUniqueId(), sound);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		previousSwapSound.remove(e.getPlayer().getUniqueId());
	}
}
