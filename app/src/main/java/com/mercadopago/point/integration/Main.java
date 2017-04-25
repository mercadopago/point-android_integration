package com.mercadopago.point.integration;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by sebad on 3/20/15.
 * Modified by PabloGallazzi on 10/07/16.
 */

public class Main extends BaseActivity implements AdapterView.OnItemSelectedListener {

    public static final int PAYMENT_REQUEST = 1;

    //UI components.
    EditText reference;
    EditText amount;
    EditText installments;
    EditText sponsor;
    Spinner spinner;
    FloatingActionButton go_bundle;
    FloatingActionButton go_url;
    FloatingActionButton go_operations_detail;

    //Variables that should be used all together.
    String appId = "YOUR_APP_ID";
    String appSecret = "YOUR_APP_SECRET";
    double appFee = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();

        go_operations_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("point://operation_detail?payment_id=2583218915")));
            }
        });

        go_bundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a new intent.
                Intent i = new Intent();

                //Set the correct intent's action.
                i.setAction(Constants.ACTION);

                //Crate a new bundle to pass information about the payment.
                Bundle bundle = new Bundle();

                /*
                //Sets the app_id and app_secret either BOTH or NONE must be provided.
                bundle.putString(BundleCodes.APP_ID, appId);
                bundle.putString(BundleCodes.APP_SECRET, appSecret);
                //This should only be provided if BOTH app_id and app_secret are provided.
                bundle.putDouble(BundleCodes.APP_FEE, appFee);
                */

                //Sets the transaction amount MUST BE PROVIDED.
                bundle.putDouble(BundleCodes.AMOUNT, Double.valueOf(amount.getText().toString()));

                //Sets the description for the payment.
                bundle.putString(BundleCodes.DESCRIPTION, reference.getText().toString());

                //Sets whether the back button is allowed or not, the default value is true.
                bundle.putBoolean(BundleCodes.DISABLE_BACK_BUTTON, true);

                //Get the selected card type from the UI
                String selectedCardType = getCardTypeFromSpinner();

                //Sets the transaction's card type.
                bundle.putString(BundleCodes.CARD_TYPE, selectedCardType);

                //Sets the number of installments, for debit card must be 1.
                if (getInstallments() != null) {
                    bundle.putInt(BundleCodes.INSTALLMENTS, Integer.valueOf(getInstallments()));
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

                //Before we can call the intent, we should check if this phone can handle the intent.
                if (isAvailable(i)) {
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

                /*
                //Sets the app_id and app_secret either BOTH or NONE must be provided.
                builder.appendQueryParameter(BundleCodes.APP_ID, appId);
                builder.appendQueryParameter(BundleCodes.APP_SECRET, appSecret);
                //This should only be provided if BOTH app_id and app_secret are provided.
                builder.appendQueryParameter(BundleCodes.APP_FEE, String.valueOf(appFee));
                */

                //Sets the transaction amount MUST BE PROVIDED.
                builder.appendQueryParameter(BundleCodes.AMOUNT, amount.getText().toString());

                //Sets the description for the payment.
                builder.appendQueryParameter(BundleCodes.DESCRIPTION, reference.getText().toString());

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

                //Sets the callback url's THIS TWO MUST BE PROVIDED.
                builder.appendQueryParameter(BundleCodes.URL_SUCCESS, "demo://www.pointh.com");
                builder.appendQueryParameter(BundleCodes.URL_FAIL, "demo://www.pointh.com");

                //Create a new intent and set the correct action and builder.
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(builder.build());

                //Before we can call the intent, we should check if this phone can handle the intent.
                if (isAvailable(i)) {
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
        if (sponsorString.isEmpty()) {
            return null;
        } else {
            return sponsorString;
        }
    }

    @Nullable
    private String getNotificationUrl() {
        return null;
        //return "https://www.algo.com/url";
    }

    @Nullable
    private String getExternalReference() {
        return null;
        //return "Un external reference de hasta 255 caracteres";
    }

    @Nullable
    private String getPayerEmail() {
        return null;
        //return "unmail@gmail.com";
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
        reference = (EditText) findViewById(R.id.reference);
        amount = (EditText) findViewById(R.id.amount);
        installments = (EditText) findViewById(R.id.installments);
        sponsor = (EditText) findViewById(R.id.sponsor);
        spinner = (Spinner) findViewById(R.id.debit_credit);
        go_bundle = (FloatingActionButton) findViewById(R.id.go_bundle);
        go_url = (FloatingActionButton) findViewById(R.id.go_url);
        go_operations_detail = (FloatingActionButton) findViewById(R.id.go_operations_detail);
        //Set up the spinner...
        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(this, R.array.cc_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
