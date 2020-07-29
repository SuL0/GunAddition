package me.sul.crackshotaddition;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponReloadCompleteEvent;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.util.CrackShotAdditionAPI;
import me.sul.servercore.inventoryevent.InventoryItemChangedEvent;
import me.sul.servercore.inventoryevent.PlayerMainItemChangedConsideringUidEvent;
import me.sul.servercore.serialnumber.UniqueIdAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;


// NOTE: 주의) PlayerMainItemChangedConsideringUidEvent의 이벤트 조건 중 하나인 PlayerItemHeldEvent은 cancellable이라서 EventPriority가 HIGHEST로 해놓아서 '무조건 PlayerItemHeldEvent보다 뒤에 호출됨.'
// NOTE: -> PlayerItemHeldEvent에서는 쓰지 마라 ! ! ! !

// NOTE: 주무기에 대한 모든 정보를 Metadata에 넣어서 관리해주는 클래스
// 데이터의 통일성을 위해 여기서 MainItemSlot을 통해 getItemStack() 메소드를 제공하고 있음. (PlayerItemHeldEvent에서 p.getInventory().getItemInMainHand(){=이전템 나옴} 를 하는 등의 실수를 막기위함.)
public class MainCrackShotWeaponInfoMetaManager implements Listener {
    private static final Plugin plugin = CrackShotAddition.getInstance();
    private static final String MAINITEM_SLOT_META = "csa.mainitem_slot"; // 불변
    private static final String PARENT_NODE_META = "csa.parent_node"; // 불변
    private static final String CONFIG_NAME_META = "csa.config_name"; // 불변
    private static final String CURRENT_AMMO_AMOUNT_META = "csa.current_ammo_amount"; // 가변
    private static final String RELOAD_AMMO_AMOUNT_META = "csa.reload_ammo_amount"; // 불변
    private static final String AMMO_ITEM_MATERIAL_META = "csa.ammo_item_material"; // 불변
    private static final String RELOAD_AMOUNT_PER_AMMO_META = "csa.reload_amount_per_ammo"; // 불번
    private static final String POSSESSED_EXTRA_AMMO_AMOUNT_META = "csa.possessed_extra_ammo_amount"; // 가변       // 진짜 총알 아이템 개수(*ReloadAmountPerAmmo 없음)
    private static final String UNIQUE_ID_META = "csa.unique_id"; // 가변       // 진짜 총알 아이템 개수(*ReloadAmountPerAmmo 없음)


