package com.mercadopago.point.integration;

/**
 * Created by sebad on 3/20/15.
 * Modified by PabloGallazzi on 10/07/16.
 */

public interface BundleCodes {

    //To start the operation.
    /**
     * This is the amount to be charged, it must be a double.
     */
    String AMOUNT = "amount";
    /**
     * This is the description to be seen at MercadoPago, it must be a String.
     */
    String DESCRIPTION = "description";
    /**
     * Send this if you want to be notified that the buyer cancelled the payment in the middle.
     * The accepted values are true and false, the default is set to false
     */
    String DISABLE_BACK_BUTTON = "disable_back_button";
    /**
     * Specify the card type must be one of
     * {@value Constants#CREDIT_CARD}, {@value Constants#DEBIT_CARD} or null.
     */
    String CARD_TYPE = "card_type";
    /**
     * Specify the amount of installments must be an int
     */
    String INSTALLMENTS = "installments";
    /**
     * Specify your MercadoPago's Application id. Can be null
     */
    String APP_ID = "client_id";
    /**
     * Specify your MercadoPago's Application secret. Can be null
     */
    String APP_SECRET = "client_secret";
    /**
     * Specify how much are you going to charge. This is NOT a percentage,
     * should be a number smaller than the value in AMOUNT. Can be null
     */
    String APP_FEE = "application_fee";
    /**
     * Specify the sponsor_id to be used. If sent it can't be null
     */
    String SPONSOR_ID = "sponsor_id";
    /**
     * Specify the notification_url to be used. If sent it can't be null
     */
    String NOTIFICATION_URL = "notification_url";

    /**
     * Specify the payer_email to be used. If sent it can't be null
     */
    String PAYER_EMAIL = "payer_email";

    /**
     * Specify the external_reference to be used. If sent it can't be null
     */
    String EXTERNAL_REFERENCE = "external_reference";

    //For the return of the operation.
    /**
     * In case you are coming from URL based integration,
     * this are the URL's the app will call to end the flow.
     */
    String URL_SUCCESS = "success_url";
    String URL_FAIL = "fail_url";
    /**
     * Specifies the result of the operation can be
     * {@value Constants#RESULT_STATUS_FAILED} or {@value Constants#RESULT_STATUS_OK}
     */
    String RESULT_STATUS = "result_status";
    /**
     * This only comes when the payment is approved, this is the replacement for
     * {@value Constants#RESULT_PAYMENT_ID}
     */
    String PAYMENT_ID = "payment_id";
    /**
     * Specifies the trunc name of the person being charged
     */
    String TRUNC_CARD_HOLDER = "trunc_card_holder";
    /**
     * This two only comes when the payment is rejected.
     */
    String ERROR = "error";
    String ERROR_DETAIL = "error_detail";

}

