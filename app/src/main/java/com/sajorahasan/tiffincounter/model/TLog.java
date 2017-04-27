package com.sajorahasan.tiffincounter.model;

import io.realm.RealmObject;

/**
 * Created by Sajora on 23-04-2017.
 */

public class TLog extends RealmObject {

    private String date;
    private int quantity;
    private int price;
    private int tiffinPrice;
    private boolean status;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTiffinPrice() {
        return tiffinPrice;
    }

    public void setTiffinPrice(int tiffinPrice) {
        this.tiffinPrice = tiffinPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
