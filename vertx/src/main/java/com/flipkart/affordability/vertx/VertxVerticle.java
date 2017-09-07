package com.flipkart.affordability.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.math.BigInteger;

/**
 * Created by piyushsinha.c on 29/08/17.
 */
public class VertxVerticle extends AbstractVerticle {
    private WebClient   httpClient;
    private RedisClient redisClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        redisClient = RedisClient.create(vertx, new RedisOptions().setHost(config().getString("redisHost"))
                .setAuth(config().getString("redisAuth")));


        System.out.println("http created");

        DeploymentOptions deployOpts = new DeploymentOptions().setWorker(true).setMultiThreaded(true).setInstances(20);
        vertx.deployVerticle(WorkerVerticle.class.getName(), deployOpts);

        router.get("/fact").blockingHandler(rc -> {
            final DeliveryOptions opts = new DeliveryOptions()
                    .setSendTimeout(2000);
           // opts.addHeader("num", rc.request().getParam("num"));
            vertx.eventBus().send("cpu.intensive", null, opts, reply -> handleReply(reply, rc));

        });
      //  router.get("/fact").handler(this :: findFact);
        router.get("/external").handler(this :: callExternal);
        router.get("/external1").blockingHandler(this :: callExternal);
        router.get("/external2").handler(this :: callExternal2);
        router.get("/external3").blockingHandler(this :: callExternal2);

        router.get("/add").handler(this :: addKey);
        router.get("/get").handler(this :: getKey);

        vertx.createHttpServer(new HttpServerOptions().setInitialSettings(new Http2Settings().setMaxConcurrentStreams(1024))).requestHandler(router:: accept)
                .listen(
                        config().getInteger("port"),
                        ar -> {
                            if (ar.succeeded()) {
                                System.out.println("Server started : " + config().getInteger("port"));
                            } else {
                                System.out.println("Cannot start the server: " + ar.cause());
                            }
                        }
                );
    }

    private void handleReply(AsyncResult<Message<Object>> reply, RoutingContext rc) {
        if (reply.succeeded()) {
            Message<Object> replyMsg = reply.result();
            if (reply.succeeded()) {
                rc.response()
                        .setStatusMessage("OK")
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end(Json.encodePrettily(replyMsg.body()));
            } else {
                rc.response()
                        .setStatusCode(500)
                        .setStatusMessage("Server Error")
                        .end(reply.cause().getLocalizedMessage());
            }
        }
    }

    private void getKey(RoutingContext routingContext) {
        String key = routingContext.request().getParam("key");
        

        redisClient.get(key, r -> {
            if (r.succeeded()) {
                routingContext.response().setStatusCode(200).end(r.result());
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void addKey(RoutingContext routingContext) {
        String key   = routingContext.request().getParam("key");
        String value = routingContext.request().getParam("value");

        redisClient.set(key, value, r -> {
            if (r.succeeded()) {
                System.out.println("key stored");
                routingContext.response().setStatusCode(201).end();
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void callExternal(RoutingContext routingContext) {
        httpClient = WebClient.create(vertx, new WebClientOptions().setTcpKeepAlive(true));

        httpClient.get(config().getInteger("externalPort"), config().getString("externalHost"), "/external")
                .send(ar -> {
                    if (ar.succeeded()) {
                        // Obtain response
                        HttpResponse<Buffer> response = ar.result();
                        String               body     = response.bodyAsString();
                        routingContext.response().end(Json.encodePrettily(body));
                    } else {
                        routingContext.response().setStatusCode(500).end();
                    }
                });
    }

    private void callExternal2(RoutingContext routingContext) {
        httpClient = WebClient.create(vertx, new WebClientOptions().setTcpKeepAlive(true));

        vertx.<String>executeBlocking(fut -> httpClient.get(config().getInteger("externalPort"), config().getString("externalHost"), "/external")
                .send(ar -> {
                    if (ar.succeeded()) {
                        // Obtain response
                        HttpResponse<Buffer> response = ar.result();
                        fut.complete(response.bodyAsString());
                    } else {
                        fut.fail("fail");
                    }
                }), res -> {
            if (res.succeeded()) {
                routingContext.response().end(Json.encodePrettily(res.result()));
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void findFact(RoutingContext routingContext) {
        //vertx.<BigInteger>executeBlocking(future -> {
            long start = System.currentTimeMillis();

            long end;

            while (true) {
                end = System.currentTimeMillis();
                if ((end - start) > 50) {
                    break;
                }
            }
            BigInteger result = BigInteger.valueOf(end - start);
            System.out.println(result);
           // future.complete(result);
       // }, res -> {
       //     if (res.succeeded()) {
                routingContext.response().end(Json.encodePrettily(new Saying(result)));
       //     } else {
           //     res.cause().printStackTrace();
         //   }

       // });


//        String name  = routingContext.request().getParam("num");
//        Integer num = Integer.parseInt(name);
//
//
//        vertx.<BigInteger>executeBlocking(future -> {
//            BigInteger result = BigInteger.ONE;
//
//            for(int i = 2; i<=num;i++) {
//                result = result.add(BigInteger.valueOf(i));
//            }
//            future.complete(result);
//        }, res -> {
//            System.out.println(res.result());
//            routingContext.response().end(Json.encodePrettily(new Saying(res.result())));
//        });
    }
}
