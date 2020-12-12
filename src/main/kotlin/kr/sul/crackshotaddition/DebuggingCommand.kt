package kr.sul.crackshotaddition

import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.addition_appearance.WeaponCameraRecoil
import kr.sul.crackshotaddition.infomanager.ammo.PlayerInvAmmoInfoMgr
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.servercore.nbtapi.NbtItem
import kr.sul.servercore.util.ItemBuilder.loreIB
import kr.sul.servercore.util.ItemBuilder.nameIB
import kr.sul.servercore.util.UniqueIdAPI
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


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
                sendM(sender, "AmmoItemMaterial: ${weaponInfo.ammoTypeNeeded?.itemInfo}")
                sendM(sender, "TakeAsMagazine: ${weaponInfo.takeAsMagazine}")
            }
            "nbtname" -> {
                val item = sender.inventory.itemInMainHand
                val weaponInfo = WeaponInfoExtractor(sender, item)
                sendM(sender, "NBTName: ${weaponInfo.nbtName}")
            }
            "ammoinfo" -> {
                val playerInvAmmoInfo = PlayerInvAmmoInfoMgr.getInfo(sender)
                sendM(sender, "")
                sendM(sender, "<AmmoInfo>")
                for ((key, value) in playerInvAmmoInfo.allOfPossessedAmmoAmt) {
                    sendM(sender, "$key : $value")
                    sendM(sender, " §7- Usage: ${key.whereToUse}")
                }
            }
            "dur" -> {
                val offItem = sender.inventory.itemInOffHand
                val heldItem = sender.inventory.itemInMainHand
                sendM(sender, "")
                sendM(sender, "${offItem.durability} / ${heldItem.durability}")
            }

            // csa nbtspeed <테스트 회수>
            "nbtspeed" -> {
                val item = ItemStack(Material.DIAMOND_AXE).nameIB("NbtSpeed 테스트용")
                        .loreIB("탄알 수 <10>", "§숨§겨§진§문§자§숨§겨§진§문§자§숨§겨§진§문§자")
                val tempNbti = NbtItem(item)
                tempNbti.tag.setInt("int", 9)
                tempNbti.tag.setString("str", "ss")
                tempNbti.tag.setBoolean("bool", true)
                tempNbti.tag.setDouble("double", 1.0)
                tempNbti.applyToOriginal()
                val repeat = args[1].toInt()

                Bukkit.broadcastMessage("--- $repeat 회 수행 ---")

                //
                speedTest("로어 <> 사이의 값 읽기") {
                    for (i in 1..repeat) {
                        val dummy = item.lore!![0].split("<")[1].replace(">", "")
                    }
                }
                speedTest("숨겨진 로어 읽기") {
                    for (i in 1..repeat) {
                        val dummy = ChatColor.stripColor(item.lore!![1])
                    }
                }
                speedTest("일반 로어 읽기") {
                    for (i in 1..repeat) {
                        val dummy = item.lore!![1]
                    }
                }

//                speedTest("setNBT(NBTAPI)") {
//                    for (i in 1..repeat) {
//                        val nbtItem = NBTItem(item)
//                        nbtItem.setString("$i", "blabla")
////                        nbtItem.applyNBT(item)
//                        item.itemMeta = nbtItem.item.itemMeta
//                    }
//                }
//                speedTest("setNBT(NMS-overrideItem)") {
//                    for (i in 1..repeat) {
//                        val nmsItem = CraftItemStack.asNMSCopy(item)  // 이건 코스트 낮음
//                        val tag = if (nmsItem.hasTag()) nmsItem.tag!! else NBTTagCompound()
//                        tag.setString("$i", "blabla")
//                        nmsItem.tag = tag
////                        val modifiedItem = CraftItemStack.asCraftMirror(nmsItem)
//                        val modifiedItem = CraftItemStack.asBukkitCopy(nmsItem) // 여기서 시간 오래걸림
////                        item.itemMeta = modifiedItem.itemMeta
//                        item = modifiedItem
//                    }
//                }
//                speedTest("setNBT(NMS-modifyItemMeta)") {
//                    for (i in 1..repeat) {
//                        val nmsItem = CraftItemStack.asNMSCopy(item)  // 이건 코스트 낮음
//                        val tag = if (nmsItem.hasTag()) nmsItem.tag!! else NBTTagCompound()
//                        tag.setString("$i", "blabla")
//                        nmsItem.tag = tag
////                        val modifiedItem = CraftItemStack.asCraftMirror(nmsItem)
//                        val modifiedItem = CraftItemStack.asBukkitCopy(nmsItem) // 여기서 시간 오래걸림
//                        item.itemMeta = modifiedItem.itemMeta
////                        item = modifiedItem
//                    }
//                }
                speedTest("getNBT(NBTAPI)") {
                    for (i in 1..repeat) {
                        val testNbti = NBTItem(item)
                        val dummy = testNbti.getInteger("int")
                    }
                }
                Bukkit.broadcastMessage("test2--")
                speedTest("justGetTag") {
                    for (i in 1..repeat) {
                        val nmsItem = CraftItemStack.asNMSCopy(item)
                        val tag = if (nmsItem.hasTag()) nmsItem.tag!! else NBTTagCompound()
                    }
                }
                speedTest("hasKey(NMS)") {
                    for (i in 1..repeat) {
                        val nbti = NbtItem(item)
                        val dummy = nbti.tag.hasKey("int")
                    }
                }
                speedTest("getInt(NMS)") {
                    for (i in 1..repeat) {
                        val nbti = NbtItem(item)
                        val dummy = nbti.tag.getInt("int")
                    }
                }
            }
            "uidspeed" -> {
                val item = sender.inventory.itemInMainHand
                val repeat = args[1].toInt()
                Bukkit.broadcastMessage("--- $repeat 회 수행 ---")
                speedTest("hasUid") {
                    for (i in 1..repeat) {
                        val dummy = UniqueIdAPI.hasUniqueID(item)
                    }
                }
                speedTest("getUid") {
                    for (i in 1..repeat) {
                        val dummy = UniqueIdAPI.getUniqueID(item)
                    }
                }
                speedTest("carveUid") {
                    for (i in 1..repeat) {
                        UniqueIdAPI.carveUniqueID(item)
                    }
                }
            }
            "recoil" -> {
                val thread = Thread() {
                    for (i in 1..1000) {
                        WeaponCameraRecoil.cameraRecoil(sender, 0f, -0.1f)
                        Thread.sleep(1)
                    }
                }
                thread.start()
            }
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }

    private fun speedTest(taskName: String, runnable: Runnable) {
        val time = System.currentTimeMillis()
        runnable.run()
        var modifiedTaskName = taskName
        if (taskName.length < 15) {
            for (i in taskName.length until 15) {
                modifiedTaskName += " "
            }
        }
        Bukkit.broadcastMessage("§7$modifiedTaskName §f: ${System.currentTimeMillis() - time}ms [${(System.currentTimeMillis() - time) / 1000}s]")
    }
}