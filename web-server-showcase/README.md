# 5. 스프링 부트 원리 - 내장 서버(HTTPS와 HTTP2)

## HTTPS vs HTTP

**HTTP**(Hyper Text Transfer Protocol)란 서버/클라이언트 모델을 따라 데이터를 주고 받기 위한 프로토콜이다. 즉, HTTP는 인터넷에서 하이퍼텍스트를 교환하기 위한 통신 규약을 의미한다.

HTTP는 암호화가 되지 않은 데이터를 전송하는 프로토콜이었기 때문에 주고받는 메시지를 변조시키거나 가로채는 것이 쉽다. 따라서 보안성이 향상된 HTTP인 HTTPS가 탄생했다.

**HTTPS**의 S는 Over Secure Socket Layer의 약자로 HTTP에 데이터 암호화가 추가된 프로토콜을 말한다.

## HTTPS와 SSL

HTTPS와 SSL를 동일한 의미로 이해하고 있는 경우가 많다. 이는 인터넷과 웹의 관계와 비슷하다. 웹이 인터넷 위에서 돌아가는 서비스 중의 하나인 것처럼 HTTPS도 SSL 프로토콜 위에서 돌아가는 프로토콜이다.

# 1. HTTPS 사용하기

## 1) keystore 생성하기

터미널에서 다음 명령어를 입력하여 키스토어를 생성한다.

```
keytool -genkey -alias spring -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 4000
```

application.properties에 property를 설정한다.

```
server.ssl.key-store=keystore.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=123456
server.ssl.key-alias=spring
```

위와 같이 설정하면 스프링 부트는 기본적으로 내장 tomcat이 사용하는 커넥터가 하나만 등록되는데 이 커넥터에 SSL을 적용하여 앞으로 모든 요청은 HTTPS로 해야한다.

HTTPS로 요청하면 아래 화면을 볼 수 있는데 그 이유는 브라우저에서 서버에 요청할 때 서버가 인증서를 보낸다. 그 인증서는 만든 키스토어에 들어있다. 공식적으로 발급된 게 아니라 브라우저는 인증서의 Public key를 모르기 때문에 아래와 같은 화면이 나타난다.

LetsEncript 등과 같은 공인된 인증서들의 Public key는 대부분의 브라우저들이 알고 있어서 아래와 같은 화면이 나타나지 않는다.

![https://i.imgur.com/4YewGcb.png](https://i.imgur.com/4YewGcb.png)

**WebServerShowcaseApplication**

```java
@SpringBootApplication
@RestController
public class WebServerShowcaseApplication {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring";
    }

    public static void main(String[] args) {
        SpringApplication.run(WebServerShowcaseApplication.class, args);
    }

}
```

**https (-k option)**

- http2로 요청해도 HTTP/1.1 로 받는다.

![https://i.imgur.com/cMMhkpg.png](https://i.imgur.com/cMMhkpg.png)

HTTPS를 적용하면 커넥터가 하나이므로 더 이상 HTTP를 받을 커넥터가 없어 HTTP를 사용할 수 없다.

다음과 같은 설정을 통해서 HTTP를 사용하도록 할 수 있다.

# 2. HTTP, HTTPS 둘 다 받기

## 1) 톰캣 Connetor 생성하기

**WebServerShowcaseApplication**

```java
package me.hyeon.webservershowcase;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebServerShowcaseApplication {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring";
    }

    public static void main(String[] args) {
        SpringApplication.run(WebServerShowcaseApplication.class, args);
    }

    @Bean
    public ServletWebServerFactory serverFactory() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        return connector;
    }

}
```

# 3. HTTP2

## 1) HTTP2를 사용하기

**application.properties**

```
server.http2.enabled=true
```

- 위의 코드를 추가하면 HTTP2를 사용할 수 있다.
- 하지만 서버마다 제약사항이 다 다르다. undertow는 HTTPS만 적용되어 있으면 어떠한 추가 설정 없이도 HTTP2 사용이 가능하다.

## 2) HTTP2 with Tomcat

- Tomcat(Tomcat 8 기준)은 매우 복잡하기 때문에 권장하지 않지만 Tomcat 9와 JDK 9를 쓴다면 추가적인 라이브러리 없이 가능하다.
- pom.xml에 ****<properties>를 추가해주면 java version과 tomcat version을 변경할 수 있다.

**pom.xml**

```xml
<properties>
	    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
	    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	    <java.version>9</java.version>
	    <tomcat.version>9.0.10</tomcat.version>
</properties>
```

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의