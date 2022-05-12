package com.mercadopago.point.integration;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

/**
 * Created by sebad on 3/20/15.
 * Modified by PabloGallazzi on 10/07/16.
 */

public class Main extends BaseActivity implements AdapterView.OnItemSelectedListener {

    public static final int PAYMENT_REQUEST = 1;

    //UI components.
    EditText description;
    EditText externalReference;
    EditText notificationURL;
    EditText amount;
    EditText installments;
    EditText sponsor;
    //Variables that should be used all together.
    EditText appId;
    EditText appSecret;
    EditText appFee;
    EditText payerEmail;
    EditText payerIdentification;
    EditText collectorId;
    Switch isKiosk;
    Spinner spinner;
    FloatingActionButton go_bundle;
    FloatingActionButton go_url;

    MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();

        IntentFilter filter = new IntentFilter("com.mercadopago.merchant.PAYMENT_STATUS");
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        go_bundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a new intent.
                Intent i = new Intent();

                //Set the correct intent's action.
                i.setAction(Constants.ACTION);

                //Crate a new bundle to pass information about the payment.
                Bundle bundle = new Bundle();

                if (!appId.getText().toString().trim().isEmpty() && appSecret.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "If you send an app_id you must send an app_secret",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appSecret.getText().toString().trim().isEmpty() && appId.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "If you send an app_secret you must send an app_id",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appFee.getText().toString().trim().isEmpty() && appId.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                        "If you send an ap_fee you must send an app_id and an app_secret",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appId.getText().toString().trim().isEmpty() && !appSecret.getText().toString().trim().isEmpty()) {
                    bundle.putString(BundleCodes.APP_ID, appId.getText().toString().trim());
                    bundle.putString(BundleCodes.APP_SECRET, appSecret.getText().toString().trim());
                }

                if (!appFee.getText().toString().trim().isEmpty()) {
                    //This should only be provided if BOTH app_id and app_secret are provided.
                    bundle.putDouble(BundleCodes.APP_FEE, Double.valueOf(appFee.getText().toString().trim()));
                }

