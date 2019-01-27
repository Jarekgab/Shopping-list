package pl.android.jarek.shoppinglist.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import pl.android.jarek.shoppinglist.R;
import pl.android.jarek.shoppinglist.data.ShoppingElement;


public class ShoppingAdapter extends ArrayAdapter {
    private List<ShoppingElement> shoppingElementList;
    private Context context;

    public ShoppingAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ShoppingElement> shoppingElementList) {
        super(context, resource, shoppingElementList);
        this.context = context;
        this.shoppingElementList = shoppingElementList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_shopping_list, parent, false);

        final TextView tvName = rowView.findViewById(R.id.tv_name);
        final LinearLayout rowShoppingListLL = rowView.findViewById(R.id.row_shopping_list_ll);

        tvName.setText(shoppingElementList.get(position).getName());
        tvName.setTextColor(shoppingElementList.get(position).getColor());

        final CheckBox cbSelected = rowView.findViewById(R.id.cb_selected);
        cbSelected.setChecked(shoppingElementList.get(position).getSelected());

                    rowShoppingListLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cbSelected.isChecked() == false){
                                cbSelected.setChecked(true);
                            }else {
                                cbSelected.setChecked(false);
                            }

                            cbSelected.setFocusable(false);

                            if (cbSelected.isChecked() == true) {

                                shoppingElementList.get(position).setRed();
                                tvName.setText(shoppingElementList.get(position).getName());
                                tvName.setTextColor(shoppingElementList.get(position).getColor());

                                shoppingElementList.get(position).setSelected(true);
                                cbSelected.setChecked(shoppingElementList.get(position).getSelected());
                            } else {
                                shoppingElementList.get(position).setBlack();
                                tvName.setText(shoppingElementList.get(position).getName());
                                tvName.setTextColor(shoppingElementList.get(position).getColor());

                                shoppingElementList.get(position).setSelected(false);
                                cbSelected.setChecked(shoppingElementList.get(position).getSelected());
                }
                ShoppingAdapter.super.notifyDataSetChanged();
            }
        });
        return rowView;
    }
}
