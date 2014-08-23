##Configuration

The configuration parameters are as follows:

```
{
"private_key" : <private_key>,
"ssl" : <ssl>,
"url" : <url>,
"address" : <address>
"timeout" : <timeout>
}
```

* `private_key` - The privatekey assigned to your app from registering for a Recaptcha account. In this module, it has been set as an Linux environment variable, which is retrieved by the EnvJsonRecaptchaConfigurator. Do NOT pass/expose this key in a configuration object.
* `ssl` - Use the ssl HTTP address for Recaptcha. Defaults to false.
* `url` - The URL for Recaptcha. Defaults to: [http://www.google.com/recaptcha/api/verify](http://www.google.com/recaptcha/api/verify)
* `address` - The default Vert.x EventBus to use. Defaults to et.recaptcha.
* `timeout` - The default timeout in ms to use when making the HTTP request to the Recaptcha service.

An example configuration object could look like:

```
{
"ssl" : true,
"url" : "http://www.google.com/recaptcha/api/verify",
"address" : "et.recaptcha"
"timeout" : 5000l
}
```

### Dependency Injection and the HK2VerticleFactory

The RecaptchaVerticle requires a RecaptchaConfigurator to be injected. An EnvJsonRecaptchaConfigurator has been provided which handles setting default config settings, aside from the private key, which you must register for and set, otherwise the Verticle will not being to successfully start.

The default binding provided is for HK2, but you can create a guice module if that is your container of choice. 

There are two ways to enable DI:

1. In the vert.x.langs.properties set the java value to: java=com.englishtown~vert-mod-hk2~1.7.0:com.englishtown.vertx.hk2.HK2VerticleFactor
2. Pass a system property at startup like this: -Dvertx.langs.java=com.englishtown~vertx-mod-hk2~1.7.0:com.englishtown.vertx.hk2.HK2VerticleFactory

See the [englishtown/vertx-mod-hk2](https://github.com/englishtown/vertx-mod-hk2) project for more details.