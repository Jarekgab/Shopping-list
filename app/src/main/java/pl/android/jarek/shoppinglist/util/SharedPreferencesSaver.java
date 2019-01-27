package pl.android.jarek.shoppinglist.util;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import pl.android.jarek.shoppinglist.data.ShoppingElement;


public class SharedPreferencesSaver {

    private static final String LIST_ITEMS_KEY = "LIST_ITEMS_KEY";
    private static final String SPINNER_ITEMS_KEY = "SPINNER_ITEMS_KEY";

    public static void saveTo(List<List<ShoppingElement>> shoppingElementList, List<String> spinnerList, SharedPreferences sp){

        Gson gson = new Gson();
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(LIST_ITEMS_KEY, gson.toJson(shoppingElementList));
        editor.putString(SPINNER_ITEMS_KEY, gson.toJson(spinnerList));
        editor.apply();                                                   //zapis rownoległy bez blokady wątku
    }

    public static List<List<ShoppingElement>> loadShoppingElementList(SharedPreferences sp){

        String newShoppingElementList = sp.getString(LIST_ITEMS_KEY, null);
        Gson gson = new Gson();

        return gson.fromJson(newShoppingElementList, new TypeToken<List<List<ShoppingElement>>>() {}.getType());
    }

    public static List<String> loadSpinnerList(SharedPreferences sp){

        String newSpinnerList = sp.getString(SPINNER_ITEMS_KEY, null);
        Gson gson = new Gson();

        return gson.fromJson(newSpinnerList, new TypeToken<List<String>>() {}.getType());
    }
}
