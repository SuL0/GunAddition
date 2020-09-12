package me.sul.crackshotaddition.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
public class WeaponSwapCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack weaponItem;
    private final String weaponTitle;

    public WeaponSwapCompleteEvent(Player player, ItemStack weaponItem, String weaponTitle) {
        this.player = player;
        this.weaponItem = weaponItem;
        this.weaponTitle = weaponTitle;
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
