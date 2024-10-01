package com.yuo.dt.Functions;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.yuo.dt.DTLoots;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.HashMap;
import java.util.Map;

public class EnchantSet extends LootItemConditionalFunction {

    private final Map<IRegistryDelegate<Enchantment>, Short> enchantments;

    protected EnchantSet(LootItemCondition[] conditions, Map<IRegistryDelegate<Enchantment>, Short> enchantments) {
        super(conditions);
        this.enchantments = enchantments;
    }

    @Override
    public LootItemFunctionType getType() {
        return DTLoots.ENCHANT_SET;
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        for (Map.Entry<IRegistryDelegate<Enchantment>, Short> e : enchantments.entrySet()) {
            if (stack.getItem() == Items.ENCHANTED_BOOK) { //附魔书
                EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(e.getKey().get(), e.getValue()));
            } else {
                stack.enchant(e.getKey().get(), e.getValue());
            }
        }
        return stack;
    }

    public static EnchantSet.Builder builder() {
        return new EnchantSet.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<EnchantSet.Builder> {
        private final Map<IRegistryDelegate<Enchantment>, Short> enchants = Maps.newHashMap();

        protected EnchantSet.Builder getThis() {
            return this;
        }

        public EnchantSet.Builder apply(Enchantment p_216077_1_, Integer p_216077_2_) {
            this.enchants.put(p_216077_1_.delegate, p_216077_2_.shortValue());
            return this;
        }

        public LootItemFunction build() {
            return new EnchantSet(this.getConditions(), this.enchants);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantSet> {

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
        public EnchantSet deserialize(JsonObject object, JsonDeserializationContext ctx, LootItemCondition[] conditions) {
            Map<IRegistryDelegate<Enchantment>, Short> enchantments = new HashMap<>();

            if (object.has("enchantments")) {
                JsonObject enchantObj = GsonHelper.getAsJsonObject(object, "enchantments");

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
                    if (ench == null) ench = Enchantments.BLOCK_EFFICIENCY; //如果附魔为null 则默认为效率
                    enchantments.put(ench.delegate, lvl);
                }
            }

            return new EnchantSet(conditions, enchantments);
        }
    }
}