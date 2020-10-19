package kr.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.CSMinion
import com.shampaggon.crackshot.CSUtility
import kr.sul.crackshotaddition.addition.CancelWeaponDrop
import kr.sul.crackshotaddition.addition.WeaponSwapDelay
import kr.sul.crackshotaddition.addition.WeaponSwapSound
import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoManager
import kr.sul.crackshotaddition.resourcepackdurability.lefthand.LeftHandResourcePackDurability
import kr.sul.crackshotaddition.resourcepackdurability.weaponitem.WeaponItemResourcePackDurability
import kr.sul.crackshotaddition.weaponappearance.WeaponBlockBreakEffect
import kr.sul.crackshotaddition.weaponappearance.WeaponCameraRecoil
import kr.sul.crackshotaddition.weaponappearance.WeaponMuzzleFlash
import kr.sul.crackshotaddition.weaponappearance.WeaponProjectileTrail
import kr.sul.crackshotaddition.weaponappearance.item.WeaponDisplayNameController
import kr.sul.crackshotaddition.weaponappearance.item.WeaponDisplayNameFixation
import kr.sul.crackshotaddition.weaponappearance.item.WeaponItemFlutterFixation
import kr.sul.crackshotaddition.weapons.FlameThrower
import kr.sul.servercore.util.ObjectInitializer
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File




class CrackShotAddition : JavaPlugin {
    constructor() : super() {}
    constructor(loader: JavaPluginLoader?, description: PluginDescriptionFile?, dataFolder: File?, file: File?) : super(loader, description, dataFolder, file) {}


    companion object {
        lateinit var plugin: Plugin private set
        val csDirector = Bukkit.getPluginManager().getPlugin("CrackShot") as CSDirector
        val csUtility = CSUtility()
        val csMinion: CSMinion = CSMinion.getInstance()
    }

    override fun onEnable() {
        plugin = this
        registerClasses()
        getCommand("csa").executor = DebuggingCommand
    }

    private fun registerClasses() {
        Bukkit.getServer().pluginManager.registerEvents(FlameThrower, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponDisplayNameController, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponItemFlutterFixation, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponMuzzleFlash, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapSound, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponBlockBreakEffect, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponProjectileTrail, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponCameraRecoil, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapDelay, this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerInvAmmoInfoManager, this)
        Bukkit.getServer().pluginManager.registerEvents(CancelWeaponDrop, this)
        Bukkit.getServer().pluginManager.registerEvents(LeftHandResourcePackDurability, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponItemResourcePackDurability, this)
        ObjectInitializer.forceInit(WeaponDisplayNameFixation::class.java)
    }
}