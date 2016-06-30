package com.mercadopago.point.integration.basic;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mercadopago.point.integration.R;

import java.util.List;

public class Main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final int PAYMENT_REQUEST = 1;

    EditText reference;
    EditText amount;
    EditText installments;
    FloatingActionButton go_bundle;
    FloatingActionButton go_url;

    String cc_selected;
    String appId = "YOUR_APP_ID";
    String appSecret = "YOUR_APP_SECRET";
    double appFee = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = (EditText) findViewById(R.id.reference);
        amount = (EditText) findViewById(R.id.amount);

        final Spinner spinner = (Spinner) findViewById(R.id.debit_credit);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cc_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        installments = (EditText) findViewById(R.id.installments);

        go_bundle = (FloatingActionButton) findViewById(R.id.go_bundle);
        go_bundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction("com.mercadopago.PAYMENT_ACTION");
                Bundle bundle = new Bundle();
                // AppId
                bundle.putString(BundleCodes.APP_ID, appId);
                bundle.putString(BundleCodes.APP_SECRET, appSecret);
                bundle.putDouble(BundleCodes.APP_FEE, appFee);
                // Amount of transaction
                bundle.putDouble(BundleCodes.AMOUNT, Double.valueOf(amount.getText().toString()));
                // Description of transaction
                bundle.putString(BundleCodes.DESCRIPTION, reference.getText().toString());
                if (spinner.getSelectedItemPosition() == 0) {
                    cc_selected = "credit_card";
                } else {
                    cc_selected = "debit_card";
                }
                // Payment type of transaction ( credit_card | debit_card  )
                bundle.putString(BundleCodes.CARD_TYPE, cc_selected);
                // # of installments
                bundle.putInt(BundleCodes.INSTALLMENTS, Integer.valueOf(installments.getText().toString()));
                // Before we can start the intent, we should check if this phone handle the intent?
                if (isAvailable(i)) {
                    // start activity for result
                    i.putExtras(bundle);
                    startActivityForResult(i, PAYMENT_REQUEST);
                } else {
                    // send to google play.
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                }

            }
        });

        go_url = (FloatingActionButton) findViewById(R.id.go_url);
        go_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri.Builder builder = Uri.parse("https://secure.mlstatic.com/org-img/point/app/index.html")
                        .buildUpon();
                // Amount of transaction
                builder.appendQueryParameter(BundleCodes.AMOUNT, amount.getText().toString());

                builder.appendQueryParameter(BundleCodes.APP_ID, appId);
                builder.appendQueryParameter(BundleCodes.APP_SECRET, appSecret);
                builder.appendQueryParameter(BundleCodes.APP_FEE, String.valueOf(appFee));


                // Description of transaction
                builder.appendQueryParameter(BundleCodes.DESCRIPTION, reference.getText().toString());
                if (spinner.getSelectedItemPosition() == 0) {
                    cc_selected = "credit_card";
                } else {
                    cc_selected = "debit_card";
                }
                // Payment type of transaction ( credit_card | debit_card  )
                builder.appendQueryParameter(BundleCodes.CARD_TYPE, cc_selected);
                // # of installments
                builder.appendQueryParameter(BundleCodes.INSTALLMENTS, installments.getText().toString());

                builder.appendQueryParameter(BundleCodes.URL_SUCCESS,"demo://www.pointh.com");
                builder.appendQueryParameter(BundleCodes.URL_FAIL,"demo://www.pointh.com");

                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(builder.build());

                // Before we can start the intent, we should check if this phone handle the intent?
                if (isAvailable(i)) {
                    // start activity for result
                    startActivity(i);
                } else {
                    // send to google play.
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                }

            }
        });


    }

    public static Integer MSG_PRINT_RESULTS = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_PRINT_RESULTS) {
                Intent intent = new Intent(Main.this, Resultado.class);
                intent.putExtras((Bundle) msg.obj);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    };

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

    }

    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_REQUEST && data != null) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_PRINT_RESULTS;
            msg.obj = data.getExtras();
            handler.sendMessageDelayed(msg, 1000);
        }

    }

    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
