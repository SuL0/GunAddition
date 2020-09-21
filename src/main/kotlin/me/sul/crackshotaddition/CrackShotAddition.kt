package me.sul.crackshotaddition

import com.shampaggon.crackshot.CSDirector
import com.shampaggon.crackshot.CSMinion
import com.shampaggon.crackshot.CSUtility
import me.sul.crackshotaddition.weaponappearance_etc.WeaponBlockBreakEffect
import me.sul.crackshotaddition.weaponappearance_etc.WeaponCameraRecoil
import me.sul.crackshotaddition.weaponappearance_etc.WeaponMuzzleFlash
import me.sul.crackshotaddition.weaponappearance_etc.WeaponProjectileTrail
import me.sul.crackshotaddition.weaponappearance_item.WeaponDisplayNameController
import me.sul.crackshotaddition.weaponappearance_item.WeaponItemFlutterFixation
import me.sul.crackshotaddition.weapons.FlameThrower
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

// object로 선언하는게 맞지 않을까?
class CrackShotAddition : JavaPlugin() {
    companion object {
        lateinit var instance: CrackShotAddition
        var csUtility = CSUtility()
        var csDirector = Bukkit.getPluginManager().getPlugin("CrackShot") as CSDirector
        var csMinion: CSMinion = CSMinion.getInstance()
    }

    override fun onEnable() {
        instance = this
        registerClasses()
        getCommand("csa").executor = DebuggingCommand()
    }

    private fun registerClasses() {
        Bukkit.getServer().pluginManager.registerEvents(FlameThrower(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponDisplayNameController(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponItemFlutterFixation(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponMuzzleFlash(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapSound(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponBlockBreakEffect(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponProjectileTrail(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponCameraRecoil(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeaponSwapDelay(), this)
        Bukkit.getServer().pluginManager.registerEvents(MainCrackShotWeaponInfoMetaManager(), this)
    }
}