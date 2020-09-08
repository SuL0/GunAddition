package me.sul.crackshotaddition;

import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.crackshotaddition.weaponappearance_etc.WeaponProjectileTrail;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebuggingCommand implements CommandExecutor {
    public static boolean distortion = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (args==null || args.length==0) return false;

        if (args[0].equalsIgnoreCase("chest")) {
            Block block = p.getTargetBlock(null, 20);
            if (block.getType() == Material.CHEST) {
                Chest chest = ((Chest)block.getState());
                chest.setCustomName("준비중");
                chest.update(); // 블럭에 조작을 가해서 NBT Tag가 없는 경우는 update가 무시될 수도 있음.
                p.openInventory(chest.getBlockInventory());
                Bukkit.getScheduler().runTaskLater(CrackShotAddition.getInstance(), () -> {
                    chest.setCustomName("완료");
                    chest.update();
                    p.openInventory(chest.getBlockInventory());
                }, 20L);
            }
        } else if (args[0].equalsIgnoreCase("cool")) {
            p.setCooldown(Material.DIAMOND_PICKAXE, 20);
        } else if (args[0].equalsIgnoreCase("info")) {
            sendM(p, "");
            sendM(p, "isSet: " + MainCrackShotWeaponInfoMetaManager.isSet(p));
            sendM(p, "getItemStack: " + MainCrackShotWeaponInfoMetaManager.getItemStack(p).getItemMeta().getDisplayName());
            sendM(p, "getParentNode: " + MainCrackShotWeaponInfoMetaManager.getParentNode(p));
            sendM(p, "getConfigName: " + MainCrackShotWeaponInfoMetaManager.getConfigName(p));
            sendM(p, "getLeftAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getLeftAmmoAmount(p));
            sendM(p, "getRightAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getRightAmmoAmount(p));
            sendM(p, "getReloadAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getReloadAmmoAmount(p));
            sendM(p, "getAmmoItemMaterial: " + MainCrackShotWeaponInfoMetaManager.getAmmoItemMaterial(p));
            sendM(p, "getReloadAmountPerAmmo: " + MainCrackShotWeaponInfoMetaManager.getReloadAmountPerAmmo(p));
            sendM(p, "getPossessedExtraAmmoAmount: " + MainCrackShotWeaponInfoMetaManager.getPossessedExtraAmmoAmount(p));
            sendM(p, "getUniqueId: " + MainCrackShotWeaponInfoMetaManager.getUniqueId(p));
        } else if (args[0].equalsIgnoreCase("setparticle")) {
            WeaponProjectileTrail.DEFAULT_PARTICLE = Particle.valueOf(args[1]);
        } else if (args[0].equalsIgnoreCase("distortion")) {
            distortion = !distortion;
            sendM(p, "총알 궤적 왜곡 " + distortion);
        } else if (args[0].equalsIgnoreCase("parentnode")) {
            sendM(p, "parentNode: " + CrackShotAdditionAPI.getWeaponParentNode(p.getItemInHand()));
        }
        return true;
    }
    private void sendM(Player p, String msg) {
        p.sendMessage(msg);
    }
    
}
