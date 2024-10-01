package com.yuo.dt.Advancement;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.yuo.dt.Advancement.HasAdvancementTrigger.Instance;
import com.yuo.dt.DTMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TF
 */
public class HasAdvancementTrigger implements CriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation(DTMod.MOD_ID, "has_advancement");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        HasAdvancementTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);
        listeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        HasAdvancementTrigger.Listeners listeners = this.listeners.get(playerAdvancements);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements playerAdvancements) {
        this.listeners.remove(playerAdvancements);
    }

    @Override
    public Instance createInstance(JsonObject jsonObject, DeserializationContext deserializationContext) {
        EntityPredicate.Composite player = EntityPredicate.Composite.fromJson(jsonObject, "player", deserializationContext);
        ResourceLocation advancementId = new ResourceLocation(GsonHelper.getAsString(jsonObject, "advancement"));
        return new HasAdvancementTrigger.Instance(player, advancementId);
    }

    public void trigger(ServerPlayer player, Advancement advancement) {
        Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(advancement);
        }
    }

    static class Instance extends AbstractCriterionTriggerInstance {

        private final ResourceLocation advancementLocation;

        Instance(Composite player, ResourceLocation advancementLocation) {
            super(HasAdvancementTrigger.ID, player);
            this.advancementLocation = advancementLocation;
        }

        boolean test(Advancement advancement) {
            return advancementLocation.equals(advancement.getId());
        }
    }

    private static class Listeners {

        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        Listeners(PlayerAdvancements playerAdvancements) {
            this.playerAdvancements = playerAdvancements;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(CriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(CriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(Advancement advancement) {
            List<Listener<Instance>> list = new ArrayList<>();

            for (CriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(advancement)) {
                    list.add(listener);
                }
            }

            for (CriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener : list) {
                listener.run(this.playerAdvancements);
            }
        }
    }
}
