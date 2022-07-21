package me.tvhee.tvheeapi.spigot.api.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public final class CraftingRecipe
{
	private static final List<CraftingRecipe> registeredRecipes = new ArrayList<>();

	public final TvheeAPI api = TvheeAPI.getInstance();
	private final String name;
	private final ItemStack result;
	private final HashMap<Character, ItemStack> ingredients = new HashMap<>();
	private NamespacedKey currentRecipeKey;

	public CraftingRecipe(String name, ItemStack result)
	{
		this.name = name;
		this.result = result;
	}

	public CraftingRecipe removeIngredient(CraftingPlace place)
	{
		ingredients.remove(place.getKey());
		return this;
	}

	public CraftingRecipe setIngredient(CraftingPlace place, ItemStack ingredient)
	{
		if(ingredient == null)
			throw new IllegalArgumentException("Please use removeIngredient(CraftingPlace) to remove any ingredient!");

		ingredients.put(place.getKey(), ingredient);
		return this;
	}

	public CraftingRecipe setIngredients(Map<CraftingPlace, ItemStack> ingredients)
	{
		for(Entry<CraftingPlace, ItemStack> entry : ingredients.entrySet())
			this.ingredients.put(entry.getKey().getKey(), entry.getValue());

		return this;
	}

	public void register()
	{
		if(currentRecipeKey != null)
			throw new IllegalArgumentException("Recipe already registered!");

		if(ingredients.isEmpty())
			throw new IllegalArgumentException("You must set ingredients before registering!");

		this.currentRecipeKey = new NamespacedKey((Plugin) TvheeAPIPlugin.getPlugin(), name);
		ShapedRecipe recipe = new ShapedRecipe(currentRecipeKey, result);

		recipe.shape("ABC", "DEF", "GHI");

		if(ingredients.containsKey('A'))
			recipe.setIngredient('A', new ExactChoice(ingredients.get('A')));

		if(ingredients.containsKey('B'))
			recipe.setIngredient('B', new ExactChoice(ingredients.get('B')));

		if(ingredients.containsKey('C'))
			recipe.setIngredient('C', new ExactChoice(ingredients.get('C')));

		if(ingredients.containsKey('D'))
			recipe.setIngredient('D', new ExactChoice(ingredients.get('D')));

		if(ingredients.containsKey('E'))
			recipe.setIngredient('E', new ExactChoice(ingredients.get('E')));

		if(ingredients.containsKey('F'))
			recipe.setIngredient('F', new ExactChoice(ingredients.get('F')));

		if(ingredients.containsKey('G'))
			recipe.setIngredient('G', new ExactChoice(ingredients.get('G')));

		if(ingredients.containsKey('H'))
			recipe.setIngredient('H', new ExactChoice(ingredients.get('H')));

		if(ingredients.containsKey('I'))
			recipe.setIngredient('I', new ExactChoice(ingredients.get('I')));

		Bukkit.addRecipe(recipe);
		registeredRecipes.add(this);
	}

	public void unregister()
	{
		if(currentRecipeKey == null)
			throw new IllegalArgumentException("Recipe is never registered!");

		Bukkit.removeRecipe(currentRecipeKey);
	}

	public static void unregisterAll()
	{
		for(CraftingRecipe recipe : registeredRecipes)
			recipe.unregister();
	}
}
