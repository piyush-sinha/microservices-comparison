# microservices-comparison

 This article focusses on the comparison between Vertx and the most common framework used in Flipkart i.e. Dropwizard.
 
## Test Scenarios

This is not another comparison based on some "Hello World" application. We will be covering following benchmarking scenarios to extensively test our frameworks : 

### CPU bound
We will run a loop for 50ms. Following is the code piece

```long start = System.currentTimeMillis();
 
long end;
 
while (true) {
    end = System.currentTimeMillis();
    if ((end - start) > 50) {
        break;
    }
}
BigInteger result = BigInteger.valueOf(end - start);
System.out.println(result);
```

### IO bound

I will divide it into two categories:

#### External service call (where a HTTP call is made to an external service)
a) Used Jersey client for Dropwizard

b)  Used vertx-webclient (async HTTP/HTTP2 vertx client)

#### Redis call (where a HTTP call is made to redis server to fetch a key)
a) Used Jedis for Dropwizard

b) Used Vert.x-redis (async vertx client for accessing redis)
