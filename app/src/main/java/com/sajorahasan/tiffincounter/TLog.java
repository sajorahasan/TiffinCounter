package com.sajorahasan.tiffincounter;

/**
 * Created by Sajora on 23-04-2017.
 */

public class TLog {
    private String date;
    private int quantity;
    private String price;
    private boolean status;

    public TLog(String date, int quantity, String price, boolean status) {
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public boolean isStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }
}
