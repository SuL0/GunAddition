package kr.sul.crackshotaddition

import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoManager
import kr.sul.crackshotaddition.infomanager.heldweapon.PlayerHeldWeaponInfoManager
import kr.sul.crackshotaddition.util.CrackShotAdditionAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DebuggingCommand : CommandExecutor {
    var distortion = true

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
        } else if (args[0].equals("cool", true)) {
            sender.setCooldown(Material.DIAMOND_PICKAXE, 20)
        } else if (args[0].equals("info", true)) {
            sendM(sender, "")
            val weaponInfo = PlayerHeldWeaponInfoManager.getInfo(sender)!!
            val ammoInfo = PlayerInvAmmoInfoManager.getInfo(sender)

            sendM(sender, "")
            sendM(sender, "ItemStack.displayName: ${weaponInfo.getHeldItem().itemMeta.displayName}")
            sendM(sender, "ParentNode: ${weaponInfo.parentNode}")
            sendM(sender, "ConfigName: ${weaponInfo.configName}")
            sendM(sender, "UniqueId: ${weaponInfo.uniqueId}")
            sendM(sender, "LeftAmmoAmount: ${weaponInfo.leftAmmoAmount}")
            sendM(sender, "RightAmmoAmount: ${weaponInfo.rightAmmoAmount}")
            sendM(sender, "ReloadAmmoAmount: ${weaponInfo.reloadCapacity}")
            sendM(sender, "AmmoItemMaterial: ${weaponInfo.ammo?.itemInfo}")
            sendM(sender, "TakeAsMagazine: ${weaponInfo.takeAsMagazine}")
            Bukkit.getServer().broadcastMessage("")
            Bukkit.getServer().broadcastMessage("<AmmoInfo>")
            for ((key, value) in ammoInfo.possessedAmmoList) {
                Bukkit.getServer().broadcastMessage("$key : $value")
                Bukkit.getServer().broadcastMessage(" §7- Usage: ${key.whereToUse}")
            }

        } else if (args[0].equals("distortion", true)) {
            distortion = !distortion
            sendM(sender, "총알 궤적 왜곡 $distortion")
        } else if (args[0].equals("parentnode", true)) {
            sendM(sender, "parentNode: " + CrackShotAdditionAPI.getWeaponParentNode(sender.inventory.itemInMainHand))
        } else if (args[0] == "fromJava") {
            if (args[1] == "default") {
                val item = sender.inventory.getItem(0)
                sendM(sender, "-default-")
                sendM(sender, "item : ${item.type}")
            }
            else if (args[1] == "withElvis") {
                val item = sender.inventory.getItem(0)?.clone() ?: ItemStack(Material.DIAMOND)
                sendM(sender, "-withElvis-")
                sendM(sender, "item : ${item.type}")
            }
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }
}