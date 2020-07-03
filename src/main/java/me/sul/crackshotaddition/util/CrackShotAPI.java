package me.sul.crackshotaddition.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sul.crackshotaddition.CrackShotAddition;

public class CrackShotAPI {
	public static boolean checkIfItemIsGun(ItemStack is) { return CrackShotAddition.getCSUtility().getWeaponTitle(is) != null; }

	public static String getWeaponTitle(ItemStack is) { return CrackShotAddition.getCSUtility().getWeaponTitle(is); }

	public static int getWeaponReloadAmount(Player player, String parent_node, ItemStack is) {
		return CrackShotAddition.getCSDirector().getReloadAmount(player, parent_node, is);
	}

	public static int getWeaponAmmoAmount(Player player, String parent_node, ItemStack is) {
		int currentAmmo = getWeaponAmmoAmount_CSP(is);
		if (currentAmmo == -1) {
			if (currentAmmo == -1) { // 경우 :  듀얼모드(|)가 있을 때.
				currentAmmo = CrackShotAddition.getCSDirector().getAmmoBetweenBrackets(player, parent_node, is);
			}
		}
		return currentAmmo;
	}

	private static int getWeaponAmmoAmount_CSP(ItemStack var0) {
		if (checkIfItemIsGun(var0)) {
			String var1 = var0.getItemMeta().getDisplayName();
			if (!var1.contains("ᴿ")) {
				if (!var1.contains("«")) {
					return -1;
				}

				if (var1.contains("|")) {
					return -1;
				}

				String[] var2 = var1.split("«");
				String var3;
				if (var1.contains("◀") || var1.contains("▶")) {
					if (var1.contains("◀")) {
						var3 = var2[1].split("◀")[0].replaceAll(" ", "");
						if (!var3.contains("×") && !var3.contains("?")) {
							return Integer.parseInt(var3);
						}

						return -1;
					}

					if (var1.contains("▶")) {
						var3 = var2[1].split("▶")[1].replaceAll(" ", "").split("»")[0];
						if (!var3.contains("×") && !var3.contains("?")) {
							return Integer.parseInt(var3);
						}

						return -1;
					}
				}

				var3 = var2[1].split("»")[0];
				return Integer.parseInt(var3);
			}
		}
		return -1;
	}
}
