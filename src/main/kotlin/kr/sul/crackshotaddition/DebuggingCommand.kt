package kr.sul.crackshotaddition

import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoManager
import kr.sul.crackshotaddition.infomanager.extractor.WeaponInfoExtractor
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
        if (args[0].equals("chest", true)) {
            val block = sender.getTargetBlock(null, 20)
            if (block.type == Material.CHEST) {
                val chest = block.state as Chest
                chest.customName = "준비중"
                chest.update() // 블럭에 조작을 가해서 NBT Tag가 없는 경우는 update가 무시될 수도 있음.
                sender.openInventory(chest.blockInventory)
                Bukkit.getScheduler().runTaskLater(CrackShotAddition.instance, {
                    chest.customName = "완료"
                    chest.update()
                    sender.openInventory(chest.blockInventory)
                }, 20L)
            }
        } else if (args[0].equals("info", true)) {
            sendM(sender, "")
            val weaponInfo = WeaponInfoExtractor(sender, sender.inventory.itemInMainHand)
            val playerInvAmmoInfo = PlayerInvAmmoInfoManager.getInfo(sender)

            sendM(sender, "")
            sendM(sender, "ItemStack.displayName: ${weaponInfo.item.itemMeta.displayName}")
            sendM(sender, "ItemStack.nbtName: ${weaponInfo.nbtName}")
            sendM(sender, "ParentNode: ${weaponInfo.parentNode}")
            sendM(sender, "ConfigName: ${weaponInfo.configName}")
            sendM(sender, "UniqueId: ${weaponInfo.uniqueId}")
            sendM(sender, "ReloadEnabled: ${weaponInfo.reloadEnabled}")
            sendM(sender, "LeftAmmoAmount: ${weaponInfo.leftAmmoAmt}")
            sendM(sender, "RightAmmoAmount: ${weaponInfo.rightAmmoAmt}")
            sendM(sender, "ReloadAmmoAmount: ${weaponInfo.reloadCapacity}")
            sendM(sender, "AmmoItemMaterial: ${weaponInfo.ammoNeeded?.itemInfo}")
            sendM(sender, "TakeAsMagazine: ${weaponInfo.takeAsMagazine}")
            Bukkit.getServer().broadcastMessage("")
            Bukkit.getServer().broadcastMessage("<AmmoInfo>")
            for ((key, value) in playerInvAmmoInfo.allOfPossessedAmmoAmt) {
                Bukkit.getServer().broadcastMessage("$key : $value")
                Bukkit.getServer().broadcastMessage(" §7- Usage: ${key.whereToUse}")
            }

        } else if (args[0].equals("nbtname", true)) {
            val item = sender.inventory.itemInMainHand
            val weaponInfo = WeaponInfoExtractor(sender, item)
            Bukkit.getServer().broadcastMessage("NBTName: ${weaponInfo.nbtName}")
        } else if (args[0].equals("ammo", true)) {
            val item = sender.inventory.itemInMainHand
            val weaponInfo = WeaponInfoExtractor(sender, item)
            Bukkit.getServer().broadcastMessage("getAmmoBetweenBrackets: ${CrackShotAddition.csDirector.getAmmoBetweenBrackets(sender, weaponInfo.parentNode, item)}")
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }
}