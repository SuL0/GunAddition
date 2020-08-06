package me.sul.crackshotaddition.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class WeaponSwapEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack weaponItem;
    private String weaponTitle;
    private int swapDelay;

    public WeaponSwapEvent(Player player, ItemStack weaponItem, String weaponTitle, Integer swapDelay) {
        this.player = player;
        this.weaponItem = weaponItem;
        this.weaponTitle = weaponTitle;
        this.swapDelay = swapDelay;
    }

    public Player getPlayer() {
        return player;
    }
    public ItemStack getWeaponItem() { return weaponItem; }
    public String getWeaponTitle() { return weaponTitle; }
    public int getSwapDelay() { return swapDelay; }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
