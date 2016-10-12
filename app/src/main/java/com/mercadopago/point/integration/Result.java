package com.mercadopago.point.integration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Modified by PabloGallazzi on 10/07/16.
 */

public class Result extends BaseActivity {

    //UI components
    TextView installments;
    TextView amount;
    TextView ccType;
    TextView paymentId;
    TextView error;
    TextView errorDetail;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        setUpViews();

        //You can receive this from an URL or an intent, it depends on how you started the integration!
        Intent launcherIntent = getIntent();
        Bundle data = launcherIntent.getExtras();
        if (data != null) {
            String result = data.getString(BundleCodes.RESULT_STATUS);
            setStatus(result);
            //The use of {@value Constants#RESULT_PAYMENT_ID} is deprecated!
            //paymentId.setText(String.valueOf(data.getLong(Constants.RESULT_PAYMENT_ID)));
            paymentId.setText(String.valueOf(data.getLong(BundleCodes.PAYMENT_ID)));
            installments.setText(String.valueOf(data.getInt(BundleCodes.INSTALLMENTS)));
            amount.setText(String.valueOf(data.getDouble(BundleCodes.AMOUNT)));
            ccType.setText(data.getString(BundleCodes.CARD_TYPE));
            error.setText(data.getString(BundleCodes.ERROR));
            errorDetail.setText(data.getString(BundleCodes.ERROR_DETAIL));
        }

        Uri uri = launcherIntent.getData();
        if (uri != null) {
            String result = uri.getQueryParameter(BundleCodes.RESULT_STATUS);
            setStatus(result);
            //The use of {@value Constants#RESULT_PAYMENT_ID} is deprecated!
            //paymentId.setText(uri.getQueryParameter(Constants.RESULT_PAYMENT_ID));
            paymentId.setText(uri.getQueryParameter(BundleCodes.PAYMENT_ID));
            installments.setText(uri.getQueryParameter(BundleCodes.INSTALLMENTS));
            amount.setText(uri.getQueryParameter(BundleCodes.AMOUNT));
            ccType.setText(uri.getQueryParameter(BundleCodes.CARD_TYPE));
            error.setText(uri.getQueryParameter(BundleCodes.ERROR));
            errorDetail.setText(uri.getQueryParameter(BundleCodes.ERROR_DETAIL));
        }

    }

    private void setStatus(String status) {
        if (Constants.RESULT_STATUS_OK.equals(status)) {
            image.setImageDrawable(ContextCompat.getDrawable(Result.this, R.drawable.ok));
        }
        //This could be an else, it is just to show the other result status available.
        if (Constants.RESULT_STATUS_FAILED.equals(status)) {
            image.setImageDrawable(ContextCompat.getDrawable(Result.this, R.drawable.fail));
        }
    }

    @Override
    protected void setUpViews(){
        image = (ImageView) findViewById(R.id.icon);
        paymentId = (TextView) findViewById(R.id.payment_id);
        installments = (TextView) findViewById(R.id.installments);
        amount = (TextView) findViewById(R.id.amount);
        ccType = (TextView) findViewById(R.id.cc_type);
        error = (TextView) findViewById(R.id.error);
        errorDetail = (TextView) findViewById(R.id.error_detail);
    }
}
