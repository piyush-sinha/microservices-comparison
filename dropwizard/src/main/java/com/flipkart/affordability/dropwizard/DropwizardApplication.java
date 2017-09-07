package com.flipkart.affordability.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Created by piyushsinha.c on 29/08/17.
 */
public class DropwizardApplication extends Application<DropwizardConfiguration> {

    public static void main(String[] args) throws Exception {
        new DropwizardApplication().run(args);
    }

    @Override
    public void run(DropwizardConfiguration dropwizardConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new DropwizardResource(dropwizardConfiguration.getTemplate(),
                                                             dropwizardConfiguration.getDefaultName(),
                                                             dropwizardConfiguration.getExternalEndpoint(),
                                                             dropwizardConfiguration.getRedisHost(),
                                                             dropwizardConfiguration.getRedisPort(),
                                                             dropwizardConfiguration.getRedisPassword()));
    }
}
