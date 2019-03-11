package cookplanner.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import cookplanner.domain.Ingredient;
import cookplanner.domain.IngredientName;
import cookplanner.domain.MeasureUnit;
import cookplanner.domain.Recipe;
import cookplanner.domain.RecipeType;
import cookplanner.domain.Tag;
import cookplanner.repository.IngredientNameRepository;
import cookplanner.repository.MeasureUnitRepository;
import cookplanner.repository.RecipeRepository;
import cookplanner.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CookPlannerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

	private final MeasureUnitRepository measureUnitRepository;
	private final IngredientNameRepository ingredientNameRepository;
	private final RecipeRepository recipeRepository;
	private final TagRepository tagRepository;

	public CookPlannerBootstrap(MeasureUnitRepository measureUnitRepository,
			IngredientNameRepository ingredientNameRepository, RecipeRepository recipeRepository,
			TagRepository tagRepository) {
		this.measureUnitRepository = measureUnitRepository;
		this.ingredientNameRepository = ingredientNameRepository;
		this.recipeRepository = recipeRepository;
		this.tagRepository = tagRepository;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		measureUnitRepository.saveAll(getMeasureUnits());
		tagRepository.saveAll(getTags());
		ingredientNameRepository.saveAll(getIngredientNames());		
		recipeRepository.saveAll(getRecipes());
		
	}
	
	private List<MeasureUnit> getMeasureUnits() {
		log.debug("Bootstrapping measureunits");
		List<MeasureUnit> measureUnits = new ArrayList<>();
		measureUnits.add(new MeasureUnit(" ", " "));
		measureUnits.add(new MeasureUnit("gram", "gram"));
		measureUnits.add(new MeasureUnit("kilogram", "kilogram"));
		measureUnits.add(new MeasureUnit("theelepel", "theelepels"));
		measureUnits.add(new MeasureUnit("eetlepel", "eetlepels"));
		measureUnits.add(new MeasureUnit("liter", "liter"));
		measureUnits.add(new MeasureUnit("deciliter", "deciliter"));
		measureUnits.add(new MeasureUnit("krop", "kroppen"));
		measureUnits.add(new MeasureUnit("bakje", "bakjes"));
		measureUnits.add(new MeasureUnit("pakje", "pakjes"));
		measureUnits.add(new MeasureUnit("plakje", "plakjes"));
		measureUnits.add(new MeasureUnit("druppel", "druppels"));
		measureUnits.add(new MeasureUnit("scheutje", "scheutjes"));
		measureUnits.add(new MeasureUnit("teentje", "teentjes"));
		measureUnits.add(new MeasureUnit("naar smaak", "naar smaak"));		
		return measureUnits;
	}
	
	private List<IngredientName> getIngredientNames() {
		List<IngredientName> ingredientNames = new ArrayList<>();
		ingredientNames.add(new IngredientName("water"));
		ingredientNames.add(new IngredientName("bouillionpoeder"));
		ingredientNames.add(new IngredientName("gehakt"));
		ingredientNames.add(new IngredientName("andijvie"));
		ingredientNames.add(new IngredientName("aardappels"));
		ingredientNames.add(new IngredientName("spekkies"));
		return ingredientNames;
	}
	
	private List<Tag> getTags() {
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("Vegetarisch"));
		tags.add(new Tag("GOK"));
		tags.add(new Tag("Makkelijk"));
		tags.add(new Tag("Snel"));
		tags.add(new Tag("Feestelijk"));
		tags.add(new Tag("Pasta"));
		tags.add(new Tag("Rijst"));
		tags.add(new Tag("Aardappel"));
		return tags;
	}

		
	private List<Recipe> getRecipes() {
		
		List<Recipe> recipes = new ArrayList<>();
		
		// Get the measure units optionals
		Optional<MeasureUnit> lepelMuOptional = measureUnitRepository.findByName("eetlepel");
		if(!lepelMuOptional.isPresent()) {
			throw new RuntimeException("Expected MeasureUnit not found!");
		}
		Optional<MeasureUnit> gramMuOptional = measureUnitRepository.findByName("gram");
		if(!gramMuOptional.isPresent()) {
			throw new RuntimeException("Expected MeasureUnit not found!");
		}
		Optional<MeasureUnit> kilogramMuOptional = measureUnitRepository.findByName("kilogram");
		if(!kilogramMuOptional.isPresent()) {
			throw new RuntimeException("Expected MeasureUnit not found!");
		}
		Optional<MeasureUnit> literMuOptional = measureUnitRepository.findByName("liter");
		if(!literMuOptional.isPresent()) {
			throw new RuntimeException("Expected MeasureUnit not found!");
		}
		Optional<MeasureUnit> kropMuOptional = measureUnitRepository.findByName("krop");
		if(!kropMuOptional.isPresent()) {
			throw new RuntimeException("Expected MeasureUnit not found!");
		}
		
		// Get the measure units
		MeasureUnit lepelMu = lepelMuOptional.get();
		MeasureUnit gramMu = gramMuOptional.get();
		MeasureUnit kilogramMu = kilogramMuOptional.get();
		MeasureUnit literMu = literMuOptional.get();
		MeasureUnit kropMu = kropMuOptional.get();
		
		// Get the ingredient name optionals
		Optional<IngredientName> waterIngredientOptional = ingredientNameRepository.findByName("water");
		if (!waterIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		Optional<IngredientName> bouillionpoederIngredientOptional = ingredientNameRepository.findByName("bouillionpoeder");
		if (!bouillionpoederIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		Optional<IngredientName> gehaktIngredientOptional = ingredientNameRepository.findByName("gehakt");
		if (!gehaktIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		Optional<IngredientName> andijvieIngredientOptional = ingredientNameRepository.findByName("andijvie");
		if (!andijvieIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		Optional<IngredientName> aardappelsIngredientOptional = ingredientNameRepository.findByName("aardappels");
		if (!aardappelsIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		Optional<IngredientName> spekkiesIngredientOptional = ingredientNameRepository.findByName("spekkies");
		if (!spekkiesIngredientOptional.isPresent()) {
			throw new RuntimeException("Expected IngredientName not found!");
		}
		
		// Get the ingredient names
		IngredientName waterIngredientName = waterIngredientOptional.get();
		IngredientName bouillionpoederIngredientName = bouillionpoederIngredientOptional.get();
		IngredientName gehaktIngredientName = gehaktIngredientOptional.get();
		IngredientName andijvieIngredientName = andijvieIngredientOptional.get();
		IngredientName aardappelsIngredientName = aardappelsIngredientOptional.get();
		IngredientName spekkiesIngredientName = spekkiesIngredientOptional.get();
		
		
		// Get the tag optionals
		Optional<Tag> vegaTagOptional = tagRepository.findByName("Vegetarisch");
		if(!vegaTagOptional.isPresent()) {
			throw new RuntimeException("Expected Tag not found!");
		}
		Optional<Tag> gokTagOptional = tagRepository.findByName("GOK");
		if(!gokTagOptional.isPresent()) {
			throw new RuntimeException("Expected Tag not found!");
		}
		Optional<Tag> makkelijkTagOptional = tagRepository.findByName("Makkelijk");
		if(!makkelijkTagOptional.isPresent()) {
			throw new RuntimeException("Expected Tag not found!");
		}
		Optional<Tag> snelTagOptional = tagRepository.findByName("Snel");
		if(!snelTagOptional.isPresent()) {
			throw new RuntimeException("Expected Tag not found!");
		}
		Optional<Tag> feestelijkTagOptional = tagRepository.findByName("Feestelijk");
		if(!feestelijkTagOptional.isPresent()) {
			throw new RuntimeException("Expected Tag not found!");
		}
		
		// Get the tags
		Tag vegaTag = vegaTagOptional.get();
		Tag gokTag = gokTagOptional.get();
		Tag makkelijkTag = makkelijkTagOptional.get();
		Tag snelTag = snelTagOptional.get();
		Tag feestelijkTag = feestelijkTagOptional.get();
		
		// create the recipes
		Recipe soepRecept = new Recipe();
		soepRecept.setName("Soep met balletjes");
		soepRecept.setDescription("Overheerlijke soep voor de winterse dagen!");
		soepRecept.setCookTime(20);
		soepRecept.setPreparationTime(2);
		soepRecept.setServings(2);
		soepRecept.setRecipeType(RecipeType.VOORGERECHT);
		soepRecept.setDirections("1 Kook het water en voeg de bouillonpoeder toe"
				+ "\n2 Maak kleine balletjes van het gehakt, kruid naar smaak"
				+ "\n3 Doe de balletjes voorzichtig in de kokende bouillon en laat kwartiertje koken"
				+ "\n");
		soepRecept.setNotes("Met Fred en Wilma gegeten, waren zeer enthousiast (yabadabidou)!");
		soepRecept.addIngredient(new Ingredient(waterIngredientName, 1f, true, literMu));
		soepRecept.addIngredient(new Ingredient(bouillionpoederIngredientName, 1f, true, lepelMu));
		soepRecept.addIngredient(new Ingredient(gehaktIngredientName, 250f, false, gramMu));
		
		soepRecept.getTags().add(makkelijkTag);		
		soepRecept.getTags().add(snelTag);
		soepRecept.getTags().add(gokTag);
		recipes.add(soepRecept);
		
		Recipe andijvieStampotRecept = new Recipe();
		andijvieStampotRecept.setName("Andijvie stampot");
		andijvieStampotRecept.setDescription("Lekere andijvie stampot");
		andijvieStampotRecept.setCookTime(20);
		andijvieStampotRecept.setPreparationTime(10);
		andijvieStampotRecept.setServings(2);
		andijvieStampotRecept.setRecipeType(RecipeType.HOOFDGERECHT);
		andijvieStampotRecept.setDirections("1 Kook de aardappels en maak er stampot van"
				+ "\n2 bak de spekjes krokant"
				+ "\n3 Als de aardappels klaar zijn de andijvie en spekjes erdoor roeren"
				+ "\n");
		andijvieStampotRecept.setNotes("Extra lekker met andijvie uit eigen tuin! Wel eerst de slakken eruit wassen!");
		andijvieStampotRecept.addIngredient(new Ingredient(andijvieIngredientName, 1f, false, kropMu));
		andijvieStampotRecept.addIngredient(new Ingredient(aardappelsIngredientName, 400f, false, kilogramMu));
		andijvieStampotRecept.addIngredient(new Ingredient(spekkiesIngredientName, 100f, false, gramMu));
		andijvieStampotRecept.getTags().add(makkelijkTag);
		andijvieStampotRecept.getTags().add(snelTag);
		recipes.add(andijvieStampotRecept);
		
		return recipes;
	}  
}