    public static boolean isSet(Player p) {  // 이 클래스 내부에서는 isSet()을 쓰지 않는게 좋음.
        return getItemStack(p) != null;
    }
    public static ItemStack getItemStack(Player p) {  // Metadata들이 올바르지 않는 경우는 PlayerItemHeldEvent에서 메소드를 호출했을 때 말곤 없음. 근데 모든 데이터가 얘를 기준으로 얻어질테니 엄청난 문제는 없을지도.
        int mainItemSlot = p.hasMetadata(MAINITEM_SLOT_META) ? p.getMetadata(MAINITEM_SLOT_META).get(0).asInt() : -1;
        if (mainItemSlot == -1) return null;
        ItemStack mainItem = p.getInventory().getItem(mainItemSlot);
        if (!(UniqueIdAPI.hasUniqueID(mainItem) && UniqueIdAPI.getUniqueID(mainItem).equals(getUniqueId(p)))) { // 메인 아이템이 예상템과 다를 때(UID이용)
            removeAllOfCrackShotMeta(p); // 뭔가 잘못됐을 시 데이터 모두 삭제
            return null;
        }
        return mainItem;
    }
    public static String getParentNode(Player p) {
        return (p.hasMetadata(PARENT_NODE_META)) ? p.getMetadata(PARENT_NODE_META).get(0).asString() : null;
    }
    public static String getConfigName(Player p) {
        return (p.hasMetadata(CONFIG_NAME_META)) ? p.getMetadata(CONFIG_NAME_META).get(0).asString() : null;
    }
    public static int getCurrentAmmoAmount(Player p) {
        return (p.hasMetadata(CURRENT_AMMO_AMOUNT_META)) ? p.getMetadata(CURRENT_AMMO_AMOUNT_META).get(0).asInt() : -1;
    }
    public static int getReloadAmmoAmount(Player p) {
        return (p.hasMetadata(RELOAD_AMMO_AMOUNT_META)) ? p.getMetadata(RELOAD_AMMO_AMOUNT_META).get(0).asInt() : -1;
    }
    public static Material getAmmoItemMaterial(Player p) {
        return (p.hasMetadata(AMMO_ITEM_MATERIAL_META)) ? (Material) p.getMetadata(AMMO_ITEM_MATERIAL_META).get(0).value() : null;
    }
    public static int getReloadAmountPerAmmo(Player p) {
        return (p.hasMetadata(RELOAD_AMOUNT_PER_AMMO_META)) ? p.getMetadata(RELOAD_AMOUNT_PER_AMMO_META).get(0).asInt() : -1;
    }
    public static int getPossessedExtraAmmoAmount(Player p) {
        return (p.hasMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META)) ? p.getMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META).get(0).asInt() : -1;
    }
    public static String getUniqueId(Player p) {
        return (p.hasMetadata(UNIQUE_ID_META)) ? p.getMetadata(UNIQUE_ID_META).get(0).asString() : null;
    }




    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMainItemChanged(PlayerMainItemChangedConsideringUidEvent e) {
        Player p = e.getPlayer();
        ItemStack newIs = e.getNewItemStack();
        if (!e.isChangedToCrackShotWeapon()) {
            if (getParentNode(p) != null) { // 여기서 isSet()은 쓰면 안됨. ItemStack이 다를 수 밖에 없기때문.
                removeAllOfCrackShotMeta(p);
            }
            return;
        }

        int mainItemSlot = e.getNewSlot();
        String parentNode = CrackShotAdditionAPI.getWeaponParentNode(newIs);
        String configName = CrackShotAdditionAPI.getWeaponConfigName(parentNode);
        int currentAmmoAmt = CrackShotAdditionAPI.getWeaponAmmoAmount(p, parentNode, newIs);
        int reloadAmmoAmt = CrackShotAdditionAPI.getWeaponReloadAmount(p, parentNode, newIs);
        Material ammoMaterial = null;
        int reloadAmtPerAmmo = -1;
        int possessedExtraAmmo = -1;
        String uniqueId = UniqueIdAPI.getUniqueID(newIs);

        boolean ammoEnable = CSDirector.getInstance().getBoolean(parentNode + ".Ammo.Enable");
        boolean takeAmmo = CSDirector.getInstance().getBoolean(parentNode + ".Reload.Take_Ammo_On_Reload");
        boolean takeAsMag = CSDirector.getInstance().getBoolean(parentNode + ".Reload.Take_Ammo_As_Magazine");
        if (ammoEnable && takeAmmo) {
            ammoMaterial = Material.getMaterial(Integer.parseInt(CSDirector.getInstance().getString( parentNode + ".Ammo.Ammo_Item_ID").split("~")[0])); // '~' 를 고려해야함
            if (ammoMaterial != null) {
                reloadAmtPerAmmo = takeAsMag ? reloadAmmoAmt : 1;
                possessedExtraAmmo = countPossessedAmmoAmount(p, ammoMaterial, reloadAmtPerAmmo);
            }
        }

        p.setMetadata(MAINITEM_SLOT_META, new FixedMetadataValue(plugin, mainItemSlot));
        p.setMetadata(PARENT_NODE_META, new FixedMetadataValue(plugin, parentNode));
        p.setMetadata(CONFIG_NAME_META, new FixedMetadataValue(plugin, configName));
        p.setMetadata(CURRENT_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, currentAmmoAmt));
        p.setMetadata(RELOAD_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, reloadAmmoAmt));
        if ((ammoMaterial != null)) {
            p.setMetadata(AMMO_ITEM_MATERIAL_META, new FixedMetadataValue(plugin, ammoMaterial));
        } else {
            p.removeMetadata(AMMO_ITEM_MATERIAL_META, plugin);
        }
        p.setMetadata(RELOAD_AMOUNT_PER_AMMO_META, new FixedMetadataValue(plugin, reloadAmtPerAmmo));
        p.setMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, possessedExtraAmmo));
        p.setMetadata(UNIQUE_ID_META, new FixedMetadataValue(plugin, uniqueId));
    }

    private static void removeAllOfCrackShotMeta(Player p) {
        p.removeMetadata(MAINITEM_SLOT_META, plugin);
        p.removeMetadata(PARENT_NODE_META, plugin);
        p.removeMetadata(CONFIG_NAME_META, plugin);
        p.removeMetadata(CURRENT_AMMO_AMOUNT_META, plugin);
        p.removeMetadata(RELOAD_AMMO_AMOUNT_META, plugin);
        p.removeMetadata(AMMO_ITEM_MATERIAL_META, plugin);
        p.removeMetadata(RELOAD_AMOUNT_PER_AMMO_META, plugin);
        p.removeMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, plugin);
        p.removeMetadata(UNIQUE_ID_META, plugin);
    }

    protected static int countPossessedAmmoAmount(Player p, Material ammoMaterial, int multiplication) {
        int amount = 0;
        for (ItemStack loopIs : p.getInventory().getContents()) {
            if (loopIs==null) continue;
            if (loopIs.getType() == ammoMaterial) {
                amount += loopIs.getAmount()*multiplication;
            }
        }
        return amount;
    }
    
    


    
    /* 가변적인 META 값 관리 */
    

    // requiredAmmoMaterial이 이벤트 대상이면 POSSESSED_EXTRA_AMMO 값을 업데이트
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemChanged(InventoryItemChangedEvent e) {
        Player p = e.getPlayer();
        Material requiredAmmoMaterial = getAmmoItemMaterial(p);
        int reloadAmtPerAmmo = getReloadAmountPerAmmo(p);
        if (requiredAmmoMaterial != null && e.getItemStack().getType() == requiredAmmoMaterial) {
            int possessedExtraAmmoAmount = countPossessedAmmoAmount(p, requiredAmmoMaterial, reloadAmtPerAmmo);
            p.setMetadata(POSSESSED_EXTRA_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, possessedExtraAmmoAmount));
        }
    }

    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onShoot(WeaponShootEvent e) {
        Player p = e.getPlayer();
        int currentAmmoAmt = getCurrentAmmoAmount(p) - 1;
        p.setMetadata(CURRENT_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, currentAmmoAmt));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onReloadComplete(WeaponReloadCompleteEvent e) {
        Player p = e.getPlayer();
        int reloadAmmoAmt = getReloadAmmoAmount(p);
        p.setMetadata(CURRENT_AMMO_AMOUNT_META, new FixedMetadataValue(plugin, reloadAmmoAmt));
    }





    /* 램 관리 */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (getParentNode(p) != null) {
            removeAllOfCrackShotMeta(p);
        }
    }
}
