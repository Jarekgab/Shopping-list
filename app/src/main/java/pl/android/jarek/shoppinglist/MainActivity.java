package pl.android.jarek.shoppinglist;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import pl.android.jarek.shoppinglist.adapter.ShoppingAdapter;
import pl.android.jarek.shoppinglist.data.ShoppingElement;
import pl.android.jarek.shoppinglist.util.SharedPreferencesSaver;


public class MainActivity extends AppCompatActivity {

    ListView lvShoppingList;
    NiceSpinner nsList;
    ImageButton ibDelete;
    BootstrapEditText etElementName;
    Button bAddElement;

    private List<String> spinnerList;
    private List<List<ShoppingElement>> shoppingElementList;

    private ArrayAdapter<String> spinnerAdapter;
    private ShoppingAdapter shoppingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvShoppingList = findViewById(R.id.lv_shopping_list);
        nsList = findViewById(R.id.ns_list);
        ibDelete = findViewById(R.id.ib_delete);
        etElementName = findViewById(R.id.et_element_name);
        bAddElement = findViewById(R.id.b_add_element);

        spinnerList = new ArrayList<>();

        if (spinnerList.isEmpty()) {
            spinnerList.add("Lista główna");
            shoppingElementList = new ArrayList<>();
            shoppingElementList.add(new ArrayList<ShoppingElement>());
        }

        List<List<ShoppingElement>> newShoppingElementList = SharedPreferencesSaver.loadShoppingElementList(getPreferences(MODE_PRIVATE));
        List<String> newSpinnerList = SharedPreferencesSaver.loadSpinnerList(getPreferences(MODE_PRIVATE));

        if (newShoppingElementList != null) {
            shoppingElementList = newShoppingElementList;
        }

        if (newSpinnerList != null) {
            spinnerList = newSpinnerList;
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerList);
        nsList.setAdapter(spinnerAdapter);

        nsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                shoppingAdapter = new ShoppingAdapter(MainActivity.this, R.layout.row_shopping_list, shoppingElementList.get(position));
                lvShoppingList.setAdapter(shoppingAdapter);
                lvShoppingList.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etElementName.getText() != null && !etElementName.getText().toString().trim().isEmpty() && !etElementName.getText().toString().equals("Podaj nazwę")) {

                    ShoppingElement shoppingElement = new ShoppingElement(etElementName.getText().toString(), Color.BLACK, false);
                    etElementName.setText("");

                    shoppingElementList.get(nsList.getSelectedIndex()).add(shoppingElement);
                    shoppingAdapter.notifyDataSetChanged();
                }
            }
        });

        etElementName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    etElementName.setText("");
                } else {
                    etElementName.setText("Podaj nazwę");
                    closeKeyboard();
                }
            }
        });

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShoppingElement> selectedView = shoppingElementList.get(nsList.getSelectedIndex());
                for (int i = 0; i < selectedView.size(); i++) {
                    if (selectedView.get(i).getSelected() == true) {
                        selectedView.remove(i);
                        i--;
                    }
                }
                shoppingAdapter.notifyDataSetChanged();
            }
        });

        shoppingAdapter = new ShoppingAdapter(this, R.layout.row_shopping_list, shoppingElementList.get(0));
        lvShoppingList.setAdapter(shoppingAdapter);
    }

    private void closeKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferencesSaver.saveTo(shoppingElementList, spinnerList, getPreferences(MODE_PRIVATE));
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

            if (!spinnerList.isEmpty() && nsList.getSelectedIndex() > 0) {
                spinnerList.remove(nsList.getSelectedIndex());
                shoppingElementList.remove(nsList.getSelectedIndex());

                nsList.setSelectedIndex(0);
                shoppingAdapter = new ShoppingAdapter(MainActivity.this, R.layout.row_shopping_list, shoppingElementList.get(0));
                lvShoppingList.setAdapter(shoppingAdapter);

            } else if (!spinnerList.isEmpty() && nsList.getSelectedIndex() == 0) {

                shoppingElementList.get(nsList.getSelectedIndex()).clear();
                nsList.setSelectedIndex(0);
                shoppingAdapter = new ShoppingAdapter(MainActivity.this, R.layout.row_shopping_list, shoppingElementList.get(0));
                lvShoppingList.setAdapter(shoppingAdapter);

                Toast toast = Toast.makeText(this, "Nie można usuwać głównej listy", Toast.LENGTH_LONG);
                View view = toast.getView();

                view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                toast.show();
            }
            shoppingAdapter.notifyDataSetChanged();
            spinnerAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.input_box);
        TextView tvDialog = (TextView) dialog.findViewById(R.id.tv_dialog);
        final EditText etListName = (EditText) dialog.findViewById(R.id.et_list_name);

        tvDialog.setText("Nazwa listy");
        tvDialog.setTextColor(Color.WHITE);

        Button bCancel = (Button) dialog.findViewById(R.id.b_cancel);
        Button bAdd = (Button) dialog.findViewById(R.id.b_add);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerList.add(etListName.getText().toString());
                shoppingElementList.add(new ArrayList<ShoppingElement>());

                int newField = spinnerList.size() - 1;

                nsList.setSelectedIndex(newField);
                shoppingAdapter = new ShoppingAdapter(MainActivity.this, R.layout.row_shopping_list, shoppingElementList.get(newField));
                lvShoppingList.setAdapter(shoppingAdapter);

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Dodano nową liste: " + etListName.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }
}
