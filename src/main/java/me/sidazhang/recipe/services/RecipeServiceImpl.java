package me.sidazhang.recipe.services;

import lombok.extern.slf4j.Slf4j;
import me.sidazhang.recipe.commands.RecipeCommand;
import me.sidazhang.recipe.converters.Recipe2RecipeCommand;
import me.sidazhang.recipe.converters.RecipeCommand2Recipe;
import me.sidazhang.recipe.exceptions.NotFoundException;
import me.sidazhang.recipe.models.Recipe;
import me.sidazhang.recipe.repositories.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeCommand2Recipe recipeCommand2Recipe;
    private final Recipe2RecipeCommand recipe2RecipeCommand;

    public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeCommand2Recipe recipeCommand2Recipe, Recipe2RecipeCommand recipe2RecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipe2RecipeCommand = recipe2RecipeCommand;
        this.recipeCommand2Recipe = recipeCommand2Recipe;
    }

    @Override
    public Set<Recipe> getRecipes() {
        Set<Recipe> recipes = new HashSet<>();
        recipeRepository.findAll().iterator().forEachRemaining(recipes::add);
        return recipes;
    }

    @Override
    @Transactional
    public Recipe findById(String id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (!recipeOptional.isPresent()) {
            throw new NotFoundException("Recipe with ID value " + id);
        }
        return recipeOptional.get();
    }

    @Override
    @Transactional
    public RecipeCommand saveRecipeCommand(RecipeCommand recipeCommand) {

        Recipe detachedRecipe = recipeCommand2Recipe.convert(recipeCommand);

        Recipe savedRecipe = recipeRepository.save(detachedRecipe);

        return recipe2RecipeCommand.convert(savedRecipe);
    }

    @Override
    public RecipeCommand findCommandById(String id) {
        return recipe2RecipeCommand.convert(findById(id));

    }

    @Override
    public RecipeCommand createRecipe() {
        RecipeCommand recipeCommand = new RecipeCommand();
        return saveRecipeCommand(recipeCommand);
    }

    @Override
    public void deleteById(String id) {
        recipeRepository.deleteById(id);
    }
}
