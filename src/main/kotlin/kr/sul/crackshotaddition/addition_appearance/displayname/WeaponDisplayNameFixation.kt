package kr.sul.crackshotaddition.addition_appearance.displayname

import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.nbtapi.NbtItem
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
                    val heldIs = p.inventory.itemInMainHand
                    if (CrackShotAdditionAPI.isValidCrackShotWeapon(heldIs)) {
                        val nbti = NbtItem(heldIs)
                        nbti.tag.setBoolean(NBT_FOR_NAME_FIXATION, bool)
                        nbti.applyToOriginal()
                    }
                }
            }
        }.runTaskTimer(csDirector, 20L, 20L)
    }
}