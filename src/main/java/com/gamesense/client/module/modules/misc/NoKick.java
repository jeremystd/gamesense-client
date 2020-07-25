package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

/**
 *   @see //com.gamesense.api.mixin.mixins.MixinNetworkManager :^)
 * PacketKick
 *
 */

public class NoKick extends Module {
    public NoKick() {super("NoKick", Category.Misc);}

    public Setting.b noPacketKick;
    Setting.b noSlimeCrash;
    Setting.b noOffhandCrash;

    public void setup(){
        noPacketKick = this.registerB("Packet", true);
        noSlimeCrash = this.registerB("Slime", false);
        noOffhandCrash = this.registerB("Offhand", false);
    }

    //slime
    public void onUpdate() {
        if (mc.world != null && noSlimeCrash.getValue()) {
            mc.world.loadedEntityList
                    .forEach(entity -> {
                        if (entity instanceof EntitySlime) {
                            EntitySlime slime = (EntitySlime) entity;
                            if (slime.getSlimeSize() > 4) {
                                mc.world.removeEntity(entity);
                            }
                        }
                    });
        }
    }

    //Offhand
    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (noOffhandCrash.getValue()) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                if (((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                    event.cancel();
                }
            }
        }
    });

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}