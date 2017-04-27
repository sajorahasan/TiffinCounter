package com.sajorahasan.tiffincounter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sajorahasan.tiffincounter.R;
import com.sajorahasan.tiffincounter.adapter.LogsAdapter;
import com.sajorahasan.tiffincounter.model.TLog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView ttlTiffin, ttlAmount;
    private int quantity, totalPrice, tiffinPrice;
    private StepperTouch stepperTouch;

    private RecyclerView recyclerView;
    private LogsAdapter logsAdapter;

    private Realm myRealm;

    public static String formatDate(String inputDate) {
        if (inputDate == null)
            return null;

        try {
            String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            String outputDateFormat = "EEE, d MMM yyyy KK:mm";

            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return inputDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing views
        ttlTiffin = (TextView) findViewById(R.id.ttlTiffin);
        ttlAmount = (TextView) findViewById(R.id.ttlAmount);
        stepperTouch = (StepperTouch) findViewById(R.id.stepperTouch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLogs);

        myRealm = Realm.getDefaultInstance();

        getLogsFromRealm();

        if (tiffinPrice == 0) {
            Toast.makeText(MainActivity.this, "Please set Tiffin price first!", Toast.LENGTH_SHORT).show();
            showAlert();
        }

        if (quantity != 0) {
            stepperTouch.stepper.setValue(quantity);
            ttlTiffin.setText(String.valueOf(quantity));
            ttlAmount.setText(String.valueOf(totalPrice));
        }

        stepperTouch.stepper.setMin(0);
        //stepperTouch.stepper.setMax(30);
        stepperTouch.stepper.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int i, boolean b) {

                if (tiffinPrice != 0) {

                    quantity = i;
                    ttlTiffin.setText(String.valueOf(quantity));
                    totalPrice = quantity * tiffinPrice;
                    ttlAmount.setText(String.valueOf(totalPrice + " \u20B9"));

                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Log.d(TAG, "onStep: " + currentDateTimeString);

                    //Add log to Realm
                    addToRealm(quantity, totalPrice, currentDateTimeString, b);

                } else {
                    totalPrice = 0;
                    Toast.makeText(MainActivity.this, "Please set Tiffin price first!", Toast.LENGTH_SHORT).show();
                    showAlert();
                }
            }
        });

    }

    private void getLogsFromRealm() {
        RealmResults<TLog> logList = myRealm.where(TLog.class).findAll();
        if (logList.size() != 0) {

            //setting up recyclerView
            logsAdapter = new LogsAdapter(this, myRealm, logList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(logsAdapter);

            TLog log = logList.last();
            tiffinPrice = log.getTiffinPrice();
            quantity = log.getQuantity();
            totalPrice = log.getPrice();
            displayQueriedResult(logList);
        }
    }

    private void displayQueriedResult(RealmResults<TLog> logList) {
        StringBuilder builder = new StringBuilder();
        for (TLog log : logList) {
            builder.append("\nTiffinPrice: ").append(log.getTiffinPrice());
            builder.append("\nDate: ").append(log.getDate());
            builder.append("\nQuantity: ").append(log.getQuantity());
            builder.append("\nPrice: ").append(log.getPrice());
        }
        Log.d(TAG, "displayQueriedResult: " + builder.toString());
    }

    private void addToRealm(final int quantity, final int totalPrice, final String currentDateTimeString, final boolean b) {
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TLog log = realm.createObject(TLog.class);
                log.setTiffinPrice(tiffinPrice);
                log.setQuantity(quantity);
                log.setPrice(totalPrice);
                log.setDate(currentDateTimeString);
                log.setStatus(b);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (tiffinPrice != 0) {
            menu.findItem(R.id.action_price).setTitle("Tiffin Price - " + tiffinPrice + " \u20B9");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {

            tiffinPrice = 0;
            quantity = 0;
            totalPrice = 0;
            stepperTouch.stepper.setValue(0);
            ttlTiffin.setText(String.valueOf(quantity));
            ttlAmount.setText(String.valueOf(totalPrice + " \u20B9"));

//            pref.edit().putString("price", null)
//                    .putString("qty", null)
//                    .putString("totalPrice", null)
//                    .apply();

            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TLog> logList = realm.where(TLog.class).findAll();
                    logList.deleteAllFromRealm();
                }
            });

            return true;
        }

        if (id == R.id.action_price) {
            showAlert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlert() {
        AlertDialog.Builder alertDialogUsername = new AlertDialog.Builder(this);
        alertDialogUsername.setMessage("Set Tiffin Price");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(tiffinPrice));
        alertDialogUsername.setView(input);

        alertDialogUsername.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                tiffinPrice = Integer.parseInt(input.getText().toString().trim());
                saveTiffinPrice();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialogUsername.show();
    }

    private void saveTiffinPrice() {
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TLog log = realm.createObject(TLog.class);
                log.setTiffinPrice(tiffinPrice);
                Toast.makeText(MainActivity.this, "Tiffin Price set!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (logsAdapter != null)
            logsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (userList != null)
//            userList.removeChangeListener(userListListner);
//        //userList.removeAllChangeListeners();
//
//        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
//            realmAsyncTask.cancel();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }
}
