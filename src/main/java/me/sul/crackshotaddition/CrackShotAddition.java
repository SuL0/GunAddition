package me.sul.crackshotaddition;

import com.shampaggon.crackshot.CSDirector;
import me.sul.crackshotaddition.weaponactionbar.WeaponActionbar;
import me.sul.crackshotaddition.weaponactionbar.WeaponAmmoActionbar;
import me.sul.crackshotaddition.weaponactionbar.WeaponSwappingOrReloadingActionbar;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.shampaggon.crackshot.CSUtility;

import me.sul.crackshotaddition.weapons.FlameThrower;

public class CrackShotAddition extends JavaPlugin implements Listener {
	private static CrackShotAddition instance;
	private static CSUtility CSUtility;
	private static CSDirector CSDirector;

	@Override
	public void onEnable() {
		instance = this;
		CSUtility = new CSUtility();
		CSDirector = (CSDirector) Bukkit.getPluginManager().getPlugin("CrackShot");

		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSounds(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponBlockBreakEffect(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponProjectileTrail(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponCameraRecoil(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwapDelay(), this);

		Bukkit.getServer().getPluginManager().registerEvents(new FlameThrower(), this);

		Bukkit.getServer().getPluginManager().registerEvents(new WeaponActionbar(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponAmmoActionbar(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwappingOrReloadingActionbar(), this);

		Bukkit.getPluginManager().registerEvents(this, this);
		
		
	}
	
	public static CrackShotAddition getInstance() {
		return instance;
	}
	public static CSUtility getCSUtility() {
		return CSUtility;
	}
	public static CSDirector getCSDirector() { return CSDirector; }
}
