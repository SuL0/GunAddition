package kr.sul.crackshotaddition.addition_appearance.displayname

import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.csDirector
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

// 이거 static이라서 구문을 뭔가 써야하는데?
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
                        val nbti = NBTItem(heldIs.clone())
                        nbti.setBoolean(NBT_FOR_NAME_FIXATION, bool)
                        heldIs.itemMeta = nbti.item.itemMeta
                    }
                }
            }
        }.runTaskTimer(csDirector, 20L, 20L)
    }
}