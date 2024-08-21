package com.yuo.dt.Advancement;

import com.yuo.dt.DTMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = DTMod.MOD_ID)
public class DTAdvancements {

    public static final HasAdvancementTrigger ADVANCEMENT_UNLOCKED = CriteriaTriggers.register(new HasAdvancementTrigger());

    public static void init(){}

    @SubscribeEvent
    public static void onAdvancementGet(AdvancementEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            DTAdvancements.ADVANCEMENT_UNLOCKED.trigger((ServerPlayerEntity) player, event.getAdvancement());
        }
    }
}
