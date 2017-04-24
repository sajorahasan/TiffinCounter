package com.sajorahasan.tiffincounter;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView ttlTiffin, ttlAmount;
    private String tiffinPrice, totalPrice, quantity;
    private StepperTouch stepperTouch;
    //private Menu menu;
    private SharedPreferences pref;

    private ListView listView;
    private LogsAdapter logsAdapter;
    private List<TLog> tLogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing views
        listView = (ListView) findViewById(R.id.lvLogs);
        ttlTiffin = (TextView) findViewById(R.id.ttlTiffin);
        ttlAmount = (TextView) findViewById(R.id.ttlAmount);
        stepperTouch = (StepperTouch) findViewById(R.id.stepperTouch);

        tLogList = new ArrayList<>();
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        quantity = pref.getString("qty", null);
        totalPrice = pref.getString("totalPrice", null);
        tiffinPrice = pref.getString("price", null);

        if (tiffinPrice == null) {
            Toast.makeText(MainActivity.this, "Please set Tiffin price first!", Toast.LENGTH_SHORT).show();
            showAlert();
        }

        if (quantity != null) {
            stepperTouch.stepper.setValue(Integer.parseInt(quantity));
            ttlTiffin.setText(quantity);
            ttlAmount.setText(totalPrice);
        }

        stepperTouch.stepper.setMin(0);
        //stepperTouch.stepper.setMax(30);
        stepperTouch.stepper.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int i, boolean b) {

                if (tiffinPrice != null && totalPrice != null) {

                    quantity = String.valueOf(i);
                    ttlTiffin.setText(quantity);
                    totalPrice = String.valueOf(Integer.parseInt(quantity) * Integer.parseInt(tiffinPrice));
                    ttlAmount.setText(totalPrice + " \u20B9");

                    pref.edit().putString("qty", quantity).apply();
                    pref.edit().putString("totalPrice", totalPrice).apply();

                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Log.d(TAG, "onStep: " + currentDateTimeString);

                    TLog tLog = new TLog(currentDateTimeString, i, totalPrice, b);
                    tLogList.add(tLog);
                    logsAdapter = new LogsAdapter(MainActivity.this, R.layout.item_log, tLogList);
                    listView.setAdapter(logsAdapter);

                } else {
                    totalPrice = "0";
                    Toast.makeText(MainActivity.this, "Please set Tiffin price first!", Toast.LENGTH_SHORT).show();
                    showAlert();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (tiffinPrice != null) {
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

            tiffinPrice = null;
            quantity = null;
            totalPrice = null;
            stepperTouch.stepper.setValue(0);
            ttlTiffin.setText("0");
            ttlAmount.setText("0 \u20B9");

            pref.edit().putString("price", null)
                    .putString("qty", null)
                    .putString("totalPrice", null)
                    .apply();

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
        input.setText(tiffinPrice);
        alertDialogUsername.setView(input);

        alertDialogUsername.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                tiffinPrice = input.getText().toString().trim();
                pref.edit().putString("price", tiffinPrice).apply();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialogUsername.show();
    }

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
}
