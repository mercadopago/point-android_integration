
# MP Mobile Android Seller Integration

The purpose of this app is to show the integration with  MP Mobile Android Seller.
[![Download it or see some application screenshots directly from the Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png "Download it or see some application screenshots directly from the Google Play")](https://play.google.com/store/apps/details?id=com.mercadopago.merchant)

## How to:

There are 2 kinds of integration Intent and Deep Linking.

### Intent:


For the Intent integration you have to create a [implicit intent](http://developer.android.com/intl/es/guide/components/intents-filters.html#ExampleSend) and start it with the method [startActivityForResult]('http://developer.android.com/intl/es/reference/android/app/Activity.html#startActivityForResult(android.content.Intent, int)') as follows:

```java

     Intent i = new Intent();
                    i.setAction("com.mercadopago.PAYMENT_ACTION");
                    Bundle bundle = new Bundle();
                    // AppId
                    bundle.putString(BundleCodes.APP_ID, appId);
                    // Secret
                    bundle.putString(BundleCodes.APP_SECRET, appSecret);
                    // App Fee
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

```

And them when the payment is done, point will notify a intent as follows:

in the calling activity:

```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_REQUEST && data != null) {
            Intent intent = new Intent(this, Resultado.class);
            intent.putExtras(data.getExtras());
            startActivity(intent);
        }

    }
```

the result activity that shows the results:

```java
public class Resultado extends AppCompatActivity {

    TextView installments;
    TextView amount;
    TextView ccType;
    TextView paymentId;
    ImageView image;
    View layoutView;

    // it could be

    public final static String RESULT_PAYMENT_ID = "paymentId";

    public final static String RESULT_STATUS_OK = "OK";
    public final static String RESULT_STATUS_FAILED = "FAILED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        image = (ImageView) findViewById(R.id.icon);
        layoutView = findViewById(R.id.payment_id_lo);
        paymentId = (TextView) findViewById(R.id.payment_id);
        installments = (TextView) findViewById(R.id.installments);
        amount = (TextView) findViewById(R.id.amount);
        ccType = (TextView) findViewById(R.id.cc_type);



        Intent launcherIntent = getIntent();
        Bundle data = launcherIntent.getExtras();
        if (data != null) {
            String result = data.getString(BundleCodes.RESULT_STATUS);
            setStatus(result);
            paymentId.setText(String.valueOf(data.getLong(RESULT_PAYMENT_ID)));
            installments.setText(String.valueOf(data.getInt(BundleCodes.INSTALLMENTS)));
            amount.setText(String.valueOf(data.getDouble(BundleCodes.AMOUNT)));
            ccType.setText(data.getString(BundleCodes.CARD_TYPE));
        }

        Uri uri = launcherIntent.getData();
        if (uri != null) {
            String result = uri.getQueryParameter(BundleCodes.RESULT_STATUS);
            setStatus(result);
            paymentId.setText(uri.getQueryParameter(RESULT_PAYMENT_ID));
            installments.setText(uri.getQueryParameter(BundleCodes.INSTALLMENTS));
            amount.setText(uri.getQueryParameter(BundleCodes.AMOUNT));
            ccType.setText(uri.getQueryParameter(BundleCodes.CARD_TYPE));
        }

    }

    private void setStatus(String status) {
        if (RESULT_STATUS_OK.equals(status)) {
            if (android.os.Build.VERSION.SDK_INT >= 22) {
                image.setImageDrawable(getDrawable(R.drawable.ok));
            } else {
                image.setImageDrawable(getResources().getDrawable(R.drawable.ok));
            }
            // Show the payment id
            layoutView.setVisibility(View.VISIBLE);
        }
        if (RESULT_STATUS_FAILED.equals(status)) {
            if (android.os.Build.VERSION.SDK_INT >= 22) {
                image.setImageDrawable(getDrawable(R.drawable.fail));
            } else {
                image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
            }
            layoutView.setVisibility(View.GONE);
            // Hide the payment id
            layoutView.setVisibility(View.VISIBLE);
        }
    }
}

```
### Deep Linking:

With URL you call mercadopago by [Deep Linking](http://developer.android.com/intl/es/training/app-indexing/deep-linking.html) 
with the following URL :  https://secure.mlstatic.com/org-img/point/app/index.html  

|   Variable    |   Required?   | Description  |
| ------------- |:-------------:| :------------|
| amount        | yes           | The amount to be charged        |
| description   | yes           | The description of the charge.  |
| card_type     | no            | The card type to use, only credit_card or debit_card are valid values |
| client_id     | no            | The client id to be used to log in. |
| client_secret  | no 1.         | The client secret of the app. |
| application_fee | no 2.          | The fee charged by the app. |
| success_url     | no          | The url to call when the payment has been done successfully, if none is provided we show the default point screen.  |
| fail_url     | no          | The url to call when the payment has not been done successfully, if none is provided we show the default point screen. |

> 1. Its required if you provide a client_id
> 2. If you provide application_fee , you must provide client_id & client_secret


## Results

These are the results you can get from Point appplication, ther are the same in both cases.

|  Variable / Query |    Description        |   Notes                                |
| -------------     | :------------         |              :------------             | 
| amount            | The amount charged    |                                        |
| result_status     | The payment's status  | Values "OK"  or "FAILED"               |
| payment_id        | The payment's id      | only in sucessfull payments            |
| card_type         | the card type         | Values "credit_card"  or "debit_card"  |
| installments      | the installments      |                                        |
| error      | error reason      |    Only in "FAILED"  transactions             |
| error_detail      | error details    |     Only in "FAILED"  transactions     |


## Repo usage
*Do not clone or push to master branch.*

Create branch or fork from *develop*, then push or create pull requests (if you don't have access) to that branch.

**mpmobile-android_seller** uses this branching model:

http://nvie.com/posts/a-successful-git-branching-model/

## Contact

In case you need to contact us, just send an email to `mpmobile-seller@mercadolibre.com` and someone will answer you as soon as possible.
