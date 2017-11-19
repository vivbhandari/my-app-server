package com.dekses.jersey.docker.demo;

import java.util.HashMap;
import java.util.Map;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.net.RequestOptions.RequestOptionsBuilder;

public class Payment {

    public static boolean process(String tokenId) {
        System.out.println("Processing payment");
        RequestOptions requestOptions = (new RequestOptionsBuilder()).setApiKey("sk_test_RX7c8QwJGgsb1nuZyTRhtwAY").build();
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", 100);
        chargeMap.put("currency", "usd");
        chargeMap.put("source", tokenId); // obtained via client
        try {
            Charge charge = Charge.create(chargeMap, requestOptions);
            System.out.println(charge);
            return charge.getOutcome().getNetworkStatus().equals("approved_by_network");
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
