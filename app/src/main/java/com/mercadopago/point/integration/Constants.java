package com.mercadopago.point.integration;

/**
 * Created by pgallazzi on 10/7/16.
 */

public interface Constants {

    String POINT_PACKAGE = "com.mercadopago.wallet";

    String RESULT_STATUS_OK = "OK";
    String RESULT_STATUS_FAILED = "FAILED";
    String USER_CANCELLED_ERROR = "USER_CANCELLED_ERROR";
    String CREDIT_CARD = "CREDIT_CARD";
    String DEBIT_CARD = "DEBIT_CARD";
    String ACTION = "com.mercadopago.PAYMENT_ACTION";
    String LINK = "https://www.mercadopago.com/point/integrations";

    /**
     * @deprecated
     * This is only for backwards compatibility!!!
     * Use {@value BundleCodes#PAYMENT_ID} instead!
     */
    String RESULT_PAYMENT_ID = "paymentId";

}
