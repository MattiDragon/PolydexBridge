package dev.mattidragon.polydexbridge;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class BridgeEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        var recipes = PolydexBridgeClient.RECIPES;
        if (recipes.isEmpty()) return;
        
        var categories = new HashMap<Identifier, EmiRecipeCategory>();
        for (var recipe : recipes) {
            for (var category : recipe.categories()) {
                categories.computeIfAbsent(category.id(), id -> new EmiRecipeCategory(id, EmiStack.of(recipe.typeIcon())) {
                    @Override
                    public Text getName() {
                        return category.name();
                    }
                });
            }
        }
        categories.values().forEach(registry::addCategory);

        for (var recipe : recipes) {
            for (var category : recipe.categories()) {
                registry.addRecipe(new BridgedEmiRecipe(categories.get(category.id()), recipe));
            }
        }
    }
}
