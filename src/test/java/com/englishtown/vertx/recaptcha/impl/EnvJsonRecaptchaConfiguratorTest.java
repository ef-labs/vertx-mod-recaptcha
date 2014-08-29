package com.englishtown.vertx.recaptcha.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link com.englishtown.vertx.recaptcha.RecaptchaConfigurator}
 */
@RunWith(MockitoJUnitRunner.class)
public class EnvJsonRecaptchaConfiguratorTest {

    EnvJsonRecaptchaConfigurator recaptchaConfigurator;
    EnvJsonRecaptchaConfigurator recaptchaConfigurator2;

    JsonObject config = new JsonObject();

    @Mock
    Container container;

    @Before
    public void setUp() {

        config.putString("private_key", "key")
                .putBoolean("ssl", true)
                .putString("event_bus_address", "et.recaptcha");

        when(container.config()).thenReturn(config);
        recaptchaConfigurator = new EnvJsonRecaptchaConfigurator(config, container);
        recaptchaConfigurator2 = new EnvJsonRecaptchaConfigurator(container);

    }

    @Test
    public void test_init_ssl() throws Exception {

        recaptchaConfigurator.init(config);

        assertEquals("et.recaptcha", recaptchaConfigurator.getAddress());
        assertEquals("key", recaptchaConfigurator.getPrivateKey());
        // expecting true if "ssl" parameter is set and also for the url to be https
        assertEquals(true, recaptchaConfigurator.getSsl());
        // should default to https://www.google.com/recaptcha/api/verify if no url or ssl is specified
        assertEquals("https://www.google.com/recaptcha/api/verify", recaptchaConfigurator.getUrl().toString());

    }

    @Test
    public void test_init_no_ssl() throws Exception {

        config.removeField("ssl");

        recaptchaConfigurator.init(config);

        assertEquals("et.recaptcha", recaptchaConfigurator.getAddress());
        assertEquals("key", recaptchaConfigurator.getPrivateKey());
        // expecting false is no "ssl" parameter is set
        assertEquals(false, recaptchaConfigurator.getSsl());
        // should default to http://www.google.com/recaptcha/api/verify if no url or ssl is specified
        assertEquals("http://www.google.com/recaptcha/api/verify", recaptchaConfigurator.getUrl().toString());

    }

    @Test
    public void test_init_no_config_contstructor() throws Exception {

        recaptchaConfigurator2.init(config);

        assertEquals("et.recaptcha", recaptchaConfigurator2.getAddress());
        assertEquals("key", recaptchaConfigurator2.getPrivateKey());
        assertEquals(true, recaptchaConfigurator2.getSsl());
        assertEquals("https://www.google.com/recaptcha/api/verify", recaptchaConfigurator2.getUrl().toString());

    }

    @Test(expected=IllegalArgumentException.class)
    public void test_init_no_private_key() throws Exception {

        config.removeField("private_key");
        recaptchaConfigurator2.init(config);

    }

}
