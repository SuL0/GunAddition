package me.sul.crackshotaddition.util;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import me.sul.servercore.serialnumber.UniqueIdAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sul.crackshotaddition.CrackShotAddition;

public class CrackShotAdditionAPI {
	private static CSDirector getCSDirector() {
		return CrackShotAddition.getCSDirector();
	}
	private static CSUtility getCSUtility() {
		return CrackShotAddition.getCSUtility();
	}

	public static boolean isValidCrackShotWeapon(ItemStack is) {
		return (is.getType() != Material.AIR &&
				getWeaponParentNode(is) != null &&
				UniqueIdAPI.hasUniqueID(is));
	}

	public static String getWeaponParentNode(ItemStack is) {
		return getCSUtility().getWeaponTitle(is);
	}

	public static String getWeaponConfigName(ItemStack is) {
		return getWeaponConfigName(getWeaponParentNode(is));
	}
	public static String getWeaponConfigName(String parentNode) {
		return getCSDirector().getString(parentNode + ".Item_Information.Item_Name");
	}

	public static int getWeaponReloadAmount(Player player, String parent_node, ItemStack is) {
		return getCSDirector().getReloadAmount(player, parent_node, is);
	}

	public static int getWeaponAmmoAmount(Player player, String parent_node, ItemStack is) {
		// TODO: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
		return getCSDirector().getAmmoBetweenBrackets(player, parent_node, is);
	}
}
