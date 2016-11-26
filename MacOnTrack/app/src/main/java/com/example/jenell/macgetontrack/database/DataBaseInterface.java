package com.example.jenell.macgetontrack.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jenell.macontrack.R;

import java.util.ArrayList;

/**
 * Created by Jenell on 3/8/2015.
 */
public class DataBaseInterface extends SQLiteOpenHelper {
    //Make sure that this class is statically called
    private static DataBaseInterface sInstance;

    public static final String DATABASE_NAME = "MacOnTrack.db";
    private final String[] successKeys = {"breakfastSuccess", "lunchSuccess", "dinnerSuccess"};
    private final String[] recipeKeys = {"breakfast", "lunch", "dinner", "sndr"};
    private final String[] timeKeys = {"breakfastTime", "lunchTime", "dinnerTime"};

    private final String[] rbKeys = {"recipeTitle", "category", "ingredients", "directions", "picID"};
    private final String separator = "&!";
    private final String userKey = "username";


    private DataBaseInterface(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    //To ensure the DataBaseInterface is never instantiated more than once
    public static DataBaseInterface getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseInterface(context.getApplicationContext());
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        //CREATE THE TODAY TABLE
        String query = "CREATE TABLE" + " today " + "( date TEXT PRIMARY KEY ";
        for (int i = 0; i < 3; i++) {
            query = query + "," + recipeKeys[i] + " TEXT, " + timeKeys[i] + " TEXT, " + successKeys[i] + " TEXT ";
        }

        query = query + ",endOfDay TEXT";
        query = query + ");";

        db.execSQL(query);


        //CREATE THE RECIPE TABLE
        query = "create table recipes(" + rbKeys[0] + " text primary key," + rbKeys[1] + "  text, " + rbKeys[2] + " text, " + rbKeys[3] + "  text," + rbKeys[4] + "  integer);";

        db.execSQL(query);


        //CREATE THE USER TABLE
        query = "create table user (" + userKey + " text primary key);";
        db.execSQL(query);

        //CREATE THE USER TABLE
        query = "create table picture (id text primary key, pic blob);";
        db.execSQL(query);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS today");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        onCreate(db);

    }

    public void addUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues c = new ContentValues();

