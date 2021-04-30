package kr.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.CSMinion
import kr.sul.crackshotaddition.addition.CancelInterationWithoutOpenable
import kr.sul.crackshotaddition.addition.SpeedOnScope
import kr.sul.crackshotaddition.addition.WeaponSwapDelay
import kr.sul.crackshotaddition.addition.WeaponSwapSound
import kr.sul.crackshotaddition.addition_appearance.*
import kr.sul.crackshotaddition.addition_appearance.camera_recoil.WeaponCameraRecoil
import kr.sul.crackshotaddition.addition_appearance.displayname.WeaponDisplayNameController
import kr.sul.crackshotaddition.addition_appearance.displayname.WeaponDisplayNameFixation
import kr.sul.crackshotaddition.addition_appearance.itemmgr.GunInMainHandFor1P
import kr.sul.crackshotaddition.addition_appearance.itemmgr.GunInOffHandFor3P
import kr.sul.crackshotaddition.weapon.FlameThrower
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
        internal lateinit var plugin: Plugin private set
        internal val csDirector = Bukkit.getPluginManager().getPlugin("CrackShot") as CSDirector
        internal val csMinion: CSMinion = CSMinion.getInstance()
    }

    override fun onEnable() {
        plugin = this
        registerClasses()
        getCommand("csa").executor = DebuggingCommand
    }

    private fun registerClasses() {
        Bukkit.getServer().pluginManager.registerEvents(FlameThrower, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponDisplayNameController, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponMuzzleFlash, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapSound, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponBlockBreakEffect, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponProjectileTrail, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponCameraRecoil, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapDelay, this)
//        Bukkit.getServer().pluginManager.registerEvents(CancelWeaponDrop, this)
        Bukkit.getServer().pluginManager.registerEvents(GunInMainHandFor1P, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponItemFlutterFixation, this)
        Bukkit.getServer().pluginManager.registerEvents(GunInOffHandFor3P, this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponCartridgeCase, this)
        Bukkit.getServer().pluginManager.registerEvents(CancelInterationWithoutOpenable, this)
        Bukkit.getServer().pluginManager.registerEvents(SpeedOnScope, this)
        ObjectInitializer.forceInit(WeaponDisplayNameFixation::class.java)
    }
}