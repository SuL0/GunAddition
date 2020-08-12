package me.sul.crackshotaddition.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class WeaponSwapCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack weaponItem;
    private String weaponTitle;
    private int swapDelay;

    public WeaponSwapCompleteEvent(Player player, ItemStack weaponItem, String weaponTitle) {
        this.player = player;
        this.weaponItem = weaponItem;
        this.weaponTitle = weaponTitle;
    }

    public Player getPlayer() {
        return player;
    }
    public ItemStack getWeaponItem() { return weaponItem; }
    public String getWeaponTitle() { return weaponTitle; }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
