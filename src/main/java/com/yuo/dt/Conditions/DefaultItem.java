package com.yuo.dt.Conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.yuo.dt.DTLoots;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fml.ModList;

public class DefaultItem extends LootFunction {

    private final Item item;
    private final Item oldItem;
    private final boolean success;

    protected DefaultItem(ILootCondition[] conditionsIn, Item itemIn, Item old, boolean success) {
        super(conditionsIn);
        this.item = itemIn;
        this.oldItem = old;
        this.success = success;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return DTLoots.ITEM_OR_DEFAULT;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        ItemStack newStack = new ItemStack(item, stack.getCount());

        newStack.setTag(stack.getTag());

        return newStack;
    }

    public static DefaultItem.Builder builder() {
        return new DefaultItem.Builder();
    }

    public static class Builder extends LootFunction.Builder<DefaultItem.Builder> {
        private String idtocheck;
        private Item item;
        private Item oldItem;

        protected DefaultItem.Builder doCast() {
            return this;
        }

        public DefaultItem.Builder apply(String modid, Item item, Item old) {
            this.idtocheck = modid;
            this.item = item;
            this.oldItem = old;
            return this;
        }

        public ILootFunction build() {
            return new DefaultItem(this.getConditions(), item, oldItem, ModList.get().isLoaded(idtocheck));
        }
    }

    public static class Serializer extends LootFunction.Serializer<DefaultItem> {

        @Override
        public void serialize(JsonObject object, DefaultItem function, JsonSerializationContext serializationContext) {
            if (function.success)
                object.addProperty("item", function.item.getRegistryName().toString());
            else
                object.addProperty("default", function.item.getRegistryName().toString());
            object.addProperty("default", function.oldItem.getRegistryName().toString());
        }

        @Override
        public DefaultItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            Item item;
            boolean success;

            try {
                item = JSONUtils.getItem(object, "item");
                success = true;
            } catch (JsonSyntaxException e) {
                item = JSONUtils.getItem(object, "default");
                success = false;
            }

            return new DefaultItem(conditionsIn, item, JSONUtils.getItem(object, "default"), success);
        }
    }
}
