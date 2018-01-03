package com.blocktyper.example;

import com.blocktyper.v1_2_6.BlockTyperBasePlugin;
import com.blocktyper.v1_2_6.recipes.IRecipe;

import java.util.Locale;
import java.util.ResourceBundle;


public class ExamplePlugin extends BlockTyperBasePlugin {

	public static final String RECIPES_KEY = "EXAMPLE_PLUGIN_RECIPE_KEY";

	public static final String RESOURCE_NAME = "com.blocktyper.example.resources.ExampleMessages";

	private static ExamplePlugin plugin;

	public ExamplePlugin(){
		if(plugin == null){
			plugin = this;
		}
	}

	public void onEnable() {
		super.onEnable();
		new ExampleCommand(this);
		new ExampleListener(this);
	}

	public static ExamplePlugin getPlugin() {
		return plugin;
	}

	// begin localization
	@Override
	public ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(RESOURCE_NAME, locale);
	}
	// end localization

	@Override
	public String getRecipesNbtKey() {
		return RECIPES_KEY;
	}
	@Override
	public IRecipe bootstrapRecipe(IRecipe recipe) {
		return recipe;
	}
}
