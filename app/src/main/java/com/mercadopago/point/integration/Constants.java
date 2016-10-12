package com.mercadopago.point.integration;

/**
 * Created by pgallazzi on 10/7/16.
 */

public interface Constants {

    String POINT_PACKAGE = "com.mercadopago.merchant";

    String RESULT_STATUS_OK = "OK";
    String RESULT_STATUS_FAILED = "FAILED";
    String CREDIT_CARD = "credit_card";
    String DEBIT_CARD = "debit_card";
    String ACTION = "com.mercadopago.PAYMENT_ACTION";
    String LINK = "https://secure.mlstatic.com/org-img/point/app/index.html";

    /**
     * @deprecated
     * This is only for backwards compatibility!!!
     * Use {@value BundleCodes#PAYMENT_ID} instead!
     */
    String RESULT_PAYMENT_ID = "paymentId";

}
