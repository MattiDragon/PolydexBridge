package dev.mattidragon.polydexbridge;

import com.google.common.collect.Iterables;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.mattidragon.polydexbridge.data.BridgeRecipe;
import dev.mattidragon.polydexbridge.data.BridgeStack;
import dev.mattidragon.polydexbridge.data.Slot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BridgedEmiRecipe implements EmiRecipe {
    private static final Style TEXTURE_STYLE = Style.EMPTY.withFont(Identifier.of("polydex:gui")).withColor(Formatting.WHITE);
    private final EmiRecipeCategory category;
    private final BridgeRecipe recipe;

    public BridgedEmiRecipe(EmiRecipeCategory category, BridgeRecipe recipe) {
        this.category = category;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return recipe.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return recipe.inputs()
                .stream()
                .map(Slot::stacks)
                .map(BridgedEmiRecipe::convertIngredient)
                .toList();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return recipe.outputs()
                .stream()
                .map(Slot::stacks)
                .map(BridgedEmiRecipe::convertIngredient)
                .map(EmiIngredient::getEmiStacks)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public int getDisplayWidth() {
        return 18 * 9;
    }

    @Override
    public int getDisplayHeight() {
        return 18 * 5;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (Slot slot : Iterables.concat(recipe.inputs(), recipe.outputs(), recipe.icons())) {
            widgets.addTexture(EmiTexture.SLOT, slot.x() * 18, slot.y() * 18);
        }

        if (recipe.texture() != null) {
            widgets.addText(createTextureText(),
                    0,
                    -11,
                    0xffffffff,
                    false);
        }
        
        for (Slot input : recipe.inputs()) {
            widgets.add(createSlotWidget(input));
        }
        for (Slot output : recipe.outputs()) {
            widgets.add(createSlotWidget(output)).recipeContext(this);
        }
        for (Slot icon : recipe.icons()) {
            widgets.add(createIconWidget(icon));
        }
    }

    private MutableText createTextureText() {
        // We add the recipe texture as a child so that it can override the font
        // This is needed because other mods adding polydex integration use their own fonts
        return Text.literal("").setStyle(TEXTURE_STYLE).append(recipe.texture());
    }

    private static SlotWidget createSlotWidget(Slot input) {
        return new SlotWidget(convertIngredient(input.stacks()), input.x() * 18, input.y() * 18) {
            @Override
            public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
                var matrices = draw.getMatrices();
                matrices.push();
                // Adjust the z index to render items on top like a chest would
                matrices.translate(0, 0, 300);
                // Scale along z axis to prevent counts from going over tooltips
                // We only do this for the position in order to preserve lighting
                matrices.peek().getPositionMatrix().scale(1, 1, 0.1f);
                super.render(draw, mouseX, mouseY, delta);
                matrices.pop();
            }
        }.drawBack(false);
    }

    private static Widget createIconWidget(Slot slot) {
        var bounds = new Bounds(slot.x() * 18, slot.y() * 18, 18, 18);
        return new Widget() {
            @Override
            public Bounds getBounds() {
                return bounds;
            }

            @Override
            public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
                var stacks = slot.stacks();
                var item = (int) (System.currentTimeMillis() / 1000 % stacks.size());
                var current = stacks.get(item);

                var matrices = draw.getMatrices();
                matrices.push();
                // Adjust the z index to render items on top like a chest would
                matrices.translate(0, 0, 300);
                // Scale along z axis to prevent counts from going over tooltips
                // We only do this for the position in order to preserve lighting
                matrices.peek().getPositionMatrix().scale(1, 1, 0.1f);
                EmiStack.of(current.stack()).render(draw, bounds.x() + 1, bounds.y() + 1, delta);
                matrices.pop();
            }

            @Override
            public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
                var stacks = slot.stacks();
                var item = (int) (System.currentTimeMillis() / 1000 % stacks.size());
                var current = stacks.get(item);
                return getTooltipComponentListFromItem(current.stack());
            }

            public List<TooltipComponent> getTooltipComponentListFromItem(ItemStack stack) {
                var list = Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack)
                        .stream()
                        .map(EmiPort::ordered)
                        .map(TooltipComponent::of)
                        .collect(Collectors.toCollection(ArrayList::new));
                var data = stack.getTooltipData();
                if (data.isPresent()) {
                    try {
                        list.add(TooltipComponent.of(data.get()));
                    } catch (Throwable e) {
                        PolydexBridge.LOGGER.error("Error while getting tooltip data", e);
                    }
                }
                return list;
            }
        };
    }
    
    private static EmiIngredient convertIngredient(List<BridgeStack> stacks) {
        return EmiIngredient.of(stacks.stream().map(stack -> EmiStack.of(stack.stack()).setChance(stack.chance())).toList());
    }
}
