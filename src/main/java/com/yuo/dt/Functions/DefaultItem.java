package com.yuo.dt.Functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.yuo.dt.DTLoots;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.fml.ModList;

public class DefaultItem extends LootItemConditionalFunction {

    private final Item item;
    private final Item oldItem;
    private final boolean success;

    protected DefaultItem(LootItemCondition[] conditionsIn, Item itemIn, Item old, boolean success) {
        super(conditionsIn);
        this.item = itemIn;
        this.oldItem = old;
        this.success = success;
    }

    @Override
    public LootItemFunctionType getType() {
        return DTLoots.ITEM_OR_DEFAULT;
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        ItemStack newStack = new ItemStack(item, stack.getCount());

        newStack.setTag(stack.getTag());

        return newStack;
    }

    public static DefaultItem.Builder builder() {
        return new DefaultItem.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<DefaultItem.Builder> {
        private String idtocheck;
        private Item item;
        private Item oldItem;

        protected DefaultItem.Builder getThis() {
            return this;
        }

        public DefaultItem.Builder apply(String modid, Item item, Item old) {
            this.idtocheck = modid;
            this.item = item;
            this.oldItem = old;
            return this;
        }

        public LootItemFunction build() {
            return new DefaultItem(this.getConditions(), item, oldItem, ModList.get().isLoaded(idtocheck));
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<DefaultItem> {

        @Override
        public void serialize(JsonObject object, DefaultItem function, JsonSerializationContext serializationContext) {
            if (function.success)
                object.addProperty("item", function.item.getRegistryName().toString());
            else
                object.addProperty("default", function.item.getRegistryName().toString());
            object.addProperty("default", function.oldItem.getRegistryName().toString());
        }

        @Override
        public DefaultItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            Item item;
            boolean success;

            try {
                item = GsonHelper.getAsItem(object, "item");
                success = true;
            } catch (JsonSyntaxException e) {
                item = GsonHelper.getAsItem(object, "default");
                success = false;
            }

            return new DefaultItem(conditionsIn, item, GsonHelper.getAsItem(object, "default"), success);
        }
    }
}
