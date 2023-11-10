LiqPay payment system API SDK for Java
===========================

[![Build Status](https://github.com/iBubbleGun/LiqPay4j)]

[liqpay.ua](https://www.liqpay.ua/) is payment system associated with [PrivatBank](https://privatbank.ua/).

API Documentation [in Russian](https://www.liqpay.ua/documentation/ru)
and [in English](https://www.liqpay.ua/documentation/en)

**WARNING:** This SDK is not thread safe. We would be very appreciated for your contribution.

Installation and usage
----------------------
This library is published at [GitHub](https://github.com/iBubbleGun/LiqPay4j) and can be added as Maven
dependency.

### Use as Maven dependency

Add to your `pom.xml` repository and dependency:

```xml

<repositories>
    <repository>
        <id>repository</id>
        <url>https://github.com/iBubbleGun/LiqPay4j</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependency>
<groupId>com.github.ibubblegun.liqpay4j</groupId>
<artifactId>LiqPay4j</artifactId>
<version>v1.0</version>
</dependency>
```

    Then you can use it as described in API documentation:

```java
import com.github.ibubblegun.liqpay4j.impl.LiqPay4j;

import java.util.Map;

public class LiqPay4jExample {

    public static void main(String[] args) {

        // Creation of the HTML-form
        Map<String, String> params = new HashMap();
        params.put("amount", "1.50");
        params.put("currency", "USD");
        params.put("description", "description text");
        params.put("order_id", "order_id_1");
        params.put("sandbox", "1"); // enable the testing environment and card will NOT charged. If not set will be used property isCnbSandbox()

        LiqPay4jApi liqPay4j = new LiqPay4j(PUBLIC_KEY, PRIVATE_KEY);
        String html = liqPay4j.cnb_form(params);
        System.out.println(html);
    }
}
```

    It is recommended to use some Inversion of Control (IoC) container,
    like [Spring IoC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html)
    or [PicoContainer](http://picocontainer.codehaus.org/).

    #### Use proxy
    To use `LiqPay4j` with proxy you can initialize it like:

```java
import java.net.InetSocketAddress;
import java.net.Proxy;
```

    Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("proxy.host.com",8080);
    LiqPay4j liqPay4j=new LiqPay4j(PUBLIC_KEY,PRIVATE_KEY,proxy,"proxyLogin","some proxy password");

```
### Grails v2.x
In `grails-app/conf/BuildConfig.groovy` you should add repository and dependency:
```

```groovy
grails.project.dependency.resolution = {
    repositories {
        grailsPlugins()
        mavenRepo 'https://github.com/iBubbleGun/LiqPay4j'
    }
    dependencies {
        compile 'com.github.ibubblegun.liqpay4j.impl:LiqPay4j:v1.0'
    }
}
```

Then you can add `LiqPay4j` bean in `grails-app/conf/spring/resources.groovy`:

```groovy
import com.github.ibubblegun.liqpay4j.impl.LiqPay4j

// Place your Spring DSL code here
beans = {
    liqPay4j(LiqPay4j, '${com.github.ibubblegun.liqpay4j.impl.publicKey}', '${com.github.ibubblegun.liqpay4j.impl.privateKey}') {
        cnbSandbox = false // set true to enable the testing environment. Card is not charged
    }
}
```

It will create bean with name `LiqPay4j` of class `com.github.ibubblegun.liqpay4j.impl.LiqPay4j` and pass to it's
constructor public and private keys
that defined in `grails-app/conf/Config.groovy` like this:

```groovy
com.github.ibubblegun.liqpay4j.impl.publicKey = 'i31219995456'
com.github.ibubblegun.liqpay4j.impl.privateKey = '5czJZHmsjNJUiV0tqtBvPVaPJNZDyuoAIIYni68G'
```

Then you can use this `LiqPay4j` bean with dependency injection in your services or controllers:

```groovy
class UserController {
    LiqPay4jApi liqPay4j // this will inject LiqPay4j bean defined in resources.groovy

    def balanceReplenishment() {
        Map<String, String> params = [
                "amount"     : '30.5',
                "currency"   : 'UAH',
                "description": 'Balance replenishmenton on example.com',
                "order_id"   : "1",
                'result_url' : g.createLink(action: 'paymentResult', absolute: true).toString()]
        String button = liqPay4j.cnb_form(params);
        [button: button]
    }
}
```

And inside `grails-app/views/user/balanceReplenishment.gsp` you can output this button like this:

```gsp
<div>
    ${raw(button)}
</div>
```

Changelog
---------

[All releases](https://github.com/iBubbleGun/LiqPay4j)

### v1.0 First version.
