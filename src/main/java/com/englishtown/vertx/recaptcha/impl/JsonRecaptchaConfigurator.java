package com.englishtown.vertx.recaptcha.impl;

import com.englishtown.vertx.recaptcha.RecaptchaConfigurator;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import javax.inject.Inject;
import java.net.URI;

/**
 * Default implementation for a {@link com.englishtown.vertx.recaptcha.RecaptchaConfigurator}
 */

public class JsonRecaptchaConfigurator implements RecaptchaConfigurator {

    protected final Container container;
    protected String privateKey;
    private URI url;
    private boolean ssl = false;
    private long timeout;
    private String address;
    public static final String DEFAULT_ADDRESS = "et.recaptcha";



    @Inject
    public JsonRecaptchaConfigurator(Container container) {
        this(container.config(), container);
    }

    public JsonRecaptchaConfigurator(JsonObject config, Container container) {
        this.container = container;
        if (config == null) {
            throw new IllegalArgumentException("JSON config was null");
        }
        init(config);
    }

    protected void init(JsonObject config) {
        initPrivateKey(config);
        initSsl(config);
        initUrl(config);
        initEventBusAddress(config);
        initTimeout(config);
    }

    protected void initPrivateKey(JsonObject config) {
        privateKey = config.getString("private_key");
        if (privateKey == null) {
            throw new IllegalArgumentException("Please provide a private_key");
        }
    }

    protected void initTimeout(JsonObject config) {
        timeout = config.getLong("timeout", 5000l);
    }

    protected void initSsl(JsonObject config) {
        ssl = config.getBoolean("ssl", false);
    }

    protected void initUrl(JsonObject config) {
        String s = config.getString("url");
        if (s == null) {
            s = (getSsl() ? "https://www.google.com/recaptcha/api/verify" : "http://www.google.com/recaptcha/api/verify");
        }
        url = URI.create(s);
    }

    protected void initEventBusAddress(JsonObject config) {
        address = config.getString("address", DEFAULT_ADDRESS);
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public boolean getSsl() {
        return ssl;
    }

    @Override
    public URI getUrl() {
        return url;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public long getTimeout() { return timeout; }

}
