# 13. 스프링 웹 MVC : 소개

# 1. 스프링 웹 MVC

스프링 부트가 제공해주는 기본 설정에 의해서 아무런 설정 파일을 작성하지 않아도 스프링 웹 MVC 개발을 바로 시작할 수 있다.

기본 설정은 spring-boot-autoconfigure라는 모듈에 spring.factories의 WebMvcAutoConfiguration class에서 확인할 수 있다.

![https://i.imgur.com/wmDmpwy.png](https://i.imgur.com/wmDmpwy.png)

![https://i.imgur.com/gT3eEO2.png](https://i.imgur.com/gT3eEO2.png)

**WebMvcAutoConfiguration class에서 몇 가지 살펴보자**

```java
@Bean
    @ConditionalOnMissingBean({HiddenHttpMethodFilter.class})
    @ConditionalOnProperty(
        prefix = "spring.mvc.hiddenmethod.filter",
        name = {"enabled"}
    )
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }
```

- HiddenHttpMethodFilter
    - 스프링 부트가 제공해주는 게 아니라 Spring 3.0 framework부터 제공해준다.
    - PUT, DELETE, PATCH 같은 요청일 경우 _method 라는 hidden form parameter로 값을 받아와서 controller에 mapping을 할 수 있게 도와주는 filter이다.
- HttpPutFormContentFilter
    - Http POST나 form data를 보낼 수 있도록 서블릿 spec에 정의되어 있다.
    - x-www-form-urlencoded 이라는 content type으로 form data를 보내오면 POST 요청해서 꺼낼 수 있는 것처럼 마찬가지로 PUT이나 PATCH 요청에서도 꺼낼 수 있도록 wrapping을 해준다.
- WebMvcProperties.class나 ResourceProperties.class
    - application.properties에서 정의하는 여러 properties 들을 파싱 받아오는 것들이다.

      ![https://i.imgur.com/7R2cU0g.png](https://i.imgur.com/7R2cU0g.png)

      WebMvcProperties는 spring.mvc로 시작하는 properties 들을 바인딩 받아온다.

      ![https://i.imgur.com/2zZNgGz.png](https://i.imgur.com/2zZNgGz.png)

      ResourceProperties는 spring.resources로 시작하는 properties 들을 바인딩 받아온다.

    - 어떠한 설정을 커스터마이징할 수 있는 properties들이 WebMvcPropertiesd와  ResourceProperties에 정의가 되어있다.
- WebMvcAutoConfigurationAdapter, ResourceLoader, configureMessageConverters, configureAsyncSupport 등 여러 가지 설정들은 커스터마이징이 이미 일어나고 있다. 기본적인 MVC 설정을 Spring Boot가 좀 더 컨벤션을 제공하는 것이다.
- ContentNegotiationViewResolver 관련 Bean도 등록이 되어있다.
- BeanNameViewResolver, LocaleResolver, form에서 입력받은 데이터를 바인딩 할 때 등 에러가 발생했을 때 에러에 해당하는 메시지를 생성하는 로직을 처리하는 Bean이 들어있고, Formatter와 webjars를 처리하는 ResourceHandlers 그리고 특정 directory에 있는 파일들을(예: resources 밑의 static directory) 처리하는  등 여러 가지 기본 설정들을 제공해 준다.
- 이렇게 스프링 부트가 제공해 주는 설정이 굉장히 많다.

# 2. 스프링 MVC 확장

**스프링 부트가 제공하는 Spring MVC 기능을 다 사용하면서 추가적인 설정을 더 하고 싶다면 아래와 같이 코드 작성**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

}
```

- 만약 @EnableWebMvc을 위 코드에 사용하면 스프링 부트가 제공하는 모든 MVC 기능은 사라지고, MVC에 관련된 모든 설정을 직접 다 해야 한다.


# 14. 스프링 웹 MVC : HttpMessageConverters, ViewResolve

# 1. HttpMessageConverters

- Spring framework 에서 제공하는 interface로 Spring MVC의 일부분이다.
- HTTP 요청 본문을 객체로 변경하거나, 객체를 HTTP 응답 본문으로 변경할 때 사용한다.
- @SpringMVC 기반의 SpringMVC에서 @RequestBody, @ResponseBody 와 같이 사용된다.

```java
@RestController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/user")
    public @ResponseBody User create(@RequestBody User user) {
        return null;
    }
}
```

사용하는 HttpMessageConverters는 여러 가지가 있고, 그 중에서 우리가 어떤 요청을 받았는지 또는 우리가 어떤 응답을 보내야하는지에 따라 사용하는 HttpMessageConverters가 달라진다.

예를 들어 요청이 JSON 요청이고 JSON 본문이 들어왔다면 JsonMessageConverter가 사용돼서 JSON 메세지를 User라는 객체로 변환한다.

Http는 전부 문자이므로 return 할 때 객체 자체를 내보낼 수 없는데, User 객체를 응답해야 할 때 JsonMessageConverter가 사용되며, String을 응답 할 때는 StringMessageConverter가 사용된다.

### @RestController를 사용한다면

아래 코드처럼 @ResponseBody를 생략해도 된다. 또한 ViewNameResolver를 타지 않고 바로 MessageConverter를 타서 응답 본문으로 내용이 들어가게 된다.

**UserController**

```java
@RestController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

만약 @RestController를 사용한다면 @ResponseBody를 반드시 써줘야 MessageConverter가 적용된다.

만약 @ResponseBody를 쓰지 않았다면, BeanNameViewResolver를 사용해서 return 값에 해당하는 view를 찾으려고 시도한다.

**UserController**

```java
@Controller
public class UserController {

    @GetMapping("/hello")
    public @ResponseBody String hello() {
        return "hello";
    }
}
```

### **MessageConverter를 활용하려면**

컴포지션 객체를 만들어야 한다. user를 생성하는 controller를 만들 건데 먼저 test부터 만들어보자.

**UserControllerTest**

```java
package me.hyeon.demospringmvc.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    public void createUser_JSON() throws Exception {
        String userJson = "{\"username\":\"hyeon\", \"password\":\"123\"}";

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is(equalTo("hyeon"))))
            .andExpect(jsonPath("$.password", is(equalTo("123"))));
    }
}
```

![https://i.imgur.com/JOiZHNh.png](https://i.imgur.com/JOiZHNh.png)

Controller 작성 후 실행하면 요청을 처리하는 Handler가 없기 때문에 404 error가 뜬다.

**UserController**

```java
@RestController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/users/create")
    public User create() {
        return null;
    }
}
```

![https://i.imgur.com/4Bv66u5.png](https://i.imgur.com/4Bv66u5.png)

이번엔 요청이 처리됐지만 원하는 응답이 나오지 않았기 때문에 200 error가 떴다.

**User**

```java
package me.hyeon.demospringmvc.user;

public class User {

    private Long id;

    private String username;

    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

User 클래스를 작성하면 test가 성공한다.

![https://i.imgur.com/nNstuAK.png](https://i.imgur.com/nNstuAK.png)

# 2. ViewResolve

스프링 부트는 뷰 리졸버 설정을 제공한다.

### ContentNegotiatingViewResolver

- 스프링 부트가 자동으로 제공한다.
- ViewResolver 중 하나로 들어오는 요청의 Accept Header에 따라 응답이 달라진다.
    - Accept Header는 브라우저 또는 클라이언트가 어떤 타입의 응답 본문을 원하는지 서버에 알려준다.
    - 클라이언트가 어떠한 View를 원하는지 판단하는 가장 좋은 정보는 Accept Header이다. 경우에 따라서는 Accept Header를 제공하지 않는 요청이 있는데, 그러한 경우에 대비해서 format이라는 parameter를 사용한다. (/path?format=pef)
- 어떠한 요청이 들어오면 그 요청의 응답을 만들 수 있는 모든 View를 찾고 최종적으로 Accept Header와 View의 type을 비교해서 선택한다.

### 요청은 JSON으로 보내고 응답은 XML로 받는 테스트코드를 작성해보자.

- Handler 코드는 전혀 고치지 않았다. Spring MVC의 ContentNegotiatingViewResolver가 들어오는 요청에 따른 응답을 처리해준다.

**UserControllerTest**

```java
package me.hyeon.demospringmvc.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createUser_XML() throws Exception {
        String userJson = "{\"username\":\"hyeon\", \"password\":\"123\"}";

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(xpath("/User/username").string("hyeon"))
                .andExpect(xpath("/User/password").string("123"));
    }
}
```

- 위 테스트 코드를 실행하면 미디어 타입을 처리할 HttpMessageConverter가 없기 때문에 아래과 같은 HttpMediaTypeNotAcceptableException으로 406 status error를 응답 받는다.
    - 테스트가 실패했을 때는 .andDo(print())가 자동으로 발동해서 요청 정보와 응답 정보를 보여준다.

    ```
    Resolved Exception:
                 Type = org.springframework.web.HttpMediaTypeNotAcceptableException
    ```

- HttpMessageConvertersAutoConfiguration 클래스에서 HttpMessageConverter를 사용하는 자동 설정을 볼 수 있다.
    - JacksonHttpMessageConvertersConfiguration 클래스 안에서 Json MessageConverter와 Xml MessageConverter를 확인할 수 있다.
    - Xml MessageConverter(MappingJackson2XmlHttpMessageConverterConfiguration)에 붙은 @ConditionalOnClass({XmlMapper.class})는 XmlMapper.class가 classpath에 있는 경우에만 Bean으로 등록한다는 의미이다.
    - XmlMapper.class가 classpath에 없기 때문에 테스트가 실패한 것이다.
    - 따라서 우리는 pom.xml에 의존성을 추가하여 classpath에 XmlMapper.class를 추가해야 한다.

**(xml로 내보내고 싶은 경우)의존성 주입**

```xml
<dependency>
		<groupId>com.fasterxml.jackson.dataformat</groupId>
		<artifactId>jackson-dataformat-xml</artifactId>
		<version>2.9.6</version>
</dependency>
```

# 15. 스프링 웹 MVC : 정적 리소스 지원, 웹 JAR, index 페이지와 파비콘

# 1. 정적 리소스

- 웹 브라우저에서 요청이 들어왔을 때 이미 만들어져있는 리소스
- Spring Boot Web MVC 기본 설정에서 정적 리소스를 제공한다.

## 1) 기본 리소스 위치 `/**`

기본적으로 아래 4가지 위치에 있는 리소스들은 "/**" 요청에 매핑되어 제공된다.

- classpath:/static
- classpath:/public
- classpath:/resources/
- classpath:/META-INF/resources
    - 예) “/hello.html” => /static/hello.html

**resources/static/hello.html**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
Hello Static Resource
</body>
</html>
```

Controller에 작성하지 않아도 스프링이 자동으로 매핑한다.

![https://i.imgur.com/7dAFcdA.png](https://i.imgur.com/7dAFcdA.png)

## 2) applciation.properties

기본적으로 리소스들은 URL 패턴이 root부터 매핑된다.

- spring.mvc.static-path-pattern: 맵핑 설정 변경 가능

```
spring.mvc.static-path-pattern=/static/**
```

- 설정 이후에는 http://localhost:8080/static/hello.html 와 같이 요청해야 한다.

![https://i.imgur.com/xVRMDK0.png](https://i.imgur.com/xVRMDK0.png)

- spring.mvc.static-locations: 리소스 찾을 위치 변경 가능
    - 기본 리소스 위치를 모두 사용할 수 없게 되어 권장하지 않는다.
- 권장하는 방법은 WebMvcConfigurer의 addRersourceHandlers로 커스터마이징 하는 것이다.
    - 스프링 부트가 제공하는 기존의 ResourceHandler를 유지하면서 내가 원하는 ResourceHandler를 추가할 수 있다.

  **WebConfig**

    ```java
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/m/**")
                    .addResourceLocations("classpath:/m/")
                    .setCachePeriod(20);
        }
    }
    ```

    - "classpath:/m/" : 반드시 마지막이 slash / 로 끝나야한다.
    - 여기서는 Cache 전략을 직접 설정해야 한다. 기본으로 Spring이 제공해주는 4가지 위치의 Resource는 다른 Cache 전략을 사용하고 있다.

  # 2. 웹 JAR

    - 스프링 부트는 웹 JAR에 대한 기본 매핑도 제공한다.
    - JAR는 여러개의 자바 클래스 파일과, 클래스들이 이용하는 관련 리소스 및 메타데이터를 하나의 파일로 모아서 자바 플랫폼에 응용 소프트웨어나 라이브러리를 배포하기 위한 소프트웨어 패키지 파일 포맷이다.
    - 예를 들어, 클라이언트에서 사용하는 자바스크립트 라이브러리 jQurey, BootStrap, ReactJS, ViewJS 등이 있다.

  ## 1) jQuery JAR 의존성 추가하기

  [https://mvnrepository.com/artifact/org.webjars.bower/jquery](https://mvnrepository.com/artifact/org.webjars.bower/jquery)

  **pom.xml**

    ```xml
    <dependency>
    	  <groupId>org.webjars.bower</groupId>
    	  <artifactId>jquery</artifactId>
    	  <version>3.5.1</version>
    </dependency>
    ```

  **hello.html**

    ```html
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    Hello Static Resource

    <script src="/webjars/jquery/3.5.1/dist/jquery.min.js"></script>
    <script>
        $(function() {
            alert("ready!");
        });
    </script>
    </body>
    </html>
    ```

  ![https://i.imgur.com/Ggo213v.png](https://i.imgur.com/Ggo213v.png)

  ## 2) 버전을 생략하고 사용하기

    - Spring Boot에서는 추가로 버전을 생략하는 기능을 제공한다.
    - jQuery 버전을 올릴 때마다 소스 코드에서 버전을 수정해야 한다면 정말 번거로울 것이다.
    - webjars-locator-core 의존성을 추가해야 한다.

  **pom.xml**

    ```xml
    <dependency>
    		<groupId>org.webjars</groupId>
    		<artifactId>webjars-locator-core</artifactId>
    		<version>0.35</version>
    </dependency>
    ```

  **hello.html 에서 버전 생략하기**

    ```html
    <script src="/webjars/jquery/dist/jquery.min.js"></script>
    ```

    - 버전 정보를 추가해주는 것은 Spring framework의 Resource Chaining과 관련이 있다. (Resource Handler와 Resource transformer를 chaining하는 내용이다.)

  # 3. index 페이지와 favicon

  ## 1) index 페이지(웰컴 페이지)

    - root를 요청했을 때 기본적인 어떤 페이지를 보여주고 싶다면, 정적 페이지와 동적 페이지를 보여주는 두 가지 방법이 있다.
    - 템플릿 엔진을 사용하는 방법은 아직 학습하지 않았기 때문에 정적 페이지를 보여주는 방법을 말하자면
    - 리소스를 제공해주는 4가지 위치 아무 곳에 index.html 파일을 두면 welcome 페이지로 사용한다.
        - classpath:/static
        - classpath:/public
        - classpath:/resources/
        - classpath:/META_INF/resources

      **resource/static/index.html**

        ```html
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Title</title>
        </head>
        <body>
        <h1>웰컴 스프링 부트!</h1>

        </script>
        </body>
        </html>
        ```

      ![https://i.imgur.com/hzaIZ61.png](https://i.imgur.com/hzaIZ61.png)

  ## 2) favicon

    - favicon을 교체하는 방법은 리소스를 제공해주는 4가지 위치 아무 곳에 favicon.ico 파일을 두면 된다.
    - 파비콘이 바뀌지 않을 때
        - 기본 favicon은 spring boot jar 파일에서 제공해주는데 캐싱 기능 때문에 변경 후에도 favicon 이 변경되지 않는다.

  [How do I force a favicon refresh?](https://stackoverflow.com/questions/2208933/how-do-i-force-a-favicon-refresh)

  ![https://i.imgur.com/y6qomjL.png](https://i.imgur.com/y6qomjL.png)


### 참고

- 백기선님의 스프링 부트 개념과 활용 강의