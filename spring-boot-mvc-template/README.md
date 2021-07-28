# 16. 스프링 웹 MVC : Thymeleaf

스프링 부트를 사용한 Spring MVC 애플리케이션으로 동적 컨텐츠를 생성하는 방법에 대해 알아보자.

# 1. 템플릿 엔진

- 템플릿 엔진이란 동적 컨텐츠를 생성하는 방법이다.

템플릿 엔진은 주로 View를 만드는데 사용되지만, Code Generation, Email Template 생성 등에도 사용된다.

이번에 살펴볼 템플릿 엔진의 주요한 기능은 Spring MVC에서 View를 만드는 기능이다.

기본적인 템플릿은 같은데 동적으로 컨텐츠를 생성하여 제공하는 경우에 템플릿 엔진을 유용하게 사용할 수 있다.

## 1) 스프링 부트가 자동 설정을 지원하는 템플릿 엔진

- FreeMarker
- Groovy
- **Thymeleaf**
- Mustache

## 2) JSP를 권장하지 않는 이유

- JSP는 자동 설정을 지원하지 않는 데다가 권장하지도 않는다.
- 권장하지 않는 이유
  - 스프링 부트가 지향하는 바와 맞지 않는데, 스프링 부트는 독립적으로 실행 가능한 Embeded Tomcat으로 Application을 빠르게 배포하고 실행하길 바란다.
  - JSP를 사용하면 JAR로 패키징한 파일을 사용하지 못하고, WAR로 패키징 해야한다.
    물론 WAR로 패키징해도 Embeded Tomcat으로 실행할 수 있고, 다른 Tomcat Servlet 엔진에 WAR 파일을 배포할 수 있다.
  - 하지만 Servlet 엔진 중에 Undertow라는 가장 최근에 만들어진 Servlet 엔진은 JSP를 아예 지원하지 않는다.
- JSP에 대한 의존성을 넣으면 의존성 문제가 생기는 경우도 있다.
- 이러한 제약 사항들이 있어서 JSP는 요즘에 잘 쓰지 않는다.

## 3) JAR vs WAR

![https://i.imgur.com/WNadj5O.png](https://i.imgur.com/WNadj5O.png)

- java 기반의 application의 배포 형태
- JAVA JAR TOOL을 이용하여 압축한 압축 파일 ( 즉, 둘이 같은 압축 형태 )
- JAR와 WAR는 사용 목적이 다름
- JAR가 가장 적은 압축 범위를 가지고 있다.
- WAR는 JAR의 모든 파일 + WAR만의 파일을 더 압축한다.

### (1) JAR (Java Archive)

- path 정보를 유지한 상태로 자바 프로젝트를 압축한 파일이다.
- 자바 클래스 파일과, 각 클래스들이 사용하는 관련 리소스파일 및 메타데이터을 압축한 파일이다.
- 원하는 구조로 구성이 가능하며 JDK(Java Development Kit)에 포함되어 있는 JRE(Java Runtime Environment)만 가지고도 실행 가능하다.

### (2) WAR(Web Application Archive)

- jsp 컨테이너에 배치할 수 있는 웹어플리케이션 압축 파일이다.
- 웹 관련 자원만을(JSP, Servlet, JAR, Class, HTML 등) 포함한다.
- JAR과 달리 WEB-INF 및 META-INF 디렉토리로 사전 정의된 구조를 사용하며 실행하기 위해서 Tomcat과 같은 웹 서버 또는 웹 컨테이너(WAS)가 필요하다.

# 2. Thymeleaf

Thymeleaf는 비교적 최근에 만들어진 템플릿 엔진이며 서버사이드 자바 템플릿 엔진의 한 종류이다.

JSP와 Thymeleaf의 가장 큰 차이점은 JSP와 달리 Servlet Code로 변환되지 않다는 점이다. 따라서 비즈니스 로직과 분리되어 오로지 View에 집중할 수 있다.

**의존성 추가**

```xml
<dependency>
	  <groupId>org.springframework.boot</groupId>
	  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

**SampleController**

- `@Controller` 이기 때문에 return하는 String은 View의 이름이된다.
- Model data를 받을 model을 선언

```java
@Controller
public class SampleController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "hyeon");
        return "hello";
    }
}
```

**hello.html (기본적으로 모든 View는 src/resources/templates 디렉토리에 있음)**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
</body>
</html>
```

**SampleControllerTest**

- 요청 "/"
- 응답
  - 모델 name : hyeon
  - 뷰 이름 : hello

```java
package me.hyeon.springbootmvctemplate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(SampleController.class)
class SampleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("hello"))
                .andExpect(model().attribute("name", is("hyeon")))
                .andExpect(content().string(containsString("hyeon")));
    }
}
```

**.andDo(print()) 를 통해 렌더링 된 결과 확인**

- 이것은 Thymeleaf를 사용했기 때문이다. 만약 JSP를 사용하면 렌더링된 결과를 확인하는 것은 힘들다.
- 렌더링은 서블릿 엔진이 하고,  서블릿 엔진 자체가 JSP 템플릿을 완성시키기 때문에  응답으로 내보낼 최종적인 View를 확인하기 위해서는 서블릿 엔진 개입이 필수적이다.
- Thymeleaf는 서블릿 컨테이너의 개입 없이 독자적으로 최종적인 View를 완성한다.
- 테스트에서 사용한 mockMVC는 가짜 서블릿 컨테이너이기 때문에 실제 서블릿 컨테이너가 할 수 있는 일을 전부 할 수 없다.
- 따라서 만약 JSP를 View 템플릿 엔진으로 사용했다면 렌더링된 결과를 확인할 수 없다.

```html
MockHttpServletRequest:
      HTTP Method = GET
      Request URI = /hello
       Parameters = {}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = me.hyeon.springbootmvctemplate.SampleController
           Method = me.hyeon.springbootmvctemplate.SampleController#hello(Model)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = null

ModelAndView:
        View name = hello
             View = null
        Attribute = name
            value = hyeon

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 200
    Error message = null
          Headers = [Content-Language:"en", Content-Type:"text/html;charset=UTF-8"]
     Content type = text/html;charset=UTF-8
             Body = <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
</body>
</html>
    Forwarded URL = null
   Redirected URL = null
          Cookies = []
```

### Thymeleaf 사용

```
[https://www.thymeleaf.org/doc/articles/standarddialect5minutes.html](https://www.thymeleaf.org/doc/articles/standarddialect5minutes.html)
```

**hello.html**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1 th:text="${name}">Name</h1>
</body>
</html>
```

- Thymeleaf 사용하기 위해 th 네임스페이스 추가하기
  - <html lang="en" xmlns:th="http://www.thymeleaf.org">
- Controller에서 Model로 받아온 name 사용

**SampleController**

```java
package me.hyeon.springbootmvctemplate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("name", "hyeon");
        return "hello";
    }
}
```

웹 애플리케이션을 실행하면 컨트롤러에서 받은 name 값이 출력되는 것을 확인할 수 있다.

![https://i.imgur.com/8nNVlZO.png](https://i.imgur.com/8nNVlZO.png)

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의