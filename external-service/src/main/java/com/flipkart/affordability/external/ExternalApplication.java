package com.flipkart.affordability.external;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

/**
 * Created by piyushsinha.c on 29/08/17.
 */
public class ExternalApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new ExternalApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(new ExternalResource());
    }
}
