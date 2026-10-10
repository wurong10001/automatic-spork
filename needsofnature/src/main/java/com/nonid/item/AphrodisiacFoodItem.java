package com.nonid.item;

import com.nonid.effect.NonStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public final class AphrodisiacFoodItem extends Item {
    public static final String NUTRITION_KEY = "AphrodisiacFoodNutrition";
    public static final String SATURATION_KEY = "AphrodisiacFoodSaturation";
    private static final int DEFAULT_NUTRITION = 6;
    private static final float DEFAULT_SATURATION = 0.8f;
    private static final FoodComponent FOOD_COMPONENT = new FoodComponent.Builder()
        .hunger(0)
        .saturationModifier(0.0f)
        .alwaysEdible()
        .build();

    public AphrodisiacFoodItem() {
        super(new Item.Settings().food(FOOD_COMPONENT));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        NbtCompound nbt = stack.getNbt();
        int nutrition = nbt != null && nbt.contains(NUTRITION_KEY)
            ? nbt.getInt(NUTRITION_KEY)
            : DEFAULT_NUTRITION;
        float saturation = nbt != null && nbt.contains(SATURATION_KEY)
            ? nbt.getFloat(SATURATION_KEY)
            : DEFAULT_SATURATION;
        ItemStack result = super.finishUsing(stack, world, user);
        if (!world.isClient) {
            if (user instanceof PlayerEntity player) {
                player.getHungerManager().add(nutrition, saturation);
            }
            user.addStatusEffect(new StatusEffectInstance(NonStatusEffects.ENERGIZED, 2400, 1, false, false, true));
        }
        return result;
    }
}
