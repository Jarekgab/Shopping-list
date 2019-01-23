package pl.android.jarek.shoppinglist;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import org.angmarch.views.NiceSpinner;
import java.util.ArrayList;
import java.util.List;
import pl.android.jarek.shoppinglist.adapter.ShoppingListAdapter;
import pl.android.jarek.shoppinglist.data.ShoppingListElement;
import pl.android.jarek.shoppinglist.util.SharedPreferencesSaver;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView itemList;
    NiceSpinner itemSpinner;
    ImageButton deleteButton;
    BootstrapEditText itemName;
    Button addButton;

    private List<String> spinnerItems;
    private List<List<ShoppingListElement>> spinnerList;
    private ShoppingListAdapter listAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        itemList = findViewById(R.id.itemList);
        itemSpinner = findViewById(R.id.itemSpinner);
        deleteButton = findViewById(R.id.deleteButton);
        itemName = findViewById(R.id.itemName_ET);
        addButton = findViewById(R.id.addButton);

        spinnerItems = new ArrayList<>();

        if (spinnerItems.isEmpty()) {
            spinnerItems.add("Lista główna");
            spinnerList = new ArrayList<>();
            spinnerList.add(new ArrayList<ShoppingListElement>());
        }

        List<List<ShoppingListElement>> newSpinnerList = SharedPreferencesSaver.loadSpinnerL(getPreferences(MODE_PRIVATE));
        List<String> newSpinnerItems = SharedPreferencesSaver.loadSpinnerI(getPreferences(MODE_PRIVATE));

        if (newSpinnerList != null) {
            spinnerList = newSpinnerList;
        }

        if (newSpinnerItems != null) {
            spinnerItems = newSpinnerItems;
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerItems);
        itemSpinner.setAdapter(spinnerAdapter);

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                listAdapter = new ShoppingListAdapter(MainActivity.this, R.layout.row_shopping_list, spinnerList.get(position));
                itemList.setAdapter(listAdapter);
                itemList.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemName.getText() != null && !itemName.getText().toString().trim().isEmpty() && !itemName.getText().toString().equals("Podaj nazwę")) {

                    ShoppingListElement shoppingListElement = new ShoppingListElement(itemName.getText().toString(), Color.BLACK, false);
                    itemName.setText("");

                    spinnerList.get(itemSpinner.getSelectedIndex()).add(shoppingListElement);
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    itemName.setText("");
                } else {
                    itemName.setText("Podaj nazwę");
                    closeKeyboard();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShoppingListElement> selectedView = spinnerList.get(itemSpinner.getSelectedIndex());
                for (int i = 0; i < selectedView.size(); i++) {
                    if (selectedView.get(i).getClassChecked() == true) {
                        selectedView.remove(i);
                        i--;
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        listAdapter = new ShoppingListAdapter(this, R.layout.row_shopping_list, spinnerList.get(0));
        itemList.setAdapter(listAdapter);
    }

    private void closeKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferencesSaver.saveTo(spinnerList, spinnerItems, getPreferences(MODE_PRIVATE));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_new_list) {

            createDialog();
            return true;
        }

        if (id == R.id.action_remove_list) {

            if (!spinnerItems.isEmpty() && itemSpinner.getSelectedIndex() > 0) {
                spinnerItems.remove(itemSpinner.getSelectedIndex());
                spinnerList.remove(itemSpinner.getSelectedIndex());

                itemSpinner.setSelectedIndex(0);
                listAdapter = new ShoppingListAdapter(MainActivity.this, R.layout.row_shopping_list, spinnerList.get(0));
                itemList.setAdapter(listAdapter);

            } else if (!spinnerItems.isEmpty() && itemSpinner.getSelectedIndex() == 0) {

                spinnerList.get(itemSpinner.getSelectedIndex()).clear();
                itemSpinner.setSelectedIndex(0);
                listAdapter = new ShoppingListAdapter(MainActivity.this, R.layout.row_shopping_list, spinnerList.get(0));
                itemList.setAdapter(listAdapter);

                Toast toast = Toast.makeText(this, "Nie można usuwać głównej listy", Toast.LENGTH_LONG);
                View view = toast.getView();

                view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                toast.show();
            }
            listAdapter.notifyDataSetChanged();
            spinnerAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.input_box);
        TextView dialogListNameTV = (TextView) dialog.findViewById(R.id.listNameTextView);
        final EditText dialogListNameET = (EditText) dialog.findViewById(R.id.listNameEditText);

        dialogListNameTV.setText("Nazwa listy");
        dialogListNameTV.setTextColor(Color.WHITE);

        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button saveButton = (Button) dialog.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerItems.add(dialogListNameET.getText().toString());
                spinnerList.add(new ArrayList<ShoppingListElement>());

                int newField = spinnerItems.size() - 1;

                itemSpinner.setSelectedIndex(newField);
                listAdapter = new ShoppingListAdapter(MainActivity.this, R.layout.row_shopping_list, spinnerList.get(newField));
                itemList.setAdapter(listAdapter);

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Dodano nową liste: " + dialogListNameET.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
