package com.englishtown.vertx.recaptcha;

import java.net.URI;

/**
 * Configuration interface for a {@link com.englishtown.vertx.recaptcha.RecaptchaVerticle}
 */
public interface RecaptchaConfigurator {

    String getPrivateKey();

    boolean getSsl();

    URI getUrl();

    String getAddress();

    long getTimeout();

}

