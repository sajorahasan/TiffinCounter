package com.sajorahasan.tiffincounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sajora on 03-02-2017.
 */

public class LogsAdapter extends ArrayAdapter<TLog> {

    //storing all the names in the list
    private List<TLog> tLogList;

    //context object
    private Context context;

    //constructor
    public LogsAdapter(Context context, int resource, List<TLog> tLogList) {
        super(context, resource, tLogList);
        this.context = context;
        this.tLogList = tLogList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutInflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listView itmes
        View listViewItem = inflater.inflate(R.layout.item_log, null, true);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        TextView textViewQty = (TextView) listViewItem.findViewById(R.id.textViewQuantity);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        TLog tLog = tLogList.get(position);

        //setting the value to textView
        textViewDate.setText(tLog.getDate());
        textViewQty.setText(" - " + String.valueOf(tLog.getQuantity() + " qty"));
        textViewPrice.setText(" - " + String.valueOf(tLog.getPrice() + " \u20B9"));

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (tLog.isStatus())
            imageViewStatus.setBackgroundResource(R.drawable.plus);
        else
            imageViewStatus.setBackgroundResource(R.drawable.minus);

        return listViewItem;
    }
}
