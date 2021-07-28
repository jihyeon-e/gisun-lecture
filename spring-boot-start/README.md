# 4. 스프링 부트 원리 - 내장 서버

# 1. 내장 웹 서버 이해

Application을 실행하면 웹 서버가 뜨기 때문에 사람들이 가끔 스프링 부트 자체가 웹 서버인 줄 안다.

엄연히 말하면 스프링 부트는 서버가 아니다. 스프링 부트에 ServletWebServerFactoryAutoConfiguration 구성을 통해 내장형 톰캣이 동작한다.

- WebApplicationType.NONE : 웹 서버가 아닌 형태로 실행하는 방법

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

}
```

- **내장 톰캣을 사용하지 않고 톰캣 객체 생성하여 웹 서버 실행해보기**
- 톰캣 객체 생성
- 포트 설정
- 톰캣에 컨텍스트 추가
- 서블릿 만들기
- 톰캣에 서블릿 추가
- 컨텍스트에 서블릿 맵핑
- 톰캣 실행 및 대기

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addContext("/", "/");

        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                PrintWriter writer = resp.getWriter();
                writer.println("<html><head><title>");
                writer.println("Hey, Tomcat");
                writer.println("</title></head>");
                writer.println("<body><h1>Hello Tomcat</h1></body>");
                writer.println("</html>");
            }
        };

        String servletName = "helloServlet";
        tomcat.addServlet("/", servletName, servlet);
        context.addServletMappingDecoded("/hello", servletName);

        tomcat.start();
        tomcat.getServer().await();
    }

}
```

hello 요청 시 Hello Tomcat이 출력되는 것을 확인할 수 있다.

![https://i.imgur.com/YBgjatO.png](https://i.imgur.com/YBgjatO.png)

이 모든 과정을 보다 상세히 또 유연하게 설정하고 실행해주는게 바로 스프링 부트의 자동 설정이다.

### **ServletWebServerFactoryAutoConfiguration**

servlet 웹 서버 생성, servlet 컨테이너를 만듦

- TomcatServletWebServerFactoryCustomizer (서버 커스터마이징)

### **DispatcherServletAutoConfiguration**

dispatcher servlet 만들고 등록, httpServlet를 상속해서 만든 스프링 mvc의 핵심 클래스임

- 둘이 따로 떨어져있는 이유? servlet 컨테이너는 pom.xml 설정에 따라 달라질 수 있지만, servlet은 변하지 않기 때문에 분리되어있음

# 2. 내장 웹 서버 응용 1부 : 컨테이너와 포트

## 1) Tomcat이 아닌 다른 서블릿 컨테이너로 변경하기

기본적으로 servlet 기반의 web mvc 어플리케이션을 개발할 때 기본적으로 tomcat이 들어있기 때문에 다른 servlet 컨테이너로 변경하고자 하면 우선 이 dependency를 빼

![https://i.imgur.com/QEG3GyA.png](https://i.imgur.com/QEG3GyA.png)

- tomcat dependency 빼기

```xml
<dependencies>
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-web</artifactId>
		    <exclusions>
		        <exclusion>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-tomcat</artifactId>
		        </exclusion>
		    </exclusions>
		</dependency>
</dependencies>
```

- jetty dependency 추가

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

![https://i.imgur.com/DgRPf0q.png](https://i.imgur.com/DgRPf0q.png)

위와 같이 tomcat은 사라지고 jetty가 들어온 것을 확인할 수 있다.

## 2) 웹 서버 사용하지 않기 - properties 사용

**application.properties**

- 웹 서블릿 컨테이너 의존성이 Classpath에 있다 하더라도 무시하고 non-web-application으로 실행함

    ```xml
    spring.main.web-application-type=none
    ```

- HTTP 포트 변경
- server.port

    ```xml
    server.port=7070
    ```

    - 랜덤 포트 사용

  랜덤으로 사용 가능한 포트를 찾아서 포트를 띄워줌

    ```xml
    server.port=0
    ```

  ## 3) ApplicationListner로 포트 정보 알아 내기

    ```java
    @Component
    public class PortListner implements ApplicationListener<ServletWebServerInitializedEvent> {

        @Override
        public void onApplicationEvent(ServletWebServerInitializedEvent servletWebServerInitializedEvent) {
            ServletWebServerApplicationContext applicationContext
                    = servletWebServerInitializedEvent.getApplicationContext();
            System.out.println(applicationContext.getWebServer().getPort());
        }
    }
    ```

  getApplicationContext 서블릿 웹 서버의 컨텍스트이기 때문에 웹 서버를 알 수 있고 그 웹서버를 통해 포트 정보를 확인할 수 있다.

  ![https://i.imgur.com/AfW1TM0.png](https://i.imgur.com/AfW1TM0.png)

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의