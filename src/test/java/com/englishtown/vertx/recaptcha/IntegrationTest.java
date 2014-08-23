package com.englishtown.vertx.recaptcha;

import com.englishtown.vertx.recaptcha.impl.JsonRecaptchaConfigurator;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 * Integration test for {@link com.englishtown.vertx.recaptcha.RecaptchaVerticle}
 */
public class IntegrationTest extends TestVerticle {

    private String address = JsonRecaptchaConfigurator.DEFAULT_ADDRESS;

    @Override
    public void start(Future<Void> startedResult) {

        JsonObject config = new JsonObject();

        container.deployVerticle(RecaptchaVerticle.class.getName(), config, result -> {
            if (result.succeeded()) {
                super.start(startedResult);
            } else {
                startedResult.setFailure(result.cause());
            }
        });

    }

    @Test
    public void testRecaptcha() throws Exception {

        /**
         * the privatekey sent across the event bus in the below json is supplied from an environment variable.
         * see {@link com.englishtown.vertx.recaptcha.impl.EnvJsonRecaptchaConfigurator}
         */
        JsonObject message = new JsonObject()
                .putString("remote_ip", "127.0.0.1")
                .putString("challenge", "toughchallenge")
                .putString("response", "incorrect");

        vertx.eventBus().send(address, message, (Message<JsonObject> reply) -> {
            JsonObject body = reply.body();
            // this is the expected error message for not supplying a correct response to the Captcha.
            // If we were able to supply the correct answer programmatically, or that would defeat the point of Captcha
            if (body != null && body.getString("message").equalsIgnoreCase("invalid-request-cookie")) {
                assertEquals("error", body.getString("status"));
                testComplete();
            }
        });

    }

}