        c.put(this.userKey, username);
        db.insert("user", null, c);

    }

    public void addPicture(byte[] bytes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put("pic",bytes);
        c.put("id","1");
        db.insert("picture",null,c);

    }


    public void setPicture(byte[] bytes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("pic", bytes);

        db.update("picture", c, "id = ?", new String[]{"1"});

        db.close();

    }

    public byte[] getPicture() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from picture", null);
        res.moveToFirst();

        byte[] image = res.getBlob(res.getColumnIndex("pic"));

        return image;
    }


    public void updateUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("username", username);
        db.update("user", c, "username = ?", new String[]{getUser()});

    }

    public String getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from user", null);
        res.moveToFirst();

        String username = res.getString(res.getColumnIndex("username"));
        return username;


    }

    public boolean checkIfUserExists() {
        try {
            this.getUser();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //TODO DEBUG
    public boolean checkIfDayExists(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM today WHERE date=?";

        Cursor res = db.rawQuery(selectQuery, new String[]{date});

        res.moveToFirst();
        String results = null;


        while (!res.isAfterLast()) {

            results = res.getString(res.getColumnIndex("date"));
            res.moveToNext();

        }
        if (results == null) {
            return false;
        }
        return true;

    }


    public void addToday(Day Today) {
        //This stores all the data for the current day in the database
        SQLiteDatabase db = this.getWritableDatabase();
        //The method todayContent puts in the recipes, the successes, the times, and the endOfDay
        ContentValues c = todayContent(Today);
        //Put the date in

        //We put the date in!
        c.put("date", Today.getDate());


        db.insert("today", null, c);
        db.close();
    }


    public void updateToday(Day Today) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = todayContent(Today);
        String d = Today.date;
        db.update("today", c, "date = ?", new String[]{d});
        db.close();


    }

    //TODO DEBUG
    public void removeToday(String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM today" + "WHERE date=\"" + date + "\";");


    }

    public Day getToday(String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM today WHERE date=?";

        Cursor res = db.rawQuery(selectQuery, new String[]{date});

        res.moveToFirst();

        String[] r = new String[3];
        String[] t = new String[3];
        String[] s = new String[3];
        String d = null;
        String endOfDay = null;


        while (!res.isAfterLast()) {


            for (int i = 0; i < successKeys.length; i++) {
                r[i] = res.getString(res.getColumnIndex(recipeKeys[i]));
                t[i] = res.getString(res.getColumnIndex(timeKeys[i]));
                s[i] = res.getString(res.getColumnIndex(successKeys[i]));
            }
            d = res.getString(res.getColumnIndex("date"));
            endOfDay = res.getString(res.getColumnIndex("endOfDay"));

            res.moveToNext();


        }


        db.close();

        if (d == null) {
            return null;
        }

        Day day = new Day(date, r, t, s, endOfDay);
        return day;


    }

    private boolean[] stringToBool(String[] successes) {
        boolean[] bool = new boolean[3];
        for (int i = 0; i < 3; i++) {
            boolean b = Boolean.parseBoolean(successes[i]);
            bool[i] = b;

        }
        return bool;
    }


    private ContentValues todayContent(Day Today) {
        ContentValues c = new ContentValues();


        for (int i = 0; i < 3; i++) {
            boolean success = Today.successes[i];
            String bool = Boolean.toString(success);
            String recipe = Today.recipes[i];
            String time = Today.times[i];

            String key = recipeKeys[i];
            c.put(key, recipe);

            key = timeKeys[i];
            c.put(key, time);

            key = successKeys[i];
            c.put(key, bool);

        }
        //Now store the end of the day
        c.put("endOfDay", Today.endOfDay);
        return c;
    }


    public ArrayList<Recipe> getRecipeCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM recipes WHERE " + rbKeys[1] + "=?";

        Cursor res = db.rawQuery(selectQuery, new String[]{category});


        String recipeTitle;
        String ingredS;
        String cat;
        String dirS;
        int picID;

        ArrayList<Recipe> recipes = new ArrayList<>();

        res.moveToFirst();

        while (!res.isAfterLast()) {


            recipeTitle = res.getString(res.getColumnIndex(rbKeys[0]));
            cat = res.getString(res.getColumnIndex(rbKeys[1]));
            ingredS = res.getString(res.getColumnIndex(rbKeys[2]));
            dirS = res.getString(res.getColumnIndex(rbKeys[3]));
            picID = res.getInt(res.getColumnIndex(rbKeys[4]));

            String[] ingredients = this.stringToArray(ingredS);
            String[] directions = this.stringToArray(dirS);

            Recipe r = new Recipe(recipeTitle, cat, ingredients, directions, picID);
            recipes.add(r);


            res.moveToNext();


        }


        db.close();
        return recipes;


    }


    public Recipe getRecipe(String recipeTitle) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM recipes WHERE " + rbKeys[0] + "=?";

        Cursor res = db.rawQuery(selectQuery, new String[]{recipeTitle});


        String category;
        String ingredS;

        String dirS;
        int picID;

        Recipe recipe = null;

        res.moveToFirst();

        while (!res.isAfterLast()) {

            category = res.getString(res.getColumnIndex(rbKeys[1]));

            ingredS = res.getString(res.getColumnIndex(rbKeys[2]));
            dirS = res.getString(res.getColumnIndex(rbKeys[3]));
            picID = res.getInt(res.getColumnIndex(rbKeys[4]));

            String[] ingredients = this.stringToArray(ingredS);
            String[] directions = this.stringToArray(dirS);

            recipe = new Recipe(recipeTitle, category, ingredients, directions, picID);


            res.moveToNext();


        }


        db.close();
        return recipe;


    }

    public ArrayList<String> getRecipeTitles(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  recipeTitle FROM recipes WHERE " + rbKeys[1] + "=?";

        Cursor res = db.rawQuery(selectQuery, new String[]{category});

        ArrayList<String> titles = new ArrayList<String>();
        String recipeTitle;


        res.moveToFirst();

        while (!res.isAfterLast()) {


            recipeTitle = res.getString(res.getColumnIndex(rbKeys[0]));


            titles.add(recipeTitle);


            res.moveToNext();


        }


        db.close();
        return titles;


    }


    private void addRecipe(String recipe, String category, String[] ingred, String[] direction, int picID) {
        ContentValues c = new ContentValues();
        String ingredients = this.arrayToString(ingred);
        String dir = this.arrayToString(direction);
        String[] values = {recipe, category, ingredients, dir};

        for (int i = 0; i <= 3; i++) {
            c.put(rbKeys[i], values[i]);
        }

        c.put(rbKeys[4], picID);

        SQLiteDatabase db = getWritableDatabase();
        db.insert("recipes", null, c);
        db.close();


    }


    private String arrayToString(String[] array) {
        String s = "";
        for (int i = 0; i < array.length; i++) {
            s = s + array[i] + this.separator;
        }
        return s;
    }

    private String[] stringToArray(String s) {
        String[] g = s.split(this.separator);
        return g;
    }


    //For setting up the recipes in the recipes table upon creation of the data base
    //It should be known that this was hardcoded for no good reason... In the future, this should incorporate reading some kind of text file
    public void setUpRecipeBook() {
        String Recipe;
        String Category = "lunch";
        String[] Ingredients;
        String[] Directions;
        int picId;


        Recipe = "Chicken Adobo";

        Ingredients = new String[]{"4 to 5 lbs of chicken wings", "1/4 cup apple cider vinegar", "1/3 cups coconut aminos", "1 cup water", "4 cloves of garlic, crushed", "1 inch piece of fresh ginger minced", "2 long red chillies, thinly sliced", " 2 bay leaves", "2 tbsp of honey", " 1 large onion, thinly sliced", "sea salt and freshly ground black pepper"};
        Directions = new String[]{"1. Combine the apple cider in vinegar, coconut aminos, garlic, ginger, chilies, bay leaves, honey, onion and water in a bowl and stir well", " 2. Season each chicken to the vinegar mixture and marinate for 30 minutes at room temperature or longer in the refrigerator, then add the chicken to the vinegar mixture and marinate for 30 minutes at room temperature or longer in the refrigerator", "3. Transfer the chicken and liquid to a saucepan and bring to a boil over high heat", "4. Cover and simmer over a medium heat for 30 to 35 minutes, or until the chicken is cooked through. Once this is done remove the chicken wings and set aside", "5. Simmer the sauce until it’s reduced by half, then return the chicken to the pan to stir it to combine then serve."};
        picId = R.drawable.chicken_adobo;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Basic Spinach Quiche Recipe";

        Ingredients = new String[]{"5 large eggs", " 1 ½ cups of fresh spinach, chopped", " 1 clove garlic minced", " ½ cup coconut milk", " ½ tsp baking powder", " sea salt and freshly ground black pepper to taste."};
        Directions = new String[]{"1. Preheat oven to 350 degrees F", "2. In a large bowl, whisk the eggs and coconut milk together. Make sure to mix things up thoroughly. As you continue to whisk, start adding in all the ingredients", "3. Grease a 9'' pie dish and pouring everything in. Bake the quiche for about 30 minutes, or until cooked through in the center", "4. Add cheese in to the quiche if desired."};
        picId = R.drawable.basic_spinach_quiche;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Smoked salmon salad in cucumber slices recipe";

        Ingredients = new String[]{"12 oz smoked salmon, coarsely chopped", "¼ cup green onions, finely chopped", "3 tbsp homemade mayonnaise", " 2 tbsp drained capers (optional)", " 1 tbsp fresh dill, chopped + some for garnishing."};
        Directions = new String[]{"1. In a bowl, combine the green onions, dill, capers and mayonnaise", "2. Add the chopped salmon, give the mixture a good stir and season to taste", "3. Make each cucumber slice into a small cup by scooping out the center with a small spoon, leaving the bottom intact", "4. Fill each cucumber cup with the salmon mixture", "5. Sprinkle some fresh dill, season to taste and serve."};
        picId = R.drawable.smoked_salmon_with_cucumer_slices;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Cooked Vegetable Salad Recipe";
        Ingredients = new String[]{" 1/3 cup light olive oil ", "1 eggplant, sliced lengthwise ", "2 zucchini, sliced lengthwise", "2 red onions, cut into wedges", "4 ripe tomatoes, sliced", "garlic cloves, peeled and thinly sliced", "5 cups baby spinach", "Young snow pea shoots", "fresh mint leaves, to taste", "fresh flat-leaf parsley leaves, to taste", "sea salt and freshly ground black pepper", "¼ cup homemade mayonnaise", "1 tbsp extra virgin olive oil", " 1 lemon, juiced", " 1 tbsp dried mint leaves", " sea salt and freshly ground black pepper."};
        Directions = new String[]{"1.Preheat your oven to 425 degrees F", "2.In a bowl, combine all the vegetables except for the spinach, snow peas, mint, and parsley. Pour the olive oil over them, and season with pepper to taste. Toss to coat", "2. Spread the oil-coated vegetables over a baking sheet and roast in the oven for 20 to 25 min", "3. In a bowl, combine all the ingredients for the dressing, then whisk together and season to taste", "4. Assemble the salad by tossing the cooked vegetables with the spinach, snow peas, mint, and parsley", "5. Drizzle the dressing over the salad and gently mix and once everything is done, serve immediately."};
        picId = R.drawable.cooked_vegetable_salad;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Ginger Citrus Roast Chicken Recipe";
        Ingredients = new String[]{"Approximately 5 tbsp of coconut oil (or avocado oil)", "3 lemons or 4 lime", "2 oranges", "1 whole chicken (about 4.5 Ibs)", "3 tbsp grated fresh ginger", " Salt and pepper to taste."};
        Directions = new String[]{"1. Preheat your oven to 425 degrees F; grate one orange and lemon into zest and then cut them in quarters", "2. Wipe the chicken dry and place it in a roasting pan", "3. Mix 1 tbsp of the grated ginger with the citrus zest. Rub the citrus mixture in the chicken cavity along with salt and pepper if desired", "4. Juice the remaining lemons and oranges with 2 tbsp ginger and also add the melted coconut oil. Brush the chicken with the mixture", "5. Put in the oven for 15 minutes", "6. After 15 minutes, baste the chicken and reduce the heat to 375 degrees F", "7. After another 25 minutes, baste again then turn the chicken on his breast and cook for another 25 minutes", "8. At this point, verify the doneness of the chicken by verifying if the juices run clear when you cut the thickest part of the breast. You can also verify with a meat thermometer (it should be about 160 degrees F in the breast and 170 degrees F in thigh)", "9. When ready, remove the chicken from the oven and let it rest for 15 minutes", "10 Garnish with extra citrus wedges if wanted on a bed of steamed vegetables or spinach. Use the citrus, coconut oil and ginger cooking juice as a sauce."};
        picId = R.drawable.ginger_citrus_roast_chicken;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Chunky Meat and Vegetable Soup Recipe";
        Ingredients = new String[]{" 2.5 Ibs ground beef", " 1 large onion diced", " 2 celery stalks diced", " 3 garlic cloves minced", " 14.5 ounces can of diced tomatoes (or 3 large tomatoes diced)", " 3 cups of beef broth", " 3 bell peppers seeded and diced", " 4 whole carrots peeled and sliced", " 2 sweet potatoes cut into chunks", " 3 tbsp tomato paste", "½ tsp ground oregano", " 1 tsp chilli powder", " sea salt and freshly ground black pepper."};
        Directions = new String[]{"1. Brown the meat with onion, garlic and celery in a large saucepan placed over a medium-high heat", "2. Add the remaining ingredients to the saucepan, season to taste then stir to combine", "3. Bring to a boil then reduce to simmer, cover and cook for 15 to 20 minutes or until the sweet potatoes are soft."};
        picId = R.drawable.chunky_meat_vegetable_soup;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Sweet potato salad recipe";
        Ingredients = new String[]{" 3 medium sized sweet potatoes, cubed", " 3 hard-boiled eggs chopped", " 1 green apple, chopped with skin still on", " 1 green apple chopped with skin still on", " 5 strips of bacon roughly chopped."};
        Directions = new String[]{"1. In a large saucepan over a medium heat, add the sweet potato cubes filled with water and bring to a boil. Cook until it’s tender", "2. Meanwhile, fry the bacon until it’s crispy in a small skillet over medium heat", "3. Hard-boil the eggs by placing them in a saucepan containing cold water. Cover it with lid and bring to a boil over high heat then reduce the heat and simmer for 10 minutes. After 10 minutes, drain the hot water and run some cold water over the eggs to stop them from overcooking. After the eggs are cold enough to handle, you can peel and chop them.", "4. In a small bowl, combine all the dressing ingredients and mix thoroughly", "5. In a large bowl, combine the cooked potatoes, eggs, apple and bacon. Top it with the dressing."};
        picId=R.drawable.sweet_potato_salad;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Tomato soup recipe";
        Ingredients = new String[]{" 5 large tomatoes, roughly chopped", " 1 large white onion, roughly chopped", " 2 carrots roughly chopped", " 3 garlic cloves minced", " 1 tbsp tomato paste", " 3 cups chicken or vegetable broth", " ¼ cup fresh basil chopped", " ¼ cup coconut milk", " 2 tbsp avocado oil", " sea salt and freshly ground black pepper."};
        Directions = new String[]{"1. Add some cooking fat to a large saucepan placed over a medium heat and cook the onion and carrots until soft for about 10 minutes", "2. Add the garlic and cook another minute or two", "3. Add the tomatoes, tomato paste, basil and chicken broth. Season to taste with salt and pepper and stir everything together", "4. Bring to a boil then lower the heat and simmer, uncovered for 30 minutes", "5. Stir in the coconut milk, then either use an immersion blender or remove the soup to put it through a food processor until smooth."};
        picId = R.drawable.tomato_soup;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Spinach and sun-dried tomato pasta recipe";
        Ingredients = new String[]{"1 spaghetti squash, halved lengthwise and seeded", "12 dehydrated sun-dried tomatoes", "2 tbsp pine nuts roasted", " ¼ tsp crushed red pepper flakes", " 1 garlic clove minced", " 1 bunch fresh spinach, torn into bite-sized pieces", " ½ cup chicken stock", "cooking oil", " sea salt and freshly ground black pepper."};
        Directions = new String[]{"1. Preheat your oven to 350 F", "2. Place the spaghetti squash, cut side down, on a baking sheet and bake for 30 minutes in the preheated oven", "3. Bring the stock to a boil in a small saucepan, then add in the sun-dried tomatoes and let everything simmer for 15 minutes", "4. Drain the stock into a separate bowl and set aside. Coarsely chop the sun-dried tomatoes ", "5. Remove the squash from oven and set aside until it’s cool enough to handle", "6. Use a fork or spoon to scoop the stringy pulp from the squash; place it in a bowl and set aside", "7. Heat some cooking fat in a skillet placed over a medium heat and add the garlic and red pepper flakes to cook for 1 minute", "8. Add in the spinach, and cook until almost wilted. Pour in the reserved stock, and stir in the chopped sun-dried tomatoes", "9. Look over at the bowl of spaghetti squash. If there’s any extra water, drain it out", "10. Add the squash to the skillet and mix everything well, then cook for 1 or 2 more minutes and serve warm."};
        picId = R.drawable.spinach_and_sundried_tomato_pasta;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Basil spinach salad with poached eggs";
        Ingredients = new String[]{"1 tbsp coconut oil", "1.2 medium sized yellow onions diced", "2 medium sized tomatoes diced", " 4 handful(s) of spinach)", " 1 package(s) basil, fresh (several sprigs)", "2 eggs", "1~2 tsp of white vinegar."};
        Directions = new String[]{"1. Select a suitable pan for poaching. Ideal pan should be shallow and wide that is big enough to add 1.5 liters of water", "2. Add 1~2 teaspoons of white vinegar into the pan and gently mix it well. This will help coagulate the eggs once it’s added", "3. Crack and place the eggs separately in a ramekin or a small cup. Once the water starts to simmer, carefully add the egg into the center of the pot separately. (If you’re experienced at poaching eggs,then you can add both eggs on 10-15 second intervals)", "4. Leave the eggs in for 3 minutes, then remove them once it’s done on a paper towel to remove water", "5. Once it’s dried, sprinkle a small pinch of salt on top and you’re done!", "1. Wash and prepare vegetables", "2. Heat a small frying pan over medium high heat. Add coconut oil when hot", "3. Add diced onions, and saute it until it’s soft and translucent. Then add tomatoes and cook for another minute or two", "4. Add spinach and basil to the pan and cook for one minute", "5. Place it in your favourite bowl and place the poached eggs on top of the salad."};
        picId = R.drawable.basil_spinach_salad;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Kale salad with chicken";
        Ingredients = new String[]{" 1 bunch(es) kale", "lacinato (enough for about 6 cups of chopped leaves)", " 2 tablespoon(s) olive oil", "1 small lemon(s) juiced", "1/2 tsp sea salt (optional)", " ½ tsp black pepper freshly ground", "2 chicken breast(s), boneless skinless cooked and sliced", "1/2 cup sunflower seeds, toasted."};
        Directions = new String[]{"1. Wash kale and remove leaves from woody stems and slice the leaves thinly", "2. In a large bowl, combine kale, olive oil, lemon juice, sea salt (if desired) and freshly ground black pepper", "3.Toss to coat leaves completely and place desired amount of kale into a bowl", "Top each salad with a cooked chicken breast and sunflower seeds to serve."};
        picId = R.drawable.kale_chicken_salad;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Zucchini ribbon salad with sweetcorn";
        Ingredients = new String[]{"1 medium zucchini", "1 medium yellow summer squash", " ½ cup sweet corn kernels cooked", "½ cup red onion thinly sliced", "¼ cup cilantro chopped", "1 tbsp extra virgin olive oil", "2 tbsp white balsamic vinegar to taste."};
        Directions = new String[]{"1. In a large bowl, whisk the olive oil and vinegar together. Season with salt and pepper to taste", "2. Trim the end of the zucchini and yellow squash. With a mandoline, spiralizer or vegetable peeler shave your zucchini and summer squash into beautiful ribbons and place into the bowl containing the vinaigrette. Add the red onion, cooked corn and basil. Gently toss until the vegetables are lightly coated with the vinaigrette. Refrigerate for 1-2 hours before serving to let the flavours merry together."};
        picId = R.drawable.zucchini_ribbon_salad;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Asian mushroom omelette";
        Ingredients = new String[]{"8 free-range eggs", "1 small red chilli deseeded and finely chopped", " 6 green onions thinly sliced", " 2 tbsp peanut oil", " 100g button mushrooms clied", " 80g snow pea sprouts trimmed", " 1 tbsp soy sauce", " 1 1/3 cups steamed jasmine fragrant rice to serve."};
        Directions = new String[]{"1. Break 4 eggs into a bowl. Add half the chilli and half the green onions. Whisk with a fork", "2. Heat a wok over medium heat. Add half the oil. Swirl to coat. Pour in egg mixture. Swirl to cover base and run 1cm up the side. Sprinkle half the mushrooms over egg mixture. Cook for 30 seconds or until base is set. Fold omelette in half. Tilt wok to allow any uncooked egg to run to edge. Cook for 30 seconds. Turn omelette over and cook for 30 seconds or until light golden (omelette should still be moist inside). Transfer to a plate. Cover to keep warm. Repeat with remaining eggs, chilli, green onions, oil and mushrooms", "3. Cut omelettes in half and place on plates. Top with snow pea sprouts. Drizzle with soy sauce. Season with salt and pepper. Serve with rice."};
        picId = R.drawable.asian_mushroom_omlette;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Vegetable egg drop soup";
        Ingredients = new String[]{"1/2 ounce (about 5) dried shiitake mushrooms", "8 ounces boned, skinned chicken breast", "1 tablespoon cornstarch", " 5 tsp soy sauce", "3/4 tsp white pepper, divided", "2 qts. vegetable broth or chicken broth", "8 -10 thin slices peeled fresh ginger", "2 small carrots, roll-cut into 1-in. pieces", "1 medium onion, diamond-cut into 1-in. pieces", "1 stalk celery, V shaped-cut into 1-in. pieces", "1 slender zucchini, roll-cut into 1-in. pieces", "1 large tomato, peeled, diamond-cut into 1-in. pieces", "4 long cilantro sprigs, small sprigs pinched off and stems cut into 1-in. pieces", " 1/2 teaspoon kosher salt", "1-2 tsp. toasted sesame oil", "2 large eggs."};
        Directions = new String[]{"1. Soak mushrooms in a bowl with 1 1/2 cups warm water until pliable, 20 to 25 minutes. Remove mushrooms, saving liquid. Snip out stems with scissors and discard. Slice mushrooms diagonally 1 in. wide, then diamond cut ", "2. Meanwhile, lay chicken flat on a work surface and slap with the side of a Chinese chef's knife to flatten to 1/4 in. Cut into lengthwise strips about 1/2 in. wide, then diamond cut", "3. In a bowl, combine chicken with cornstarch, 1 tbsp. soy sauce, and 1/2 tsp. white pepper. Then set this aside", "4. Bring broth, mushroom soaking liquid, and ginger to a boil in a large covered pot over high heat. Reduce heat to medium-high and add mushrooms and carrots.Don't throw in everything together or it will be overcooked. In Chinese cooking, you want everything to have crunchy texture", "5. Cook it for 5 minutes, then add onion, celery, and zucchini.  Then cover to let it boil low, and cook until onion is tender-crisp, 2 to 3 minutes", "6. Add chicken, tomato, cilantro stems, remaining 1/4 tsp. white pepper and 2 tsp. soy sauce, the salt, and oil to taste. Cover it for about 3 minutes or more for  the chicken to be thoroughly cooked ", "7. Whisk the eggs in a bowl until the egg whites are completely blended, but not so much that they foam, as it can form residues on top ", "8. Let it simmer under low fire while stirring it very slowly with a ladle, then gradually pour the eggs in. Garnish with cilantro sprigs."};
        picId = R.drawable.vegetable_egg_drop_soup;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Arugula shrimps with grapes";
        Ingredients = new String[]{"3/4 Ibs large shrimp, peeled and deveined", "1/2 cup diagonally cut celery", "5 cups trimmed arugula", "1 cup seedless red grapes, halved", "1 cup seedless green grapes, halved", "1/4 cup fresh basil leaves, thinly sliced", "2 tbsp crumbled Gorgonzola cheese", "1 tbsp coarsely chopped walnuts, lightly toasted.", "1/3 cup seedless green grapes", "1 tbsp Champagne or white wine vinegar", "1 tsp olive oil", "3/4 tsp Dijon mustard", "1/2 tsp minced fresh Vidalia or sweet onion (optional)", "1/8 tsp salt", "Dash of white pepper."};
        Directions = new String[]{"1. To prepare dressing, combine the first 7 ingredients in a blender and process it until it’s smooth", "2.To prepare salad, bring 4 cups water to a boil in a large saucepan. Add shrimps and cook it for 1 minute. Add celery and cook it for 1 minute as well . Drain the saucepan and rinse  with cold water, then pat dry", "3. Place shrimp mixture, arugula, grapes, and basil in a large bowl. Drizzle with dressing then toss gently for it to coat. Then top it with cheese and walnuts."};
        picId = R.drawable.aurugla_shrimp_salad_with_grapes;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Category = recipeKeys[0];

        Recipe = "Breakfast Salad";
        Ingredients = new String[]{"2 cups of mixed greens", "1 green onion,slice", "4 cherry or pear tomatoes,chopped", "half bell pepper,diced", "1 large egg", "Prosciutto or bacon to taste", " 2 tbsp extra virgin olive oil", "1tsp balsamic vinegar", "1tbsp fresh lemon juice", "2 tsp rice vinegar", "Sea salt and freshly ground black pepper to taste"};
        Directions = new String[]{"1. In a small saucepan placed over a medium-high heat, bring the water to a gentle simmer and add the rice vinegar", "2. Break the egg into the water, turn off the heat, and cook for about 4 minutes", "3. In a small bowl, combine the olive oil, balsamic vinegar, lemon juice, and salt and pepper to taste", "4. Mix the greens, bell pepper, tomatoes, and prosciutto or bacon in a serving bowl.", "5. Add the olive and oil balsamic vinegar to the salad", "6. Add the poached egg on top of the salad and serve"};
        picId = R.drawable.breakfast_salad;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Scrambled Eggs with Smoked Salmon, Asparagus and Goat Cheese";
        Ingredients = new String[]{" ¼ tbsp butter", "2 stalks asparagus", "woody bottoms removed and chopped into 1 pieces", "salt and black pepper to taste", "2 eggs", "¼ tbsp fat-free milk", "1tablespoon crumbled fresh goat cheese", "1 oz smoked salmon, chopped"};
        Directions = new String[]{"1. Heat the butter in a large nonstick skillet or saute pan over medium heat. When the butter begins to foam, add the asparagus and cook until just tender ('crisp-tender' in kitchen parlance). Season with salt and pepper", "2. Crack the eggs into a large bowl and whisk with the milk. Season with a few pinches of salt and pepper and add to the pan with the asparagus. Turn the heat down to low and use a wooden spoon to constantly stir and scrape the eggs until they begin to form soft curds. A minute before they're done, stir in the goat cheese", "3. Remove from the heat when the eggs are still creamy and soft (remember, scrambled eggs are like meat-- they continue to cook even after you cut the heat) and fold in the smoked salmon"};
        picId = R.drawable.scrambled_eggs;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Egg Sandwich";

        Ingredients = new String[]{"1 egg", "1 tsp olive oil", "1 whole wheat English muffin", "1 slice reduced-fat Swiss cheese", "1 slice tomato", "3 baby spinach leaves", "1 small apple"};

        Directions = new String[]{"1. Fry 1 egg in olive oil", "2. Fill English muffin with cheese, tomato, spinach, and fried egg", "3. Serve with apple"};
        picId = R.drawable.egg_sandwhich;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Homemade Muesli";
        Ingredients = new String[]{"½ cup raw oats", "2 tbsp raisins, ¼ cup chopped apple", "1 tbsp slivered almonds (or 6 almonds, chopped)", "4 oz low-fat plain yogurt", "2 oz fat-free milk", "½ tsp brown sugar or honey (if desired)"};
        Directions = new String[]{"1. Mix ingredients together and eat as soon as you can!"};
        picId = R.drawable.homemade_muesli;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Zucchini and Eggs";
        Ingredients = new String[]{"2 tsp olive oil", "1 egg beaten", "1 small zucchini, sliced", "salt and pepper to taste"};
        Directions = new String[]{"1. Heat a small skillet over medium Heat, then pour in oil and saute zucchini until it’s tender", "2. Spread out the zucchini into an even layer and pour beaten egg evenly over top, cooking it until the egg is firm", "3. Season it with salt and pepper to taste"};
        picId = R.drawable.zucchinie_and_eggs;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Tomato and egg stir fry";
        Ingredients = new String[]{"2tbsp avocado oil", "6 eggs, beaten", "2 green onions, sliced thin"};
        Directions = new String[]{"1. Heat 1tbsp of avocado oil in a wok or skillet over medium heat, then cook and stir eggs in the hot oil until mostly cooked through, about 1 minute. Then transfer the eggs to a plate", "2. Pour remaining 1tbsp avocado oil into wok, then cook and stir tomatoes until most of the liquid has evaporated for about 2 minutes", "3. Pour the eggs to wok and add green onions, then stir the eggs until they’re fully cooked for about 30 seconds"};
        picId = R.drawable.egg_tomato_stirfry;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = " Summer fruit salad with lemon,honey and mint dressing";
        Ingredients = new String[]{"4 cups of cubed seeded watermelon", "fresh lemon juice", "2 cups of sliced fresh strawberries", "1/4 cup of minced fresh mint", "2 large cubed peaches", "2 large cubed nectarines", "1 cubed red anjour pear", " 1 cup seedless grapes, halved", " 1/2 of lemon, zested", " 1tbsp honey"};
        Directions = new String[]{"1.Combine watermelon, strawberries, peaches, nectarines, pear and grapes in a large mixing bowl", "2. Whisk lemon juice, mint, lemon zest and honey together in a small bowl. Then drizzle over the fruit mixture and toss to coat", "3. Refrigerate 1 hour before serving"};
        picId = R.drawable.summer_fruit_salad;

        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Deliciously healthy paleo pancakes with banana and walnuts";
        Ingredients = new String[]{"1 tsp coconut oil or more if needed", "6 large bananas, sliced", "6 eggs", "3 tablespoons extra-virgin coconut oil", "2 tbsp vanilla extract", "1/2 tsp salt", "1/2 tsp baking soda", "1tsp ground cinnamon", "1/2 cup chopped walnuts"};
        Directions = new String[]{"1.Heat 1 tsp coconut oil on a griddle set to 325 degrees F or a skillet over medium heat", "2. Place bananas in the bowl of a stander mixer, then add eggs, 3 tablespoons of coconut oil, vanilla extract, salt and baking soda. Then beat until the batter is smooth and fluffy", "3.Gently ladle batter about ¼ cup per pancake onto the hot griddle, then sprinkle with cinnamon and arrange walnuts on each pancake. Cook until bubbles form and the edges are dry for about 3 to 4 minutes. Flip and cook until browned on the other side 3 to 4 more minutes. Repeat with remaining batter, adding more coconut oil between batches"};
        picId = R.drawable.paleo_pancakes_banana;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Joe’s Special Scramble";
        Ingredients = new String[]{"2tbsp olive oil", "2 Ibs ground beef", " 2 cloves of garlic, minced", "2 onions, chopped", "1 (8 ounce) package sliced fresh mushrooms", "1/2 tsp ground nutmeg", "1/2 teaspoon dried oregano", "1 (10ounce) package frozen chopped spinach, thawed and drained", "6 eggs", "salt and pepper to taste"};
        Directions = new String[]{"1. Heat the olive in a large skillet over medium-high heat. Add the ground beef and cook, stirring to crumble until it’s no longer pink (for about 8 minutes).”, “2. Pour off any excess grease then stir in the garlic, onions and mushrooms. Reduce heat to medium, then cover and cook until the onions have softened and turned translucent for about 5 minutes.”, ”3. Stir in the nutmeg, oregano, spinach, salt and pepper and cook until the spinach is heated well", "4. Reduce the heat to medium –low and make 6 egg sized indentations into the beef and spinach mixture. Then crack the eggs into each indentations, then cover and continue cooking until the eggs are done to your liking for about 5 minutes or more on medium fire"};
        picId = R.drawable.joe_special_scramble;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Paleo Pancakes with Pureed Strawberries";
        Ingredients = new String[]{"1.5 cups of almond meal", " 2 eggs", "1/2 tsp vanilla extract", "1/2 tsp ground cinnamon", "1/2 cup applesauce", "1/4 tsp baking powder", "1/4 cup coconut milk, or more as needed", "1tsp olive oil", "1 cup strawberries"};
        Directions = new String[]{"1. Mix together almond flour, eggs, vanilla extract, cinnamon, applesauce, baking powder and coconut milk in a bowl", "2. Lightly oil a griddle and place over medium-high heat. Drop batter by large spoonfuls onto the griddle and cook until bubbles form and the edges of pancakes are dry. Flip and cook until browned on the other side. Repeat with remaining batter", "3.Puree strawberries in a food processor until smooth, then top pancakes with pureed strawberries"};
        picId = R.drawable.paleo_pancakes_strawberries;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Category = recipeKeys[2];

        Recipe = "Nectarine and Onion Pork Chops";
        Ingredients = new String[]{"6 pork chops, bone-in", "3 nectarines, quartered", "1 large onion, quartered", "2 tsp Dijon mustard", "1.5 tbsp lemon juice", "¼ cup fresh mint, coarsely chopped", "2 tbsp cooking fat + extra for rubbing the chops, melted", "Sea salt and freshly ground black pepper to taste."};
        Directions = new String[]{"1.Combine the nectarine and onion quarters in a bowl along with the 2 tbsp cooking fat and season the mixture to taste with sea salt and freshly ground black pepper", "2. Heat a skillet over medium heat, add the nectarine and onion mixture and cook, stirring frequently until the nectarine quarters have softened for about 8 minutes", "3. Set the cooked mixture aside to cool in a bowl. Wipe the skillet clean to cook the pork chops", "4. Rub some additional cooking fat over the pork chops on both sides and season them to taste with salt and pepper. Reheat the skillet to medium heat", "5. Add the chops to the hot skillet and cook for about 3 minutes per side, until well cooked", "6. While the pork chops are cooking, cut the cooked nectarine and onion quarters into ¼ inch sized slices. Then add them back to the bowl with their juices", "7. Add the lemon juice, mustard and chopped mint to the nectarine and onion preparation and season to taste with salt and pepper", "8. Serve the cooked pork chops topped with a generous portions of the nectarines and onion preparation."};
        picId = R.drawable.nectarine_onion_pork;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Balsamic steak rolls";
        Ingredients = new String[]{"1.5 to 2 Ibs skirt steak, sliced into thin strips", "1 carrot, matchstick cut", "1 bell pepper, matchstick cut", " ½ zucchini, matchstick cut", " 5 green onions, matchstick cut", " 2 cloves of garlic, minced", " ½ tsp dried oregano", " ½ tsp dried basil", " cooking fat", "sea salt and freshly ground black pepper.", "1 tbsp ghee (clarified butter)", "2 tbsp shallot onion", "1/4 cup balsamic vinegar", " 1 tbsp honey", " ¼ cup beef stock", " sea salt and freshly ground black pepper"};
        Directions = new String[]{"1. Season the steak slices with sea salt and freshly ground pepper to taste and set aside", "2. Melt the ghee in a skillet placed over a medium heat", "3. Add the shallot onions and cook until soft for about 3 minutes", "4. Add the balsamic vinegar, honey, beef stock and season again with salt and pepper to taste", "5. Bring to a boil, lower the heat and simmer until the liquid is reduced by half. Transfer to a bowl."};
        picId = R.drawable.balsamic_steak;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Oven chicken fajita bake recipe";
        Ingredients = new String[]{"3-4 boneless skinless chicken breasts", "1-2tbsp taco seasoning", " 2 bell peppers, deseeded and thinly sliced", "1 red onion, peeled and thinly sliced.", "1 tbsp chilli powder", "1tsp paprika powder", "1/4 tsp garlic powder", "1/4 tsp onion powder", "1/4 tsp crushed red pepper flakes", "1/4 dried oregano flakes", "1.5 tsp ground cumin", " sea salt and fresh ground black pepper"};
        Directions = new String[]{"1. In a bowl, combine all the ingredients for the taco seasoning", "2. Preheat your oven to 375F", "3. Place the chicken breasts in a baking dish", "4. Sprinkle the taco seasoning over the chicken", "5. Place it in the oven and cook for 20 minutes", "6. Remove the chicken from the oven and lay the onion and bell peppers on top", "7. Return the dish to the oven and cook for another 15 to 20 minutes", "8. Serve with guacamole or your favourite salsa"};
        picId = R.drawable.chicken_fajitas_limes;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Greek-style chicken recipe";
        Ingredients = new String[]{"4 skinless, boneless chicken breast halves", "1 cup extra-virgin olive oil", "juice from one lemon", " 2 garlic cloves, minced", "1/2 tbsp dried oregano flakes", "1/2 tsp paprika", "1 or 2 lemons , sliced", "sea salt and freshly ground black pepper"};
        Directions = new String[]{"1. In a bowl, combine olive oil, garlic, paprika, lemon juice, oregano and season with salt and pepper to taste", "2. Place the chicken in a non-metal marinating container and cover it with marinade", "3. Cover the marinated chicken and refrigerate for at least one hour or overnight", "4. Preheat your oven to 400F", "5. Place the chicken in a roasting pan and cover each piece with a slice of lemon", "6. Place it in the oven and bake for 30 to 40 minutes"};
        picId = R.drawable.greek_style_chicken;

        Recipe = "Pineapple pork chops recipe";
        Ingredients = new String[]{"4 big pork chops", "4 big slices of fresh pineapple, cut into 1-inch chunks", " ½ cup coconut aminos", "¼  cup raw honey (optional)", "2 tbsp apple cider vinegar", "1 tbsp minced ginger", " ½ cup fresh pineapple juice", " ¼ cup water", "2 tbsp minced garlic"};
        Directions = new String[]{"1. In a small bowl, whisk the marinade ingredients together", "2. Place the pork chops and marinade in a marinating container and refrigerate for at least 3 hours", "3. Remove the pork chops from marinade but do not discard the marinade", "4. Pour the remaining marinade into a saucepan. Bring to a boil, then reduce to a simmer and cook for about 4 minutes", "5. Preheat your grill to a medium high heat", "6. Cook the pork chops on the preheated grill, about 5 minutes per side, brushing with the marinade the whole time", "7. Add the pineapple chunks to the grill and cook for 3 to 4 minutes, flipping them halfway through", "8. Serve the pork chops with the cooked pineapple on top."};
        picId = R.drawable.pineapple_pork;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Butternut squash lasagna recipe";
        Ingredients = new String[]{"1.5 Ibs ground beef", "1 large butternut squash, peeled and cut into thin slices", "4 cups tomato sauce", "4 oz tomato paste", "1 onion minced", "3 garlic cloves, minced", "1 tsp dried basil", "1 tsp dried oregano flakes", "Olive oil", " Sea salt and freshly ground black pepper."};
        Directions = new String[]{"1. Preheat your oven to 400 degrees F", "2. Melt some cooking fat and sauté the onion and the garlic until softened, about 5 minutes in a skillet or a saucepan placed over medium-high heat", "3. Add the beef and cook until browned for about 6 minutes", "4. Add the tomato sauce, tomato paste, basil, oregano and season with salt and pepper to taste. Turn heat down to low and let it simmer for about 10 minutes", "5. To prepare the lasagna, alternate layers of butternut squash slices with layers of the meat sauce in a baking dish. Keep making layers until you have used all of the ingredients", "6. Bake for about 25 minutes (or until the squash is soft) in the preheated oven. Once all is done, enjoy!"};
        picId = R.drawable.butternut_squash_lasagna;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Poached Wild Salmon with Seared Potatoes & Sweet Pea Purée";
        Ingredients = new String[]{"3 cups (750 mL) water", "1 stalk organic leek", "1 whole small organic carrot", "2 bay leaves", "3 oz (85 g) wild salmon fillet", "1 skinned medium organic Yukon Gold potato (do not peel) washed and cut into four 1/8­inch (2mm) thick slices", "1 cup (250 mL) frozen organic peas defrosted and steamed for 3 minutes", "1 tbsp (15 mL) organic butter Salt and pepper, to taste"};
        Directions = new String[]{"1. In a large saucepan, combine 3 cups (750 mL) water, leek, carrot and bay leaves. Bring to a slow simmer. Gently slide salmon into water and poach for 10 minutes. Remove and set aside. Reserve 2 tbsp (30 mL) of the poaching liquid", "2. In a heavy cast iron skillet or stainless steel skillet, heat oil over medium heat. Add potatoes in one layer. Sauté potatoes, flipping once, until they are deeply browned on both sides. Season each side with salt and pepper. Arrange cooked potatoes on warmed serving plate, place poached salmon on top", " 3. In a blender, add peas, butter and 2 tbsp (30 mL) hot poaching liquid. Purée until smooth. Add a pinch of salt and pepper; pulse", "4. Pour 2 tbsp (30 mL) of the pea purée atop the salmon and potatoes. In a small serving bowl, pour remaining pea purée", "5.Serve"};
        picId = R.drawable.poached_salmon;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Cumin Crusted Fish";
        Ingredients = new String[]{"½ tbsp (7 ­ 15 mL) ground cumin", "¼ tsp (1 mL) thyme", "1 tsp (5 mL) paprika", " ½  tsp (2 mL) lemon pepper", "1 lb (500g) white fish fillets (walleye, halibut, cod...)", "½  tbsp (7 mL) canola oil", "2 tbsp (25 mL) chopped parsley", "Lemon or lime wedges"};
        Directions = new String[]{"1. In a small bowl, mix together cumin, thyme, paprika and lemon pepper. Rub spice mixture on both sides of fillets", "2. In a large skillet over medium heat, heat canola oil. Add fish fillets and cook until browned on both sides and fish is opaque in the centre, about 4 minutes per side", "3. Sprinkle with parsley and serve immediately with lemon or lime wedges"};
        picId = R.drawable.cumin_fish;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);




        Recipe = "Chicken Stir-­Fry with Broccoli";
        Ingredients = new String[]{"2 tbsp. chopped garlic", "2 tbsp. peanut oil", "2 tbsp. chopped ginger", "4 large skinless, boneless chicken breasts, sliced thinly (about 4­5 ounces each)", "2 cups broccoli florets cut into small pieces", "½  cup water chestnuts", "2 cups thinly sliced mushrooms", "Freshly ground black pepper", "½  cup low sodium soy sauce"};
        Directions = new String[]{"1. Heat a large skillet or wok over medium-­high heat", "2. Add the garlic, peanut oil and ginger, stir quickly for 30 seconds", "3. Raise the heat to high and add in chicken stirring for 2­3 minutes", "4. Add the broccoli, water chestnuts and mushrooms and stir after each", "5. Season with black pepper", "6. Add the soy sauce and cook until the vegetables are tender, about 2 minutes"};
        picId = R.drawable.chicken_stirfry_brocco;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = " Blackberry Buckle";
        Ingredients = new String[]{"½  cup shortening", "1 cup sugar, divided", "1 egg, beaten", "2 ½  cup all-purpose flour, divided", "2  ½ tsp baking powder", "½  tsp salt", "2 cups fresh or frozen blackberries", " 2 tsp lemon juice", "½  tsp ground cinnamon", "½  cup cold butter"};
        Directions = new String[]{"1. In a large mixing bowl, cream the shortening and 1/2 cup sugar", "2. Add the egg and mix well", "3. Combine 2 cups flour, baking powder and salt; add to the creamed mixture beating well after each addition", " 4. Spread into a greased 9­inch square baking dish", "5. Toss the blackberries with lemon juice; and sprinkle over batter", " 6. In a small bowl, combine the cinnamon and remaining sugar and flour; cut in the butter until mixture resembles coarse crumbs, Sprinkle over berries", " 7. Bake, uncovered, at 350 degrees F for 45­ to 50 minutes"};
        picId = R.drawable.blackberry_buckle;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Coconut Chicken";
        Ingredients = new String[]{"1 Ibs boneless chicken breast", "¼ cup almond flour", " ¼ cup unsweetened shredded coconut", "⅛ tsp sea salt, 1 large egg", "2 tbsp coconut oil."};
        Directions = new String[]{"1. Mix almond flour, shredded coconut and sea salt together in a bowl", "2. Beat egg in separate bowl", "3. Dip chicken breast in egg and roll in dry mixture", "4. Heat a frying pan over medium heat and add coconut oil when hot", "5. Pan-fry chicken until fully cooked. If the crust starts to brown and your chicken isn’t fully cooked yet (this will depend on the size of the chicken breast), take it out of the pan and place it in the oven on a baking sheet at 350 degrees F for 5-10 minutes covered with foil."};
        picId = R.drawable.coconut_chicken;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Apple Chicken";
        Ingredients = new String[]{" 2 chicken breasts, boneless skinless (4-6 oz)", " ¼ tspsea salt, (optional)", " ⅛ tsp black pepper, freshly ground", "2 tsp coconut oil", " 1 large apple", " ½ tsp cinnamon, or allspice"};
        Directions = new String[]{"1. slice chicken breasts.Season with sea salt and freshly ground black pepper. Set aside", "2. Heat a medium saute pan over medium-high heat. Add coconut oil when hot", " 3. Add diced chicken and cook until slightly pink ( approximately 150 degrees F)", " 4. Grate apple into pan", " 5. Add cinnamon or allspice", " 6. Continue to cook until chicken is done and apple is tender"};
        picId=R.drawable.apple_chicken;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Zucchini Mushroom Pasta";
        Ingredients = new String[]{"1 spaghetti squash, halved lengthwise and seeded", "1 Ibs cremini mushrooms, sliced", "2 zucchini, chopped", "2 shallots, sliced thinly", "2 cloves garlic, minced", "2 sprigs thyme", "¼ cup coconut milk", "3 tbsp olive oil", "Cooking fat", "Sea salt and freshly ground black pepper"};
        Directions = new String[]{"1. Preheat your oven to 350 F.", "2. Lightly oil the spaghetti squash with 1 tbsp of olive oil and season to taste", "3. Place the squash, cut side down, on a baking sheet and bake 30 minutes, or until the flesh is soft", "4. Remove the squash from the oven and set aside until cool enough to be easily handled", "5. Use a large fork or spoon to scoop the stringy flesh from the squash and place it in a medium bowl", "6. Melt some cooking fat in a skillet placed over a medium- ­high heat", "7. Add the garlic, thyme, shallots and cook until soft and fragrant for about 3 to 4 minutes", "8. Add zucchini, mushroom and cook until it softens but still al dente", "9. Add coconut milk and bring to a simmer stirring constantly", "10. Add the spaghetti to the skillet. Mix everything well, drizzle with the remaining olive oil, adjust the seasoning and serve warm"};
        picId = R.drawable.zucchini_pasta;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Mushroom Salad";
        Ingredients = new String[]{"½ cup hazelnuts", "2 tbsp shallots, finely chopped", "3 tbsp rice vinegar", "9 tbsp extra-­virgin olive oil", "2 Ibs mushrooms,(we suggest using Portobello’s) sliced thickly", "2 tbsp butter or clarified butter", "6 oz fresh greens (arugula or baby spinach are two great options)", "1 tsp fresh thyme", "¼ cup shallots, finely chopped", "Sea salt and freshly ground black pepper to taste"};
        Directions = new String[]{"1. Preheat your oven to 375F. This step is only necessary if your hazelnuts still have the skin membrane on them. Spread the hazelnuts out on a baking sheet and roast them in the oven for about 8 minutes. You will know they are ready, as the skin will have already started to rise off the nut. Allow them to completely cool and then roll them through your hands so that the skin falls apart. Set aside", "2. As the hazelnuts are roasting, you’ll have time to prepare the marinade/vinaigrette. In a small bowl, combine the 2 tbsp. of shallots, vinegar and a dash of sea salt. Whisk the mixture together and set aside for 5 minutes. This will allow the shallots to absorb the vinegar. Once the 5 minutes has elapsed, you can go ahead and whisk in 7 tbsp of the olive oil. Set aside for later use", "3. In a large skillet over a medium-­high heat, melt the butter or clarified butter and add the remaining 2 tbsp olive oil. Throw in the mushrooms and sprinkle with the thyme and some salt and pepper to taste. The cooking time will vary depending on what type of mushrooms you decided to go with. For the Portobello’s, they need anywhere from 8 to 10 minutes, just until they have begun to turn golden and have shrunk in size quite a bit. At this point, you can add the remaining 1/4 cup shallots in with the mushrooms and continue cooking for another few minutes, until the shallots are soft", "4. Fill a large plate or bowl with the fresh greens. Transfer the mushrooms from the skillet on top of the greens. Pour over the vinaigrette and top with the hazelnuts (you may prefer to crush the hazelnuts). Eat and enjoy every last bite!"};
        picId = R.drawable.mushroom_salad;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Category = recipeKeys[3];

        Recipe = "Kale-banana smoothie";
        Ingredients = new String[]{"2-3 ice cubes", "1 cup kale", "½ banana", "2-3 strawberries ", "2 cups water", "1 tsp hemp seeds."};
        Directions = new String[]{"1. Wash kale and dry. Then separate the kale leaves from the stems and add the leave into a blender", "2. Divide the banana and strawberries into small chunks and add it into the blender", "3. Add in water and ice", "4. Sprinkle in hemp seeds", "5. Blend to preferred texture", "6. Pour into a glass and enjoy."};
        picId = R.drawable.kale_smoothie;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Banana chips";
        Ingredients = new String[]{"3-4 bananas", "baking sheet", "cinnamon", "1/2 cup of water", "1 lemon, juiced", "baking pan", "Parchment paper."};
        Directions = new String[]{"1. Preheat the oven to 250 degrees F", "2. Squeeze lemon juice into small bowl", "3. Thinly slice bananas", "4. Dip each banana slices into lemon and place on parchment paper", "5. Once all slices are in baking sheet and sprinkle on cinnamon", "6. Place baking sheet into oven", "7. Bake until golden brown and crispy", "8. Place into a bowl and serve with a glass of milk."};
        picId = R.drawable.banana_chips;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Chocolate covered strawberries";

        Ingredients = new String[]{"16 strawberries", "2 oz 100% pure unsweetened chocolate", "1 tsp coconut oil", "baking pan", "baking sheet."};
        Directions = new String[]{"1. Wash and dry strawberries and bring to room temperature", "2. In a saucepan melt together chocolate and oil at low temperature", "3. Consistently stir until chocolate is completely melted and is a smooth texture, then remove from heat", "4. Tilt the saucepan and dip each strawberries, coating it in chocolate", "5. Place onto baking sheet", "6. Once finished, set the sheet into the refrigerator to cool", "7. Once cooled, lift carefully onto a serving plate and enjoy!"};
        picId = R.drawable.chocolate_covered_strawberries;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);

        Recipe = "Kale Chips";
        Ingredients = new String[]{"1 bunch kale", "2 tbsp extra virgin olive oil", "1 tbsp cup balsamic vinegar", "salt", "sesame seeds"};
        Directions = new String[]{"1. Wash kale and dry, then destem the kale, separating the leaves from the stem", "2. Fold each leaves in half and run a knife along the stem", "3. Add the kales in the bowl, then add in oil and vinegar, massaging into the kale until it’s fully coated", "4. Lay each leaf onto the baking sheet making sure they are not on top of each other", "5. Grab a pinch of salt and sprinkle on the bowl to add taste", "5. Sprinkle on sesame seeds and place it into the oven", "6. Bake into the oven for 8-9 minutes to make it crispy, and once it’s done take it out of the oven rack for it to cool.", "7. Once it’s cooled down, enjoy!"};
        picId = R.drawable.kale_chips;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);


        Recipe = "Berry smoothie";
        Ingredients = new String[]{"Berry smoothie", "2-3 ice cubes", "½ cup raspberries", "½ blueberries", "2-3 strawberries", "¼ cup greek yogourt", "1 tsp hemp seeds", "2 cups orange juice"};
        Directions = new String[]{"1. In a blender, add in raspberries, blueberries and strawberries", "2. Add in greek yogurt", "3. Add in orange juice and ice", "4. Sprinkle in hemp seeds", "5. Blend to preferred texture", "6. Pour into a glass and enjoy!"};
        picId = R.drawable.berry_smoothie;
        this.addRecipe(Recipe, Category, Ingredients, Directions, picId);




    }

}

