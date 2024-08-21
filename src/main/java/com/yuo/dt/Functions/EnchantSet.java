package com.yuo.dt.Functions;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.yuo.dt.DTLoots;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.HashMap;
import java.util.Map;

public class EnchantSet extends LootFunction {

    private final Map<IRegistryDelegate<Enchantment>, Short> enchantments;

    protected EnchantSet(ILootCondition[] conditions, Map<IRegistryDelegate<Enchantment>, Short> enchantments) {
        super(conditions);
        this.enchantments = enchantments;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return DTLoots.ENCHANT_SET;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        for (Map.Entry<IRegistryDelegate<Enchantment>, Short> e : enchantments.entrySet()) {
            if (stack.getItem() == Items.ENCHANTED_BOOK) { //附魔书
                EnchantedBookItem.addEnchantment(stack, new EnchantmentData(e.getKey().get(), e.getValue()));
            } else {
                stack.addEnchantment(e.getKey().get(), e.getValue());
            }
        }
        return stack;
    }

    public static EnchantSet.Builder builder() {
        return new EnchantSet.Builder();
    }

    public static class Builder extends LootFunction.Builder<EnchantSet.Builder> {
        private final Map<IRegistryDelegate<Enchantment>, Short> enchants = Maps.newHashMap();

        protected EnchantSet.Builder doCast() {
            return this;
        }

        public EnchantSet.Builder apply(Enchantment p_216077_1_, Integer p_216077_2_) {
            this.enchants.put(p_216077_1_.delegate, p_216077_2_.shortValue());
            return this;
        }

        public ILootFunction build() {
            return new EnchantSet(this.getConditions(), this.enchants);
        }
    }

    public static class Serializer extends LootFunction.Serializer<EnchantSet> {

        @Override
        public void serialize(JsonObject object, EnchantSet function, JsonSerializationContext ctx) {
            if (!function.enchantments.isEmpty()) {
                JsonObject obj = new JsonObject();

                for (Map.Entry<IRegistryDelegate<Enchantment>, Short> e : function.enchantments.entrySet()) {
                    obj.addProperty(e.getKey().get().getRegistryName().toString(), e.getValue());
                }

                object.add("enchantments", obj);
            }
        }

        @Override
        public EnchantSet deserialize(JsonObject object, JsonDeserializationContext ctx, ILootCondition[] conditions) {
            Map<IRegistryDelegate<Enchantment>, Short> enchantments = new HashMap<>();

            if (object.has("enchantments")) {
                JsonObject enchantObj = JSONUtils.getJsonObject(object, "enchantments");

                for (Map.Entry<String, JsonElement> e : enchantObj.entrySet()) {
                    ResourceLocation id = new ResourceLocation(e.getKey());
                    if (!ForgeRegistries.ENCHANTMENTS.containsKey(id)) {
                        throw new JsonSyntaxException("Can't find enchantment " + e.getKey());
                    }

                    Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(id);
                    short lvl = e.getValue().getAsShort();

                    for (IRegistryDelegate<Enchantment> other : enchantments.keySet()) {
                        if (ench != null && !ench.isCompatibleWith(other.get())) {
                            throw new JsonParseException(String.format("Enchantments %s and %s conflict", ench.getRegistryName(), other.get().getRegistryName()));
                        }
                    }
                    if (ench == null) ench = Enchantments.EFFICIENCY; //如果附魔为null 则默认为效率
                    enchantments.put(ench.delegate, lvl);
                }
            }

            return new EnchantSet(conditions, enchantments);
        }
    }
}