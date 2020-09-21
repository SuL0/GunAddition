package me.sul.crackshotaddition.weaponappearance_item

import com.shampaggon.crackshot.CSDirector
import de.tr7zw.nbtapi.NBTItem
import me.sul.crackshotaddition.util.CrackShotAdditionAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object WeaponDisplayNameFixation {
    private const val NBT_FOR_NAME_FIXATION = "NbtForNameFixation"
    private var bool = true
    init {
        object : BukkitRunnable() {
            override fun run() {
                bool = !bool
                for (p in Bukkit.getServer().onlinePlayers) {
                    val mainIs = p.inventory.itemInMainHand
                    if (CrackShotAdditionAPI.isValidCrackShotWeapon(mainIs)) {
                        val nbti = NBTItem(mainIs.clone())
                        nbti.setBoolean(NBT_FOR_NAME_FIXATION, bool)
                        mainIs.itemMeta = nbti.item.itemMeta
                    }
                }
            }
        }.runTaskTimer(CSDirector.getInstance(), 20L, 20L)
    }
}