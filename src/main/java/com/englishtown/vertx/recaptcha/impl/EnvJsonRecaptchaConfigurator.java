package com.englishtown.vertx.recaptcha.impl;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import javax.inject.Inject;

/**
 * Recaptcha configuration is read from JSON, but also tries to read environment variables.
 * <p/>
 * Environment variables include:
 * ENV_VAR_RECAPTCHA_PRIVATE_KEY - the Recaptcha private_key to use.
 * This can be set in your .bash_profile for testing by using Terminal like so:
 *
 * vim ~/.bash_profile
 * export RECAPTCHA_PRIVATE_KEY=your_private_key_with_no_quotes
 *
 * and restarting your machine for it to take effect.
 */
public class EnvJsonRecaptchaConfigurator extends JsonRecaptchaConfigurator {

    public static final String ENV_VAR_RECAPTCHA_PRIVATE_KEY = "RECAPTCHA_PRIVATE_KEY";

    @Inject
    public EnvJsonRecaptchaConfigurator(Container container) {
        super(container);
    }

    public EnvJsonRecaptchaConfigurator(JsonObject config, Container container) {
        super(config, container);
    }

    @Override
    protected void initPrivateKey(JsonObject config) {
        privateKey = container.env().get(ENV_VAR_RECAPTCHA_PRIVATE_KEY);

        // Recall super if private key env var is missing
        if (privateKey == null) {
            super.initPrivateKey(config);
        }
    }

}
