package com.yuo.dt.Conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.yuo.dt.DTLoots;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fml.ModList;

public class ModIsLoad implements ILootCondition {

    private final boolean exists;
    private final String modID;

    public ModIsLoad(String modID) {
        this.exists = ModList.get().isLoaded(modID); //判断模组是否加载
        this.modID = modID;
    }

    @Override
    public LootConditionType getConditionType() {
        return DTLoots.MOD_IS_LOAD;
    }

    @Override
    public boolean test(LootContext context) {
        return exists;
    }

    public static ILootCondition.IBuilder builder(String modid) {
        return () -> new ModIsLoad(modid);
    }

    public static class Serializer implements ILootSerializer<ModIsLoad> {

        @Override
        public void serialize(JsonObject json, ModIsLoad value, JsonSerializationContext context) {
            json.addProperty("mod_id", value.modID);
        }

        @Override
        public ModIsLoad deserialize(JsonObject json, JsonDeserializationContext context) {
            return new ModIsLoad(JSONUtils.getString(json, "mod_id"));
        }
    }
}
