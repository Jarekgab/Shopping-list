package pl.android.jarek.shoppinglist.data;

import android.graphics.Color;

public class ShoppingElement {

    private String name;
    private int color;
    private Boolean selected;

    private int red = Color.RED;
    private int black = Color.BLACK;

    public ShoppingElement(String name, int color, Boolean selected){
        this.name = name;
        this.color = color;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void setSelected(Boolean checked) {
        this.selected = checked;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setRed() {
        this.color = red;
    }

    public void setBlack() {
        this.color = black;
    }

}
