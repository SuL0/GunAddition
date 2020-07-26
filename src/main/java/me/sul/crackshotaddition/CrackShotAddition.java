package me.sul.crackshotaddition;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import me.sul.crackshotaddition.weaponactionbar.WeaponActionbar;
import me.sul.crackshotaddition.weaponactionbar.WeaponAmmoActionbar;
import me.sul.crackshotaddition.weaponactionbar.WeaponSwappingOrReloadingActionbar;
import me.sul.crackshotaddition.weaponnamecontroller.WeaponDisplayNameController;
import me.sul.crackshotaddition.weaponnamecontroller.WeaponDisplayNameFixation;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.shampaggon.crackshot.CSUtility;

import me.sul.crackshotaddition.weapons.FlameThrower;

public class CrackShotAddition extends JavaPlugin implements Listener {
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
//		registerWeaponActionbar();
		registerWeapons();
		registerWeaponDisplayNameController();
		registerEvents();
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("csa").setExecutor(new DebuggingCommand());

	}
	private void registerWeaponActionbar() {
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponActionbar(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponAmmoActionbar(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwappingOrReloadingActionbar(), this);
	}
	private void registerWeapons() {
		Bukkit.getServer().getPluginManager().registerEvents(new FlameThrower(), this);
	}
	private void registerWeaponDisplayNameController() {
		new WeaponDisplayNameFixation();
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponDisplayNameController(), this);
	}
	private void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwapSound(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponBlockBreakEffect(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponProjectileTrail(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponCameraRecoil(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponSwapDelay(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponMuzzleFlash(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeaponItemFlutterFixation(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MainCrackShotWeaponInfoMetaManager(), this);
	}

	public static CrackShotAddition getInstance() {
		return instance;
	}
	public static CSUtility getCsUtility() {
		return csUtility;
	}
	public static CSDirector getCsDirector() { return csDirector; }
	public static CSDirector getCSDirector() { return csDirector; }
	public static CSMinion getCSMinion() { return csMinion; }
}
