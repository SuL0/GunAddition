package kr.sul.crackshotaddition

import com.shampaggon.crackshot.magazine.MagazineInInv
import de.tr7zw.nbtapi.NBTItem
import kr.sul.crackshotaddition.CrackShotAddition.Companion.plugin
import kr.sul.crackshotaddition.infomanager.weapon.WeaponInfoExtractor
import kr.sul.servercore.nbtapi.NbtItem
import kr.sul.servercore.util.ItemBuilder.loreIB
import kr.sul.servercore.util.ItemBuilder.nameIB
import kr.sul.servercore.util.UniqueIdAPI
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.concurrent.Executors


object DebuggingCommand : CommandExecutor {
    var location : Location? = null
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
                sendM(sender, "LeftAmmoAmount: ${weaponInfo.leftSideAmmoAmt}")
                sendM(sender, "RightAmmoAmount: ${weaponInfo.rightSideAmmoAmt}")
                sendM(sender, "ReloadAmmoAmount: ${weaponInfo.reloadCapacity}")
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

            // csa nbtspeed <테스트 회수>
            "nbtspeed" -> {
                val item = ItemStack(Material.DIAMOND_AXE).nameIB("NbtSpeed 테스트용")
                        .loreIB(listOf("탄알 수 <10>", "§숨§겨§진§문§자§숨§겨§진§문§자§숨§겨§진§문§자"))
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

                Bukkit.broadcastMessage("write test---")
                speedTest("setNBT(NBTAPI)") {
                    for (i in 1..repeat) {
                        val nbtItem = NBTItem(item)
                        nbtItem.setString("tt", "blabla")
                        item.itemMeta = nbtItem.item.itemMeta
                    }
                }
                speedTest("setNBT(NMS)") {
                    for (i in 1..repeat) {
                        val nbti = NbtItem(item)
                        nbti.tag.setString("tt", "blabla")
                        nbti.applyToOriginal()
                    }
                }


                Bukkit.broadcastMessage("test2--")
                speedTest("getNBT(NBTAPI)") {
                    for (i in 1..repeat) {
                        val testNbti = NBTItem(item)
                        val dummy = testNbti.getInteger("int")
                    }
                }
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

            // MetaData vs HashMap  -> 100,000 [avg 120 : 40]
            "metaspeed" -> {
                val repeat = args[1].toInt()
                Bukkit.broadcastMessage("--- $repeat 회 수행 ---")
                // meta
                speedTest("meta-write") {
                    for (i in 1..repeat) {
                        sender.setMetadata("$i", FixedMetadataValue(plugin, i))
                    }
                }
                speedTest("meta-read") {
                    for (i in 1..repeat) {
                        val dummy = sender.getMetadata("$i")[0].asInt()
                    }
                }
                val map = hashMapOf<String, Int>()

                // map
                speedTest("map-write") {
                    for (i in 1..repeat) {
                        map["$i"] = i
                    }
                }
                speedTest("map-read") {
                    for (i in 1..repeat) {
                        val dummy = map["$  i"]
                    }
                }

                // MagazineInInv
                speedTest("Magazine Read") {
                    for (i in 1..repeat) {
                        val dummy = MagazineInInv.getAmmoAmt(sender, "5'56")
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
            }
            "threadspeed" -> {
                val repeat = args[1].toInt()
                Bukkit.broadcastMessage("--- $repeat 회 수행 ---")
                speedTest("일반적이게 thread 생성") {
                    for (i in 1..repeat) {
                        Thread() {
                            println("thread-1")
                        }.start()
                    }
                }
                speedTest("스레드 풀 이용aaa") {
                    val executorService = Executors.newFixedThreadPool(10)
                    for (i in 1..repeat) {
                        executorService.submit { println("thread-2"); Thread.currentThread().join() }
                    }
                }
            }
            "crafting" -> {
                sender.openWorkbench(null, true)
                if (sender.openInventory.topInventory.type == InventoryType.WORKBENCH) {
                    sender.openInventory.topInventory.setItem(2, ItemStack(Material.DIAMOND))
                    sender.openInventory.topInventory.setItem(5, ItemStack(Material.DIAMOND))
                    sender.openInventory.topInventory.setItem(8, ItemStack(Material.STICK))
                }
            }

            // 3배 차이
            "wrapcost" -> {
                val repeat = args[1].toInt()
                val gun = sender.inventory.itemInMainHand
                speedTest("use wrapper class") {
                    for (i in 1..repeat) {
                        val uniqueId = WeaponInfoExtractor(sender, gun).configName
                    }
                }
                speedTest("not use wrapper class") {
                    for (i in 1..repeat) {
                        val uniqueId = UniqueIdAPI.getUniqueID(gun)
                    }
                }
            }
        }
        return true
    }

    private fun sendM(p: Player, msg: String) {
        p.sendMessage(msg)
    }

    private fun speedTest(taskName: String, runnable: Runnable) {
        val time = System.currentTimeMillis()
        val nanoTime = System.nanoTime()
        runnable.run()
        var modifiedTaskName = taskName
        if (taskName.length < 15) {
            for (i in taskName.length until 15) {
                modifiedTaskName += " "
            }
        }
        Bukkit.broadcastMessage("§7$modifiedTaskName §f: ${System.currentTimeMillis() - time}ms [${(System.currentTimeMillis() - time) / 1000}s]  §e${(System.nanoTime() - nanoTime)/1000}us")  // us : 마이크로초
    }
}