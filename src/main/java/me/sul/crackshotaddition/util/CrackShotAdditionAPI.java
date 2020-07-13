package me.sul.crackshotaddition.util;

import com.shampaggon.crackshot.CSMinion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sul.crackshotaddition.CrackShotAddition;

public class CrackShotAdditionAPI {
	public static boolean checkIfItemIsGun(ItemStack is) { return CrackShotAddition.getCSUtility().getWeaponTitle(is) != null; }

	public static String getWeaponTitle(ItemStack is) { return CrackShotAddition.getCSUtility().getWeaponTitle(is); }

	public static int getWeaponReloadAmount(Player player, String parent_node, ItemStack is) {
		return CrackShotAddition.getCSDirector().getReloadAmount(player, parent_node, is);
	}

	public static int getWeaponAmmoAmount(Player player, String parent_node, ItemStack is) {
		int currentAmmo = getWeaponAmmoAmount_CSP(is);
		if (currentAmmo == -1) {
			// 듀얼모드(|)가 있을 때
			// SEE: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
			currentAmmo = CrackShotAddition.getCSDirector().getAmmoBetweenBrackets(player, parent_node, is);
		}
		return currentAmmo;
	}

	private static int getWeaponAmmoAmount_CSP(ItemStack is) {
		if (checkIfItemIsGun(is)) {
			String nbtItemName = CSMinion.getInstance().getItemNbtName(is);
			if (!nbtItemName.contains("ᴿ")) {
				if (!nbtItemName.contains("«")) {
					return -1;
				}

				if (nbtItemName.contains("|")) {
					return -1;
				}

				String[] var2 = nbtItemName.split("«");
				String var3;
				if (nbtItemName.contains("◀") || nbtItemName.contains("▶")) {
					if (nbtItemName.contains("◀")) {
						var3 = var2[1].split("◀")[0].replaceAll(" ", "");
						if (!var3.contains("×") && !var3.contains("?")) {
							return Integer.parseInt(var3);
						}

						return -1;
					}

					if (nbtItemName.contains("▶")) {
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
