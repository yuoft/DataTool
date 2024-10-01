package com.yuo.dt;

import com.yuo.dt.Conditions.ModIsLoad;
import com.yuo.dt.Functions.DefaultItem;
import com.yuo.dt.Functions.EnchantSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

//注册 条件和函数
public class DTLoots {
    public static LootItemFunctionType ENCHANT_SET;
    public static LootItemFunctionType ITEM_OR_DEFAULT;
    public static LootItemConditionType MOD_IS_LOAD;

    public static void init() {
        ENCHANT_SET = registerFunction("enchant", new LootItemFunctionType(new EnchantSet.Serializer()));
        ITEM_OR_DEFAULT = registerFunction("item_or_default", new LootItemFunctionType(new DefaultItem.Serializer()));

        MOD_IS_LOAD = registerCondition("mod_exists", new LootItemConditionType(new ModIsLoad.Serializer()));
    }

    private static LootItemFunctionType registerFunction(String name, LootItemFunctionType function) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(DTMod.MOD_ID, name), function); //ILootFunction registry
    }

    private static LootItemConditionType registerCondition(String name, LootItemConditionType condition) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(DTMod.MOD_ID, name), condition); //ILootCondition registry
    }

}
