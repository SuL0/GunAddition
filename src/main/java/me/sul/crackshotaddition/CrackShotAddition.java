package me.sul.crackshotaddition;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import me.sul.crackshotaddition.weaponappearance_etc.WeaponBlockBreakEffect;
import me.sul.crackshotaddition.weaponappearance_etc.WeaponCameraRecoil;
import me.sul.crackshotaddition.weaponappearance_etc.WeaponMuzzleFlash;
import me.sul.crackshotaddition.weaponappearance_etc.WeaponProjectileTrail;
import me.sul.crackshotaddition.weaponappearance_item.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.shampaggon.crackshot.CSUtility;

import me.sul.crackshotaddition.weapons.FlameThrower;

public class CrackShotAddition extends JavaPlugin {
	private static CrackShotAddition instance;
	private static CSUtility csUtility;
	private static CSDirector csDirector;
	private static CSMinion csMinion;

	@Override
	public void onEnable() {
		instance = this;
		csUtility = new CSUtility();
		csMinion = CSMinion.getInstance();
		csDirector = (CSDirector) Bukkit.getPluginManager().getPlugin("CrackShot");
		registerClasses();
		getCommand("csa").setExecutor(new DebuggingCommand());
	}
	private void registerClasses() {
		Bukkit.getServer().getPluginManager().registerEvents(new FlameThrower(), this);
		new WeaponDisplayNameFixation();
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponDisplayNameController(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponItemFlutterFixation(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponMuzzleFlash(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwapSound(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponBlockBreakEffect(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponProjectileTrail(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponCameraRecoil(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwapDelay(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MainCrackShotWeaponInfoMetaManager(), this);
	}

	public static CrackShotAddition getInstance() {
		return instance;
	}
	public static CSUtility getCsUtility() {
		return csUtility;
	}
	public static CSDirector getCsDirector() { return csDirector; }
	public static CSMinion getCsMinion() { return csMinion; }
}
