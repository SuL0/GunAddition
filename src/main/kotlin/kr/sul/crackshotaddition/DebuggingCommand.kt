package kr.sul.crackshotaddition

import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoManager
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object DebuggingCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false

        when(args[0].toLowerCase()) {
            "chest" -> {
                val block = sender.getTargetBlock(null, 20)
                if (block.type == Material.CHEST) {
                    val chest = block.state as Chest
                    chest.customName = "준비중"
                    chest.update() // 블럭에 조작을 가해서 NBT Tag가 없는 경우는 update가 무시될 수도 있음.
                    sender.openInventory(chest.blockInventory)
                    Bukkit.getScheduler().runTaskLater(CrackShotAddition.plugin, {
                        chest.customName = "완료"
                        chest.update()
                        sender.openInventory(chest.blockInventory)
                    }, 20L)
                }
            }
            "info" -> {
                sendM(sender, "")
                val weaponInfo = WeaponInfoExtractor(sender, sender.inventory.itemInMainHand)

                sendM(sender, "")
                sendM(sender, "ItemStack.displayName: ${weaponInfo.item.itemMeta.displayName}")
                sendM(sender, "ItemStack.nbtName: ${weaponInfo.nbtName}")
                sendM(sender, "ItemStack.durability: ${weaponInfo.item.durability}")
                sendM(sender, "ParentNode: ${weaponInfo.parentNode}")
                sendM(sender, "ConfigName: ${weaponInfo.configName}")
                sendM(sender, "UniqueId: ${weaponInfo.uniqueId}")
                sendM(sender, "ReloadEnabled: ${weaponInfo.reloadEnabled}")
                sendM(sender, "LeftAmmoAmount: ${weaponInfo.leftAmmoAmt}")
                sendM(sender, "RightAmmoAmount: ${weaponInfo.rightAmmoAmt}")
                sendM(sender, "ReloadAmmoAmount: ${weaponInfo.reloadCapacity}")
                sendM(sender, "AmmoItemMaterial: ${weaponInfo.ammoNeeded?.itemInfo}")
                sendM(sender, "TakeAsMagazine: ${weaponInfo.takeAsMagazine}")
            }
            "ammoinfo" -> {
                val playerInvAmmoInfo = PlayerInvAmmoInfoManager.getInfo(sender)
                sendM(sender, "")
                sendM(sender, "<AmmoInfo>")
                for ((key, value) in playerInvAmmoInfo.allOfPossessedAmmoAmt) {
                    sendM(sender, "$key : $value")
                    sendM(sender, " §7- Usage: ${key.whereToUse}")
                }
            }
            "nbtname" -> {
                val item = sender.inventory.itemInMainHand
                val weaponInfo = WeaponInfoExtractor(sender, item)
                sendM(sender, "NBTName: ${weaponInfo.nbtName}")
            }
            "dur" -> {
                val offItem = sender.inventory.itemInOffHand
                val heldItem = sender.inventory.itemInMainHand
                sendM(sender, "")
                sendM(sender, "${offItem.durability} / ${heldItem.durability}")
            }
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }
}