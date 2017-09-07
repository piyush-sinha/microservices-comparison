package com.flipkart.affordability.dropwizard;

import org.glassfish.jersey.client.ClientProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by piyushsinha.c on 29/08/17.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DropwizardResource {
    private final AtomicLong counter;
    private final String     template;
    private final String     defaultName;
    private final WebTarget  webTarget;
    private final JedisPool  jedisPool;

    public DropwizardResource(String template, String defaultName, String externalEndpoint, String redisHost,
                              int redisPort, String redisPassword) {
        this.counter = new AtomicLong();
        this.template = template;
        this.defaultName = defaultName;
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 1000);

        this.webTarget = client.target(externalEndpoint);


        this.jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, 10000, redisPassword);
    }

    @Path("fact")
    @GET
    public Response sayHello() {
        long start = System.currentTimeMillis();
        long end;

        while (true) {
            end = System.currentTimeMillis();
            if ((end - start) > 50) {
                break;
            }
        }

        BigInteger result = BigInteger.valueOf(end-start);

        System.out.println(result.longValueExact());

        return Response.ok(new Saying(result)).build();
    }

    @GET
    @Path("external")
    public Response callExternal() {
        Response response    = null;
        String   returnValue = "No response";
        try {
            response = webTarget.path("/external").request().get();
            if (response.getStatus() == 200) {
                returnValue = response.readEntity(String.class);
            }
        } catch (Exception e) {
            return Response.serverError().build();
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return Response.ok(returnValue).build();
    }

    @Path("add")
    @GET
    public Response addKey(@QueryParam("key") String key, @QueryParam("value") String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("get")
    public Response getValue(@QueryParam("key") String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = jedisPool.getResource();
            value = jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return Response.ok(value).build();
    }
}