                if (amount.getText().toString().trim().isEmpty() || description.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both amount and description are mandatory",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                //Sets the transaction amount MUST BE PROVIDED.
                bundle.putDouble(BundleCodes.AMOUNT, Double.valueOf(amount.getText().toString()));

                //Sets the description for the payment MUST BE PROVIDED.
                bundle.putString(BundleCodes.DESCRIPTION, description.getText().toString());

                //Sets whether the back button is allowed or not, the default value is true.
                bundle.putBoolean(BundleCodes.DISABLE_BACK_BUTTON, true);

                //Get the selected card type from the UI
                String selectedCardType = getCardTypeFromSpinner();

                //Sets the transaction's card type.
                bundle.putString(BundleCodes.CARD_TYPE, selectedCardType);

                //Sets the number of installments, for debit card must be 1.
                if (getInstallments() != null) {
                    bundle.putLong(BundleCodes.INSTALLMENTS, Long.valueOf(getInstallments()));
                }

                //Sets the sponsor_id.
                if (getSponsorId() != null) {
                    bundle.putLong(BundleCodes.SPONSOR_ID, Long.valueOf(getSponsorId()));
                }

                //Sets the notification_url.
                if (getNotificationUrl() != null) {
                    bundle.putString(BundleCodes.NOTIFICATION_URL, getNotificationUrl());
                }

                //Sets the external_reference.
                if (getExternalReference() != null) {
                    bundle.putString(BundleCodes.EXTERNAL_REFERENCE, getExternalReference());
                }

                //Sets the payer_email.
                if (getPayerEmail() != null) {
                    bundle.putString(BundleCodes.PAYER_EMAIL, getPayerEmail());
                }

                //Sets the payer_identification.
                if (getPayerIdentification() != null) {
                    bundle.putLong(BundleCodes.IDENTIFICATION, getPayerIdentification());
                }

                //Sets the collector id to be used.
                if (getCollector() != null) {
                    bundle.putLong(BundleCodes.COLLECTOR_ID, getCollector());
                }

                //Sets is kiosk mode enabled.
                if (isKiosk.isChecked()) {
                    bundle.putBoolean(BundleCodes.IS_KIOSK, isKiosk.isChecked());
                }

                //Before we can call the intent, we should check if this phone can handle the intent.
                if (true) {
                    //Start activity for result.
                    i.putExtras(bundle);
                    startActivityForResult(i, PAYMENT_REQUEST);
                } else {
                    //Send to google play.
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + Constants.POINT_PACKAGE)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + Constants.POINT_PACKAGE)));
                    }
                }
            }
        });

        go_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get a new builder for an url call.
                Uri.Builder builder = Uri.parse(Constants.LINK).buildUpon();

                if (!appId.getText().toString().trim().isEmpty() && appSecret.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "If you send an app_id you must send an app_secret",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appSecret.getText().toString().trim().isEmpty() && appId.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "If you send an app_secret you must send an app_id",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appFee.getText().toString().trim().isEmpty() && appId.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                        "If you send an ap_fee you must send an app_id and an app_secret",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                if (!appId.getText().toString().trim().isEmpty() && !appSecret.getText().toString().trim().isEmpty()) {
                    builder.appendQueryParameter(BundleCodes.APP_ID, appId.getText().toString().trim());
                    builder.appendQueryParameter(BundleCodes.APP_SECRET, appSecret.getText().toString().trim());
                }

                if (!appFee.getText().toString().trim().isEmpty()) {
                    //This should only be provided if BOTH app_id and app_secret are provided.
                    builder
                        .appendQueryParameter(BundleCodes.APP_FEE, String.valueOf(appFee.getText().toString().trim()));
                }

                if (amount.getText().toString().trim().isEmpty() || description.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both amount and description are mandatory",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                //Sets the transaction amount MUST BE PROVIDED.
                builder.appendQueryParameter(BundleCodes.AMOUNT, amount.getText().toString());

                //Sets the description for the payment MUST BE PROVIDED.
                builder.appendQueryParameter(BundleCodes.DESCRIPTION, description.getText().toString());

                //Sets whether the back button is allowed or not, the default value is true.
                builder.appendQueryParameter(BundleCodes.DISABLE_BACK_BUTTON, "true");

                //Get the selected card type from the UI
                String selectedCardType = getCardTypeFromSpinner();

                //Sets the transaction's card type.
                builder.appendQueryParameter(BundleCodes.CARD_TYPE, selectedCardType);

                //Sets the number of installments, for debit card must be 1.
                if (getInstallments() != null) {
                    builder.appendQueryParameter(BundleCodes.INSTALLMENTS, getInstallments());
                }

                //Sets the sponsor_id.
                if (getSponsorId() != null) {
                    builder.appendQueryParameter(BundleCodes.SPONSOR_ID, getSponsorId());
                }

                //Sets the notification_url.
                if (getNotificationUrl() != null) {
                    builder.appendQueryParameter(BundleCodes.NOTIFICATION_URL, getNotificationUrl());
                }

                //Sets the external_reference.
                if (getExternalReference() != null) {
                    builder.appendQueryParameter(BundleCodes.EXTERNAL_REFERENCE, getExternalReference());
                }

                //Sets the payer_email.
                if (getPayerEmail() != null) {
                    builder.appendQueryParameter(BundleCodes.PAYER_EMAIL, getPayerEmail());
                }

                //Sets the payer_identification.
                if (getPayerIdentification() != null) {
                    builder.appendQueryParameter(BundleCodes.IDENTIFICATION, getPayerIdentification().toString());
                }

                //Sets the collector id to be used.
                if (getCollector() != null) {
                    builder.appendQueryParameter(BundleCodes.COLLECTOR_ID, getCollector().toString());
                }

                //Sets is kiosk mode enabled.
                if (isKiosk.isChecked()){
                    builder.appendQueryParameter(BundleCodes.IS_KIOSK, String.valueOf(isKiosk.isChecked()));
                }

                //Sets the callback url's THIS TWO MUST BE PROVIDED.
                builder.appendQueryParameter(BundleCodes.URL_SUCCESS, "demo://www.pointh.com");
                builder.appendQueryParameter(BundleCodes.URL_FAIL, "demo://www.pointh.com");

                //Create a new intent and set the correct action and builder.
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(builder.build());

                //Before we can call the intent, we should check if this phone can handle the intent.
                if (true) {
                    //Start activity for result.
                    startActivity(i);
                } else {
                    //Send to google play.
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + Constants.POINT_PACKAGE)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + Constants.POINT_PACKAGE)));
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
                Intent intent = new Intent(Main.this, Result.class);
                intent.putExtras((Bundle) msg.obj);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    };

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //Nothing to do...
    }

    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        return true;
    }

    @Nullable
    private String getInstallments() {
        final String sponsorString = installments.getText().toString();
        if (sponsorString.isEmpty()) {
            return null;
        } else {
            return sponsorString;
        }
    }

    @Nullable
    private String getSponsorId() {
        final String sponsorString = sponsor.getText().toString();
        if (sponsorString.trim().isEmpty()) {
            return null;
        } else {
            return sponsorString;
        }
    }

    @Nullable
    private String getNotificationUrl() {
        notificationURL.setText("https://b9ddb180-9043-47a5-8588-5ad1fa67e928.trayapp.io/");
        if (notificationURL.getText().toString().trim().isEmpty()) {
            return null;
        } else {
            return notificationURL.getText().toString();
        }
        //return "https://www.algo.com/url";
    }

    @Nullable
    private String getExternalReference() {
        if (externalReference.getText().toString().trim().isEmpty()) {
            return null;
        } else {
            return externalReference.getText().toString();
        }
        //return "Un external description de hasta 255 caracteres";
    }

    @Nullable
    private Long getCollector() {
        if (collectorId.getText().toString().trim().isEmpty()) {
            return null;
        } else {
            return Long.valueOf(collectorId.getText().toString());
        }
        //return "Un collector del tipo long";
    }

    @Nullable
    private String getPayerEmail() {
        if (payerEmail.getText().toString().trim().isEmpty()) {
            return null;
        } else {
            if (isValidEmail(payerEmail.getText().toString())) {
                return payerEmail.getText().toString();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid email, parameter not sent",
                    Toast.LENGTH_LONG).show();
                return null;
            }
        }
        //return "unmail@gmail.com";
    }

    @Nullable
    private Long getPayerIdentification() {
        if (payerIdentification.getText().toString().trim().isEmpty()) {
            return null;
        } else {
            try {
                if (isValidIdentification(Long.valueOf(payerIdentification.getText().toString()))) {
                    return Long.valueOf(payerIdentification.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid identification, parameter not sent",
                        Toast.LENGTH_LONG).show();
                    return null;
                }
            } catch (final NumberFormatException ignored) {
                return null;
            }
        }
        //return 36363636L;
    }

    private static boolean isValidEmail(final CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static boolean isValidIdentification(final Long target) {
        return target != null && target >= 1000000 && target <= 999999999;
    }

    private String getCardTypeFromSpinner() {
        String type = null;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                type = Constants.CREDIT_CARD;
                break;
            case 1:
                type = Constants.DEBIT_CARD;
                break;
            default:
                break;
        }
        return type;
    }

    @Override
    protected void setUpViews() {
        description = (EditText) findViewById(R.id.description);
        externalReference = (EditText) findViewById(R.id.external_reference);
        notificationURL = (EditText) findViewById(R.id.notification_url);
        amount = (EditText) findViewById(R.id.amount);
        installments = (EditText) findViewById(R.id.installments);
        sponsor = (EditText) findViewById(R.id.sponsor);
        spinner = (Spinner) findViewById(R.id.debit_credit);
        payerEmail = (EditText) findViewById(R.id.payer_email);
        collectorId = (EditText) findViewById(R.id.collector_id);
        isKiosk = (Switch) findViewById(R.id.is_kiosk);
        payerIdentification = (EditText) findViewById(R.id.payer_identification);
        go_bundle = (FloatingActionButton) findViewById(R.id.go_bundle);
        appId = (EditText) findViewById(R.id.app_id);
        appSecret = (EditText) findViewById(R.id.app_secret);
        appFee = (EditText) findViewById(R.id.app_fee);
        go_url = (FloatingActionButton) findViewById(R.id.go_url);
        //Set up the spinner...
        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(this, R.array.cc_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //You will receive the status -> PROCESSING when a payment is initiated...
            Log.d("Payment status", intent.getStringExtra(BundleCodes.STATUS));
        }
    }
}
