package com.flipkart.affordability.vertx;

import java.math.BigInteger;

/**
 * Created by piyushsinha.c on 27/03/17.
 */
public class Saying {
    private BigInteger factorial;

    public Saying() {
        // Jackson deserialization
    }

    public Saying(BigInteger factorial) {
        this.factorial = factorial;
    }

    public BigInteger getFactorial() {
        return factorial;
    }
}
