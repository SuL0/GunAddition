package me.sul.crackshotaddition.weaponappearance_etc;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Collections;

public class WeaponMuzzleFlash implements Listener {
    private final Particle MUZZLE_FLASH_PARTICLE = Particle.REDSTONE;
    private final int RIGHT = 90;
    private final int LEFT = -90;


    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        boolean b_muzzleFlash = CSDirector.getInstance().getBoolean(e.getWeaponTitle() + ".Addition.Muzzle_Flash.Enable");
        if (b_muzzleFlash) {
            double multiplyToRightSideVec = CSDirector.getInstance().getDouble(e.getWeaponTitle() + ".Addition.Muzzle_Flash.MultiplyToRightSideVec");
            double sumToY = CSDirector.getInstance().getDouble(e.getWeaponTitle() + ".Addition.Muzzle_Flash.SumToY");
            double multiplyToForwardSideVec = CSDirector.getInstance().getDouble(e.getWeaponTitle() + ".Addition.Muzzle_Flash.MultiplyToForwardSideVec");

            spawnMuzzleFlashParticle(e.getPlayer(), RIGHT, multiplyToRightSideVec, sumToY, multiplyToForwardSideVec);
        }
    }

    private void spawnMuzzleFlashParticle(Player p, int rightOrLeft, double multiplyToRightSideVec, double sumToY, double multiplyToForwardSideVec) {
        double playerYaw = (p.getLocation().getYaw() + 90.0F + rightOrLeft) * Math.PI / 180.0D;

        Vector toRightSideVec = new Vector(Math.cos(playerYaw)*multiplyToRightSideVec, sumToY, Math.sin(playerYaw)*multiplyToRightSideVec);
        Vector toForwardSideVec = p.getLocation().getDirection().normalize().multiply(multiplyToForwardSideVec);

        // 벡터 보정
        double value;
        if (toForwardSideVec.getY() > 0) {
            value = 0.185*Math.pow(toForwardSideVec.getY()/toForwardSideVec.length(), 3D);
            toForwardSideVec.add(toForwardSideVec.clone().multiply(new Vector(0, -1, 0)).normalize().multiply(value));
        } else {
            value = 0.14*Math.pow(Math.abs(toForwardSideVec.getY())/toForwardSideVec.length(), 2D);
            toForwardSideVec.add(toForwardSideVec.clone().multiply(new Vector(-1, 0, -1)).normalize().multiply(value));
        }

        // 최종 위치
        Location loc = p.getEyeLocation().toVector().add(toRightSideVec).add(toForwardSideVec).toLocation(p.getWorld());
        loc.getWorld().spawnParticle(MUZZLE_FLASH_PARTICLE, Collections.singletonList(p), p, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 0, null);
    }
}
