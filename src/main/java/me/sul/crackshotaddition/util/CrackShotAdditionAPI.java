package me.sul.crackshotaddition.util;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import com.shampaggon.crackshot.itemcontroller.ItemDisplayController;
import me.sul.servercore.serialnumber.SerialNumberAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sul.crackshotaddition.CrackShotAddition;

public class CrackShotAdditionAPI {
	public static boolean isValidCrackShotWeapon(ItemStack is) {
		return (is.getType() != Material.AIR &&
				CrackShotAddition.getCSUtility().getWeaponTitle(is) != null &&
				SerialNumberAPI.hasSerialNumber(is));
	}

	public static String getWeaponTitle(ItemStack is) { return CrackShotAddition.getCSUtility().getWeaponTitle(is); }

	public static int getWeaponReloadAmount(Player player, String parent_node, ItemStack is) {
		return CrackShotAddition.getCSDirector().getReloadAmount(player, parent_node, is);
	}

	public static int getWeaponAmmoAmount(Player player, String parent_node, ItemStack is) {
		// TODO: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
		return CrackShotAddition.getCSDirector().getAmmoBetweenBrackets(player, parent_node, is);
	}

	public static String getINameFromConfig(String parentNode) {
		return CSDirector.getInstance().getString(parentNode + ".Item_Information.Item_Name");
	}
}
