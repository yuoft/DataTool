package com.yuo.dt;

import com.yuo.dt.Conditions.DefaultItem;
import com.yuo.dt.Conditions.ModIsLoad;
import com.yuo.dt.Functions.EnchantSet;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

//注册 条件和函数
public class DTLoots {
    public static LootFunctionType ENCHANT_SET;
    public static LootFunctionType ITEM_OR_DEFAULT;
    public static LootConditionType MOD_IS_LOAD;

    public static void init() {
        ENCHANT_SET = registerFunction("enchant", new LootFunctionType(new EnchantSet.Serializer()));
        ITEM_OR_DEFAULT = registerFunction("item_or_default", new LootFunctionType(new DefaultItem.Serializer()));

        MOD_IS_LOAD = registerCondition("mod_exists", new LootConditionType(new ModIsLoad.Serializer()));
    }

    private static LootFunctionType registerFunction(String name, LootFunctionType function) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(DTMod.MOD_ID, name), function); //ILootFunction registry
    }

    private static LootConditionType registerCondition(String name, LootConditionType condition) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(DTMod.MOD_ID, name), condition); //ILootCondition registry
    }

}
