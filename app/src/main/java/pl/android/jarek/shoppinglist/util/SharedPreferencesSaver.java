package pl.android.jarek.shoppinglist.util;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import pl.android.jarek.shoppinglist.data.ShoppingListElement;


public class SharedPreferencesSaver {

    private static final String LIST_ITEMS_KEY = "LIST_ITEMS_KEY";
    private static final String SPINNER_ITEMS_KEY = "SPINNER_ITEMS_KEY";

    public static void saveTo(List<List<ShoppingListElement>> spinnerList, List<String> spinnerItems, SharedPreferences sp){

        Gson gson = new Gson();
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(LIST_ITEMS_KEY, gson.toJson(spinnerList));
        editor.putString(SPINNER_ITEMS_KEY, gson.toJson(spinnerItems));
        editor.apply();                                                   //zapis rownoległy bez blokady wątku
    }

    public static List<List<ShoppingListElement>> loadSpinnerL(SharedPreferences sp){

        String newSpinnerListString = sp.getString(LIST_ITEMS_KEY, null);
        Gson gson = new Gson();

        return gson.fromJson(newSpinnerListString, new TypeToken<List<List<ShoppingListElement>>>() {}.getType());
    }

    public static List<String> loadSpinnerI(SharedPreferences sp){

        String newSpinnerItemsString = sp.getString(SPINNER_ITEMS_KEY, null);
        Gson gson = new Gson();

        return gson.fromJson(newSpinnerItemsString, new TypeToken<List<String>>() {}.getType());
    }
}
