package com.flipkart.affordability.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;

/**
 * Created by piyushsinha.c on 04/09/17.
 */
public class WorkerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Register a consumer on the event bus to listen for message sent to "com.zanclus.customer"
        System.out.println("Worker Verticle");
        vertx.eventBus().consumer("cpu.intensive").handler(this :: handleCpuIntensiveRequest);
    }

    private void handleCpuIntensiveRequest(Message<Object> objectMessage) {

        long start = System.currentTimeMillis();
        long end;

        while (true) {
            end = System.currentTimeMillis();
            if ((end - start) > 50) {
                break;
            }
        }

        BigInteger result = BigInteger.valueOf(end-start);

        System.out.println(result);

        objectMessage.reply(JsonObject.mapFrom(new Saying(result)));

//        vertx.<BigInteger>executeBlocking(future -> {
//            long start = System.currentTimeMillis();
//            long end;
//
//            while (true) {
//                end = System.currentTimeMillis();
//                if ((end - start) > 50) {
//                    break;
//                }
//            }
//
//            BigInteger result = BigInteger.valueOf(end-start);
//
//            future.complete(result);
//        }, res -> {
//            System.out.println(res.result());
//            ;
//        });

    }
}
