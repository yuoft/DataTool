package com.yuo.dt;

import com.yuo.dt.Advancement.DTAdvancements;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("dt")
@Mod.EventBusSubscriber(modid = DTMod.MOD_ID, bus = Bus.MOD)
public class DTMod {
    public static final String MOD_ID = "dt";

    public DTMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> evt) {
        DTLoots.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        DTAdvancements.init();
    }
}
