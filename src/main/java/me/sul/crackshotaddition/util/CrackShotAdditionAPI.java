package me.sul.crackshotaddition.util;

import com.shampaggon.crackshot.CSDirector;
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
		// TODO: Airstrike같이 한번 쓰고 없어지는 템은 null이 뜸. (총기 내구도 때문에 파괴돼도 그럴지도?)
		return CrackShotAddition.getCSDirector().getAmmoBetweenBrackets(player, parent_node, is);
	}

	public static String getINameFromConfig(String parentNode) {
		return CSDirector.getInstance().getString(parentNode + ".Item_Information.Item_Name");
	}
}
