package kr.sul.crackshotaddition

import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object DebuggingCommand : CommandExecutor {
    var distortion = true

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        if (args[0].equals("chest", ignoreCase = true)) {
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
        } else if (args[0].equals("cool", ignoreCase = true)) {
            sender.setCooldown(Material.DIAMOND_PICKAXE, 20)
        } else if (args[0].equals("info", ignoreCase = true)) {
            sendM(sender, "")
            val weaponInfo = MainCrackShotWeaponInfoManager.get(sender)
            if (weaponInfo == null) {
                sendM(sender, "null")
                return true
            }
            sendM(sender, "ItemStack.displayName: ${weaponInfo.getMainItem().itemMeta.displayName}")
            sendM(sender, "ParentNode: ${weaponInfo.parentNode}")
            sendM(sender, "ConfigName: ${weaponInfo.configName}")
            sendM(sender, "UniqueId: ${weaponInfo.uniqueId}")
            sendM(sender, "LeftAmmoAmount: ${weaponInfo.ammoInfo.leftAmmoAmount}")
            sendM(sender, "RightAmmoAmount: ${weaponInfo.ammoInfo.rightAmmoAmount}")
            sendM(sender, "ReloadAmmoAmount: ${weaponInfo.ammoInfo.reloadAmount}")
            sendM(sender, "AmmoItemMaterial: ${weaponInfo.ammoInfo.ammoMaterial}")
            sendM(sender, "TakeAsMagazine: ${weaponInfo.ammoInfo.takeAsMagazine}")
            sendM(sender, "PossessedExtraAmmoAmount: ${weaponInfo.ammoInfo.possessedExtraAmmoAmount}")
        } else if (args[0].equals("distortion", ignoreCase = true)) {
            distortion = !distortion
            sendM(sender, "총알 궤적 왜곡 $distortion")
        } else if (args[0].equals("parentnode", ignoreCase = true)) {
            sendM(sender, "parentNode: " + CrackShotAdditionAPI.getWeaponParentNode(sender.inventory.itemInMainHand))
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }
}