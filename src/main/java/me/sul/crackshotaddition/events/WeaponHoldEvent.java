package me.sul.crackshotaddition.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class WeaponHoldEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack weaponItem;
    private String weaponTitle;
    private int swapDelay;

    public WeaponHoldEvent(Player player, ItemStack weaponItem, String weaponTitle, Integer swapDelay) {
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

    public boolean isWeaponSwap() { return (swapDelay > 0); }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
