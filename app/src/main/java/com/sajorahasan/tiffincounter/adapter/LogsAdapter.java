package com.sajorahasan.tiffincounter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sajorahasan.tiffincounter.R;
import com.sajorahasan.tiffincounter.model.TLog;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Sajora on 03-02-2017.
 */

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.MyViewHolder> {

    private static final String TAG = LogsAdapter.class.getSimpleName();
    private Context mContext;
    private RealmResults<TLog> mRealmUserList;
    private LayoutInflater mInflater;
    private Realm mRealm;

    public LogsAdapter(Context context, Realm realm, RealmResults<TLog> realmUserList) {

        this.mContext = context;
        this.mRealm = realm;
        this.mRealmUserList = realmUserList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_log, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TLog log = mRealmUserList.get(position);
        holder.txvDate.setText(log.getDate());
        holder.txvQty.setText(" - " + String.valueOf(log.getQuantity() + " qty"));
        holder.txvPrice.setText(" - " + String.valueOf(log.getPrice() + " \u20B9"));

        if (log.isStatus())
            holder.imgStatus.setBackgroundResource(R.drawable.plus);
        else
            holder.imgStatus.setBackgroundResource(R.drawable.minus);

    }

    @Override
    public int getItemCount() {
        return mRealmUserList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txvDate, txvQty, txvPrice;
        private ImageView imgStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            txvDate = (TextView) itemView.findViewById(R.id.textViewDate);
            txvQty = (TextView) itemView.findViewById(R.id.textViewQuantity);
            txvPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
        }
    }
}
