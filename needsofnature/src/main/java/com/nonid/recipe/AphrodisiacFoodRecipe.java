package com.nonid.recipe;

import com.nonid.item.AphrodisiacFoodItem;
import com.nonid.potion.NonPotions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public final class AphrodisiacFoodRecipe extends SpecialCraftingRecipe {
    private static final String NUTRITION_KEY = AphrodisiacFoodItem.NUTRITION_KEY;
    private static final String SATURATION_KEY = AphrodisiacFoodItem.SATURATION_KEY;

    public AphrodisiacFoodRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory input, World world) {
        int foodCount = 0;
        int potionCount = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isAphrodisiacPotion(stack)) {
                potionCount++;
            } else if (isVanillaFood(stack)) {
                foodCount++;
            } else {
                return false;
            }
        }
        return foodCount == 1 && potionCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory input, DynamicRegistryManager registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStack(i);
            if (isVanillaFood(stack)) {
                return createFoodStack(stack);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registries) {
        return createFoodStack(DEFAULT_FOOD);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory input) {
        DefaultedList<ItemStack> remainders = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStack(i);
            if (isAphrodisiacPotion(stack)) {
                remainders.set(i, new ItemStack(Items.GLASS_BOTTLE));
            } else if (!stack.isEmpty() && stack.getItem().hasRecipeRemainder()) {
                remainders.set(i, new ItemStack(stack.getItem().getRecipeRemainder()));
            }
        }
        return remainders;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NonRecipeSerializers.APHRODISIAC_FOOD_RECIPE;
    }

    private static ItemStack createFoodStack(ItemStack source) {
        ItemStack output = new ItemStack(com.nonid.NeedsOfNature.APHRODISIAC_FOOD);
        net.minecraft.item.FoodComponent food = source.getItem().getFoodComponent();
        output.getOrCreateNbt().putInt(NUTRITION_KEY, food.getHunger());
        output.getOrCreateNbt().putFloat(SATURATION_KEY, food.getSaturationModifier());
        return output;
    }

    private static boolean isAphrodisiacPotion(ItemStack stack) {
        if (stack.isEmpty() || !stack.isOf(Items.POTION)) {
            return false;
        }
        var potion = PotionUtil.getPotion(stack);
        return potion == NonPotions.APHRODISIAC || potion == NonPotions.APHRODISIAC_III;
    }

    private static boolean isVanillaFood(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getFoodComponent() == null) {
            return false;
        }
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        return itemId != null && "minecraft".equals(itemId.getNamespace());
    }

    private static final ItemStack DEFAULT_FOOD = new ItemStack(Items.APPLE);
}
