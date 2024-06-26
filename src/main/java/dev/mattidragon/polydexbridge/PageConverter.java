package dev.mattidragon.polydexbridge;

import dev.mattidragon.polydexbridge.data.BridgeStack;
import dev.mattidragon.polydexbridge.data.Slot;
import eu.pb4.polydex.api.v1.recipe.PageBuilder;
import eu.pb4.polydex.api.v1.recipe.PolydexIngredient;
import eu.pb4.polydex.api.v1.recipe.PolydexStack;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PageConverter implements PageBuilder {
    private final ServerPlayerEntity player;
    public final List<Slot> icons = new ArrayList<>();
    public final List<Slot> inputs = new ArrayList<>();
    public final List<Slot> outputs = new ArrayList<>();

    public PageConverter(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void set(int x, int y, ItemStack stack) {
        icons.add(new Slot(x, y, new BridgeStack(stack)));
    }

    @Override
    public void set(int x, int y, ItemStack... stacks) {
        icons.add(new Slot(x, y, Arrays.stream(stacks).map(BridgeStack::new).toList()));
    }

    @Override
    public void setOutput(int x, int y, ItemStack... stacks) {
        outputs.add(new Slot(x, y, Arrays.stream(stacks).map(BridgeStack::new).toList()));
    }

    @Override
    public void setOutput(int x, int y, PolydexStack<?>... stacks) {
        outputs.add(new Slot(x, y, Arrays.stream(stacks).map(stack -> new BridgeStack(stack.toItemStack(player), stack.chance())).toList()));
    }

    @Override
    public void setIngredient(int x, int y, ItemStack... stacks) {
        inputs.add(new Slot(x, y, Arrays.stream(stacks).map(BridgeStack::new).toList()));
    }

    @Override
    public void setIngredient(int x, int y, Ingredient ingredient) {
        inputs.add(new Slot(x, y, Arrays.stream(ingredient.getMatchingStacks()).map(BridgeStack::new).toList()));
    }

    @Override
    public void setIngredient(int x, int y, PolydexIngredient<?> ingredient) {
        inputs.add(new Slot(x, y, convertIngredient(ingredient, player)));
    }

    @Override
    public void setIngredient(int x, int y, PolydexIngredient<?> ingredient, Consumer<GuiElementBuilder> consumer) {
        inputs.add(new Slot(x, y, ingredient.asStacks()
                .stream()
                .map(stack -> {
                    var builder = GuiElementBuilder.from(stack.toItemStack(player));
                    consumer.accept(builder);
                    return new BridgeStack(builder.asStack(), ingredient.chance());
                })
                .toList()));
    }

    @Override
    public void setEmpty(int x, int y) {
        inputs.add(new Slot(x, y, List.of()));
    }

    @Override
    public boolean hasTextures() {
        return true;
    }

    public static List<BridgeStack> convertIngredient(PolydexIngredient<?> ingredient, ServerPlayerEntity player) {
        return ingredient.asStacks().stream().map(stack -> new BridgeStack(stack.toItemStack(player), ingredient.chance())).toList();
    }
}
