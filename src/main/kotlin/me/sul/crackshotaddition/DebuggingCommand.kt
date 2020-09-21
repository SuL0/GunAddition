package me.sul.crackshotaddition

import me.sul.crackshotaddition.util.CrackShotAdditionAPI
import me.sul.crackshotaddition.weaponappearance_etc.WeaponProjectileTrail
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebuggingCommand : CommandExecutor {
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
            sendM(sender, "isSet: " + MainCrackShotWeaponInfoMetaManager.isSet(sender))
            sendM(sender, "getItemStack: " + MainCrackShotWeaponInfoMetaManager.getItemStack(sender)!!.getItemMeta().getDisplayName())
            sendM(sender, "getParentNode: " + MainCrackShotWeaponInfoMetaManager.getParentNode(sender))
            sendM(sender, "getConfigName: " + MainCrackShotWeaponInfoMetaManager.getConfigName(sender))
            sendM(sender, "getLeftAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getLeftAmmoAmount(sender))
            sendM(sender, "getRightAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getRightAmmoAmount(sender))
            sendM(sender, "getReloadAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getReloadAmmoAmount(sender))
            sendM(sender, "getAmmoItemMaterial: " + MainCrackShotWeaponInfoMetaManager.getAmmoItemMaterial(sender))
            sendM(sender, "getReloadAmountPerAmmo: " + MainCrackShotWeaponInfoMetaManager.getReloadAmountPerAmmo(sender))
            sendM(sender, "getPossessedExtraAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getPossessedExtraAmmoAmount(sender))
            sendM(sender, "getUniqueId: " + MainCrackShotWeaponInfoMetaManager.getUniqueId(sender))
        } else if (args[0].equals("distortion", ignoreCase = true)) {
            distortion = !distortion
            sendM(sender, "총알 궤적 왜곡 $distortion")
        } else if (args[0].equals("parentnode", ignoreCase = true)) {
            sendM(sender, "parentNode: " + CrackShotAdditionAPI.getWeaponParentNode(sender.itemInHand))
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }

    companion object {
        var distortion = true
    }
}