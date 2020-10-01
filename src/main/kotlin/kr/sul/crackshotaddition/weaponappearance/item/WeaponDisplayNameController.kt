package kr.sul.crackshotaddition.weaponappearance.item

import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import kr.sul.crackshotaddition.infomanager.heldweapon.PlayerHeldWeaponInfoManager
import kr.sul.crackshotaddition.events.WeaponSwapCompleteEvent
import kr.sul.crackshotaddition.events.WeaponSwapEvent
import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoManager
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToOnotherEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object WeaponDisplayNameController : Listener {
    internal enum class DisplayNameType {
        NORMAL, RELOADING, SWAPPING
    }
    private const val AMMO_ICON1 = "§f锄 " // §f 없으면 색 이상해짐
    private const val MIDDLE_BLANK_LENGTH = 7

    // NORMAL
    @EventHandler(priority = EventPriority.NORMAL) // onSwap보다 선행돼야 함 (Swap에게 덮어 씌워져야하기 때문)
    fun onPlayerHeldItemChanged(e: PlayerHeldItemIsChangedToOnotherEvent) {
        if (e.isChangedToCrackShotWeapon()) {
            updateMainWeaponDisplay(e.player, DisplayNameType.NORMAL)
        }
    }

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        updateMainWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        updateMainWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    @EventHandler
    fun onItemChanged(e: InventoryItemChangedEvent) {
        val p = e.player
        val ammoItemStack = PlayerHeldWeaponInfoManager.getInfo(p)?.ammo?.itemStack
        if (ammoItemStack != null && (e.newItemStack.type == ammoItemStack.type || e.newItemStack.type == Material.AIR)) {
            // Reload나 Swapping중이였다면, 이게 무시돼야 함
            val heldItemName = PlayerHeldWeaponInfoManager.getInfo(p)!!.getHeldItem().itemMeta.displayName
            if (heldItemName.contains(DisplayNameType.RELOADING.name) || heldItemName.contains(DisplayNameType.SWAPPING.name)) return

            updateMainWeaponDisplay(p, DisplayNameType.NORMAL)
        }
    }

    // RELOADING
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (e.isCancelled) return
        updateMainWeaponDisplay(e.player, DisplayNameType.RELOADING)
    }

    // SWAPPING
    @EventHandler(priority = EventPriority.HIGH) // onPlayerHeldItemChanged보다 후행돼야 함 (덮어 씌워야하기 때문)
    fun onSwap(e: WeaponSwapEvent) {
        if (e.swapDelay > 0) {
            updateMainWeaponDisplay(e.player, DisplayNameType.SWAPPING)
        }
    }

    @EventHandler
    fun onSwapComplete(e: WeaponSwapCompleteEvent) {
        updateMainWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    private fun updateMainWeaponDisplay(p: Player, displayNameType: DisplayNameType) {
        val weaponInfo = PlayerHeldWeaponInfoManager.getInfo(p)!!
        val weapon = weaponInfo.getHeldItem()
        val configName = weaponInfo.configName
        var leftAmmo: Int? = null
        var rightAmmo: Int? = null
        var possessedExtraAmmoAmt: Int? = null
        if (displayNameType == DisplayNameType.NORMAL) {
            leftAmmo = weaponInfo.leftAmmoAmount
            rightAmmo = weaponInfo.rightAmmoAmount

            val invAmmoInfo = PlayerInvAmmoInfoManager.getInfo(p)
            possessedExtraAmmoAmt = weaponInfo.ammo?.let { invAmmoInfo.getReloadableAmountPerWeapon(p, weapon, it) }
        }
        makePrettyWeaponDisplayName(p, displayNameType, weapon, configName, leftAmmo, rightAmmo, possessedExtraAmmoAmt)
    }



    @JvmStatic  // 외부에서 사용할 때는 무조건 DisplayNameType.NORMAL
    fun makePrettyWeaponDisplayName(p: Player?, weapon: ItemStack, configName: String, leftAmmo: Int?, rightAmmo: Int?, possessedExtraAmmoAmt: Int?) {
        makePrettyWeaponDisplayName(p, DisplayNameType.NORMAL, weapon, configName, leftAmmo, rightAmmo, possessedExtraAmmoAmt)
    }
    private fun makePrettyWeaponDisplayName(p: Player?, displayNameType: DisplayNameType, weapon: ItemStack, configName: String, leftAmmo: Int?, rightAmmo: Int?, possessedExtraAmmoAmt: Int?) {
        val meta = weapon.itemMeta
        val weaponNameBuilder = StringBuilder()
        weaponNameBuilder.append(configName) // 총기 이름 넣기
        for (i in 0 until MIDDLE_BLANK_LENGTH) {
            weaponNameBuilder.append(" ") // 중간 공백 넣기
        }
        if (displayNameType == DisplayNameType.NORMAL) {
            if (leftAmmo != Integer.MAX_VALUE) {
                // 왼쪽 총알 넣기
                if (leftAmmo == 0) {
                    weaponNameBuilder.append("${AMMO_ICON1}§c").append(leftAmmo)
                } else {
                    weaponNameBuilder.append("${AMMO_ICON1}§f").append(leftAmmo)
                }

                // | 및 오른쪽 총알 넣기
                if (rightAmmo != null) {
                    if (rightAmmo == 0) {
                        weaponNameBuilder.append(" §f| §c").append(rightAmmo).append(" ")
                    } else {
                        weaponNameBuilder.append(" §f| ").append(rightAmmo).append(" ")
                    }
                }

                // 슬래쉬 및 보유 총알 넣기
                if (possessedExtraAmmoAmt == null || possessedExtraAmmoAmt == 0) {
                    weaponNameBuilder.append("§7/§4").append(possessedExtraAmmoAmt ?: 0)
                } else {
                    weaponNameBuilder.append("§7/").append(possessedExtraAmmoAmt)
                }
            }
            // 무한
            else {
                weaponNameBuilder.append("§dInfinity")
            }
        } else if (displayNameType == DisplayNameType.RELOADING) {
            weaponNameBuilder.append("${AMMO_ICON1}§cRELOADING..")
        } else if (displayNameType == DisplayNameType.SWAPPING) {
            weaponNameBuilder.append("${AMMO_ICON1}§cSWAPPING..")
        }
        meta.displayName = weaponNameBuilder.toString()

        weapon.itemMeta = meta
        p?.updateInventory()
    }
}