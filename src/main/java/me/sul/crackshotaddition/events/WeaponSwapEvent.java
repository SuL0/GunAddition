package me.sul.crackshotaddition.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
public class WeaponSwapEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack weaponItem;
    private final String weaponTitle;
    private final int swapDelay;

    public WeaponSwapEvent(Player player, ItemStack weaponItem, String weaponTitle, Integer swapDelay) {
        this.player = player;
        this.weaponItem = weaponItem;
        this.weaponTitle = weaponTitle;
        this.swapDelay = swapDelay;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
