package com.englishtown.vertx.hk2;

import com.englishtown.vertx.recaptcha.RecaptchaConfigurator;
import com.englishtown.vertx.recaptcha.impl.EnvJsonRecaptchaConfigurator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Recaptcha HK2 Binder
 */
public class RecaptchaBinder extends AbstractBinder {
    /**
     * Implement to provide binding definitions using the exposed binding
     * methods.
     */
    @Override
    protected void configure() {

        bind(EnvJsonRecaptchaConfigurator.class).to(RecaptchaConfigurator.class).in(Singleton.class);

    }
}
