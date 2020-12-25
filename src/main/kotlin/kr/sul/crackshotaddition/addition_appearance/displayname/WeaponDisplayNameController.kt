package kr.sul.crackshotaddition.addition_appearance.displayname

import com.shampaggon.crackshot.events.WeaponAttachmentToggleEvent
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent
import com.shampaggon.crackshot.events.WeaponReloadEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import com.shampaggon.crackshot.magazine.MagazineInInv
import com.shampaggon.crackshot.magazine.MagazineItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.event.WeaponSwapCompleteEvent
import kr.sul.crackshotaddition.event.WeaponSwapEvent
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import kr.sul.servercore.extensionfunction.UpdateInventorySlot
import kr.sul.servercore.extensionfunction.UpdateInventorySlot.updateInventorySlot
import kr.sul.servercore.inventoryevent.InventoryItemChangedEvent
import kr.sul.servercore.inventoryevent.PlayerHeldItemIsChangedToAnotherEvent
import kr.sul.servercore.util.ItemBuilder.nameIB
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack


object WeaponDisplayNameController : Listener {
    enum class DisplayNameType {
        NORMAL, RELOADING, SWAPPING
    }
    private const val AMMO_ICON1 = "§f訢 " // §f 없으면 색 이상해짐
    private const val MIDDLE_BLANK_LENGTH = 3

    private const val INFINITY_DISPLAY = "§d무한"
    private const val RELOADING_DISPLAY = "§c장전중.."
    private const val SWAPPING_DISPLAY = "§c교체중.."

    // NORMAL //
    @EventHandler(priority = EventPriority.NORMAL) // onSwap보다 선행돼야 함 (Swap에게 덮어 씌워져야하기 때문)
    fun onPlayerHeldItemChanged(e: PlayerHeldItemIsChangedToAnotherEvent) {
        if (e.isChangedToCrackShotWeapon()) {
            updateWeaponDisplay(e.player, DisplayNameType.NORMAL, e.newItemStack) // updateHeldWeaponDisplay 쓰면 안됨. p.itemInMainHand가 previousItem이기 때문.
        }
    }

