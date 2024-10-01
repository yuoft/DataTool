package com.yuo.dt.Conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.yuo.dt.DTLoots;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.fml.ModList;

public class ModIsLoad implements LootItemCondition {

    private final boolean exists;
    private final String modID;

    public ModIsLoad(String modID) {
        this.exists = ModList.get().isLoaded(modID); //判断模组是否加载
        this.modID = modID;
    }

    @Override
    public LootItemConditionType getType() {
        return DTLoots.MOD_IS_LOAD;
    }

    @Override
    public boolean test(LootContext context) {
        return exists;
    }

    public static LootItemCondition.Builder builder(String modid) {
        return () -> new ModIsLoad(modid);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ModIsLoad> {

        @Override
        public void serialize(JsonObject json, ModIsLoad value, JsonSerializationContext context) {
            json.addProperty("mod_id", value.modID);
        }

        @Override
        public ModIsLoad deserialize(JsonObject json, JsonDeserializationContext context) {
            return new ModIsLoad(GsonHelper.getAsString(json, "mod_id"));
        }
    }
}
