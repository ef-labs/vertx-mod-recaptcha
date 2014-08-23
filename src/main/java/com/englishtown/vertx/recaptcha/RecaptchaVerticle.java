package com.englishtown.vertx.recaptcha;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.json.JsonObject;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * This will listen for a vert.x EventBus message for a Recaptcha response and send off a request to verify a Recaptcha
 * and reply to it with the response.
 */
public class RecaptchaVerticle extends BusModBase implements Handler<Message<JsonObject>> {

    private RecaptchaConfigurator configurator;
    protected String address;
    protected URI url;
    public static final String CHARSET_UTF8 = Charset.forName("UTF-8").toString();
    protected HttpClient client;

    @Inject
    public RecaptchaVerticle(RecaptchaConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Start the busmod
     */
    @Override
    public void start() {
        super.start();
        address = configurator.getAddress();
        url = configurator.getUrl();
        eb.registerHandler(address, this);
        client = vertx.createHttpClient()
                .setHost(url.getHost())
                .setSSL(configurator.getSsl()); // TODO: port?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        eb.unregisterHandler(address, this);
        configurator = null;
        super.stop();
    }

    // reply with a json message for the result of a Provider's Recaptcha verify() method
    @Override
    public void handle(Message<JsonObject> message) {

        try {
            String remoteIp = getMandatoryString("remote_ip", message);
            String challenge = getMandatoryString("challenge", message);
            String response = getMandatoryString("response", message);

            if (remoteIp == null) {
                return;
            }
            if (challenge == null) {
                return;
            }
            if (response == null) {
                return;
            }

            verify(remoteIp, challenge, response, message);

        } catch (Exception e) {
            sendError(message, "Unhandled exception!", e);
        }
    }

    private void verify(
            String remoteIp,
            String challenge,
            String response,
            Message<JsonObject> message) {

        String content = null;
        // set POST params
        try {

            content = "privatekey=" + URLEncoder.encode(configurator.getPrivateKey(), CHARSET_UTF8) +
                    "&remoteip=" + URLEncoder.encode(remoteIp, CHARSET_UTF8) +
                    "&challenge=" + URLEncoder.encode(challenge, CHARSET_UTF8) +
                    "&response=" + URLEncoder.encode(response, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(url.getPath(), r -> handleResponse(r, message))
                .setTimeout(configurator.getTimeout())
                .exceptionHandler(t -> handleThrowable(t, message))
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .end(content);

    }

    private void handleResponse(HttpClientResponse response, Message<JsonObject> message) {

        response.bodyHandler(bodyHandler -> {
            // google docs says 1st line of the response body will contain a true or false string
            // if false, the second line will be the error message.
            String[] body = bodyHandler.getString(0, bodyHandler.length()).split("\n");

            if (body[0].equalsIgnoreCase("true")) {
                sendOK(message);
            } else {
                sendError(message, body[1]);
            }
        });

    }

    private void handleThrowable(Throwable cause, Message<JsonObject> message) {
        Exception e = cause instanceof Exception ? (Exception) cause : new RuntimeException(cause);
        sendError(message, "Http exception", e);
    }

}