    @EventHandler
    fun onShoot(e: WeaponShootEvent) {
        if (e.player.inventory.itemInMainHand.type == Material.AIR) return // 소모성 아이템을 사용했을 때
        updateHeldWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    @EventHandler
    fun onReloadComplete(e: WeaponReloadCompleteEvent) {
        updateHeldWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onAttachmentToggle(e: WeaponAttachmentToggleEvent) {
        if (e.isCancelled) return
        Bukkit.getScheduler().runTaskLater(plugin, {
            updateWeaponDisplay(e.player, DisplayNameType.NORMAL, e.itemStack)
        }, 1L) // WeaponAttachmentToggleEvent가 아이템 이름 바뀌기도 전에 호출되기 때문임
    }

    @EventHandler
    fun onInventoryItemChanged(e: InventoryItemChangedEvent) {
        if (!CrackShotAdditionAPI.isValidCrackShotWeapon(e.newItemStack)) return
        updateWeaponDisplay(e.player, DisplayNameType.NORMAL, e.newItemStack)
    }

    /* 보유한 Ammo 값 업데이트 */
    // TODO: 총알 변동들을 0.5초마다 합산해서 업데이트 하도록 수정 (성능 보완하기 위함)
    // TODO: "ONLY 디스플레이용" 으로 만약 5'56 이란 총알 수를 get했다면, 1틱동안 player meta에 캐시해놓음.
    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemChanged(e: InventoryItemChangedEvent) {
        val p = e.player
        var isFullUpdate: Boolean? = null
        // 대상 총알을 사용하는 총만 선별 후 Display 업데이트
        if (MagazineItem.isMagazine(e.newItemStack)) {
            isFullUpdate = false
        }
        // 모든 총들 Display 업데이트
        else if (e.newItemStack.type == Material.AIR) {
            isFullUpdate = true
        }

        // e.newItemStack == 총알 or AIR
        if (isFullUpdate != null) {
            for (weaponInInv in p.inventory.storageContents.filterNotNull().filter { CrackShotAdditionAPI.isValidCrackShotWeapon(it) }) {
                val weaponInfo = WeaponInfoExtractor(p, weaponInInv)

                if (!weaponInInv.itemMeta.displayName.contains(RELOADING_DISPLAY) && !weaponInInv.itemMeta.displayName.contains(SWAPPING_DISPLAY)) {
                    if (!isFullUpdate) {
                        if (weaponInfo.ammoEnabled && weaponInfo.ammoUse != MagazineItem(e.newItemStack, p).parentNode) {
                            continue
                        }
                    }
                    updateWeaponDisplay(p, DisplayNameType.NORMAL, weaponInInv)
                }
            }
        }
    }

    // RELOADING //
    @EventHandler(priority = EventPriority.HIGH)
    fun onReload(e: WeaponReloadEvent) {
        if (e.isCancelled) return
        updateHeldWeaponDisplay(e.player, DisplayNameType.RELOADING)
    }

    // SWAPPING //
    @EventHandler(priority = EventPriority.HIGH) // onPlayerHeldItemChanged보다 후행돼야 함 (덮어 씌워야하기 때문)
    fun onSwap(e: WeaponSwapEvent) {
        if (e.swapDelay > 0) {
            updateWeaponDisplay(e.player, DisplayNameType.SWAPPING, e.newItem)
        }
    }

    @EventHandler
    fun onSwapComplete(e: WeaponSwapCompleteEvent) {
        updateHeldWeaponDisplay(e.player, DisplayNameType.NORMAL)
    }

    private fun updateHeldWeaponDisplay(p: Player, displayNameType: DisplayNameType) {
        updateWeaponDisplay(p, displayNameType, p.inventory.itemInMainHand)
    }
    private fun updateWeaponDisplay(p: Player, displayNameType: DisplayNameType, item: ItemStack) {
        if (!WeaponInfoExtractor.isValidCrackShotWeapon(item)) throw Exception("$p, $item, $displayNameType")
        makePrettyWeaponDisplayName(p, displayNameType, item)
    }


    @JvmStatic  // 외부에서 사용할 때는 무조건 DisplayNameType.NORMAL
    fun makePrettyWeaponDisplayName(p: Player?, item: ItemStack) {
        makePrettyWeaponDisplayName(p, DisplayNameType.NORMAL, item)
    }
    // configName: String, leftAmmo: Int?, rightAmmo: Int?, reloadableAmt: Int?, hasAttachment: Boolean, selectIsLeft: Boolean?
    private fun makePrettyWeaponDisplayName(p: Player?, displayNameType: DisplayNameType, item: ItemStack) {
        val weaponInfo = WeaponInfoExtractor(p, item)
        if (weaponInfo.bRemoveUnusedTag) return // 수류탄 같은 특수무기를 위함. 이게 없으면 Infinity로 나오기 때문임
        val weaponNameBuilder = StringBuilder()

        weaponNameBuilder.append(weaponInfo.mainFixedConfigName) // 총기 이름 넣기
        for (i in 0 until MIDDLE_BLANK_LENGTH) {
            weaponNameBuilder.append(" ") // 중간 공백 넣기
        }
        // NORMAL //
        if (displayNameType == DisplayNameType.NORMAL) {
            if (weaponInfo.reloadEnabled) {

                // 왼쪽 총알 넣기
                weaponNameBuilder.append(AMMO_ICON1)
                weaponNameBuilder.append(run {
                    val name = run {
                        when (val leftAmmoAmt = weaponInfo.leftAmmoAmt) {
                            Integer.MAX_VALUE -> INFINITY_DISPLAY
                            0 -> "§c$leftAmmoAmt"
                            else -> "§f$leftAmmoAmt"
                        }
                    }
                    if (weaponInfo.hasAttachment() && weaponInfo.selectIsLeft()) insertEmphasis(name) else name
                })

                // | 와 오른쪽 총알 넣기
                if (weaponInfo.isDualWield() || weaponInfo.hasAttachment()) {
                    weaponNameBuilder.append("§f | ")
                    weaponNameBuilder.append(run {
                        val name = run {
                            when (val rightAmmoAmt = weaponInfo.rightAmmoAmt) {
                                Integer.MAX_VALUE -> INFINITY_DISPLAY
                                0 -> "§c$rightAmmoAmt"
                                else -> "§f$rightAmmoAmt"
                            }
                        }
                        if (weaponInfo.hasAttachment() && !weaponInfo.selectIsLeft()) insertEmphasis(name) else name
                    })
                    weaponNameBuilder.append("§f ") // 공백 한개
                }

                // 슬래쉬 와 보유 총알 넣기
                if (p != null) {
                    val ammoUse = weaponInfo.ammoUse
                    if (weaponInfo.ammoEnabled && ammoUse != null) {
                        val reloadableAmmoAmt = MagazineInInv.getAmmoAmt(p, ammoUse)
                        weaponNameBuilder.append("§7/")
                        weaponNameBuilder.append(run {
                            if (reloadableAmmoAmt == 0) {
                                "§40"
                            } else {
                                "§7$reloadableAmmoAmt"
                            }
                        })
                    }
                    // 무한 넣기
                    else {
                        weaponNameBuilder.append("§7/")
                        weaponNameBuilder.append(INFINITY_DISPLAY)
                    }
                }
            }
        }

        // RELOADING //
        else if (displayNameType == DisplayNameType.RELOADING) {
            weaponNameBuilder.append("${AMMO_ICON1}$RELOADING_DISPLAY")
        }

        // SWAPPING //
        else if (displayNameType == DisplayNameType.SWAPPING) {
            weaponNameBuilder.append("${AMMO_ICON1}$SWAPPING_DISPLAY")
        }
        item.nameIB(weaponNameBuilder.toString())
        p?.updateInventorySlot(UpdateInventorySlot.HandType.MAIN_HAND)
    }


    private fun insertEmphasis(str: String): String {
        val stringBuffer = StringBuffer(str)
        stringBuffer.insert(2, "§n")
        return stringBuffer.toString()
    }
}