# 8. 스프링 부트 활용 - 기능 소개, Spring Application

스프링 부트가 제공하는 여러 기능을 사용하며 원하는대로 커스터마이징 하는 방법을 학습한다.

# 스프링 부트가 제공하는 기능

스프링부트가 제공하는 기능은 두 가지로 나뉠 수 있다.

1. 어떤 기술을 사용하더라도 스프링 부트가 기본적으로 제공하는 핵심 기능들
2. 각종 기술과 연동해서 사용하는 기능들

![https://i.imgur.com/nto0Cb1.png](https://i.imgur.com/nto0Cb1.png)

# 스프링 핵심 기능

# 1. SpringApplication

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringinitApplication.class, args);
    }
}
```

보통 SpringApplication을 실행 할 때 위 한 줄을 추가한다.

하지만 이렇게 하면 SpringApplication이 제공하는 다양한 기능을 커스터마이징해서 사용하기 어렵다.

```java
SpringApplication app = new SpringApplication(SpringinitApplication.class);
app.run(args);
```

따라서 SpringApplication을 커스터마이징하여 사용하고자 할 때는 위와 같이 SpringApplication 인스턴스를 생성해 실행해야 한다.

## 1) Log Level 설정

아무런 옵션 변경없이 실행하면 기본적으로 애플리케이션의 로그레벨은 INFO 레벨이다.

![https://i.imgur.com/DvPXMyd.png](https://i.imgur.com/DvPXMyd.png)

디버그 모드로 SpringApplication을 실행하고 싶으면 `Edit Configurations...`를 선택한 다음

VM options에 `-Ddebug` 또는 Program arguments에 `--debug`라고 적어주면 (둘중 하나만)

**디버그모드**로 애플리케이션이 동작하는 것을 확인할 수 있다.

![https://i.imgur.com/kHi3il1.png](https://i.imgur.com/kHi3il1.png)

애플리케이션 로그도 디버그 레벨까지 출력한다.

![https://i.imgur.com/gDYCUxc.png](https://i.imgur.com/gDYCUxc.png)

디버그 레벨로 찍힐 때 특이한 점은 어떠한 자동설정이 적용이 됐는지, 어떠한 자동설정이 왜 적용이 안됐는지 로그를 볼 수 있다.

## 2) FailureAnalyzer

애플리케이션이 에러가 났을 때, 에러 메세지를 보기 쉽게 출력해주는 기능이다. 기본적으로 스프링 부트 애플리케이션에는 여러 FailureAnalyzer들이 등록되어 있다.

## 3) Banner

Banner란 스프링 부트 애플리케이션 실행 화면에서 나오는 Spring이라는 글자이다. 배너를 바꾸고 싶으면
src - main - resources에 banner.txt를 추가하면 된다. (gif, jpg, png 파일도 가능)

![https://i.imgur.com/Jrrgxfu.png](https://i.imgur.com/Jrrgxfu.png)

- resources가 아닌 다른 위치에 배너 파일을 넣고 싶을 때 application.properties 파일에 다음과 같이 추가한다.

```
spring.banner.location=classpath:directory\banner.txt
```

- 기본 인코딩은 UTF-8이라 시스템 콘솔의 인코딩도 확인해야 한다.
- 이미지 배너는 다음과 같은 property들도 사용할 수 있다.

```
spring.banner.image.location=classpath:banner.jpg
spring.banner.image.width=가로사이즈(100이면 원본 이미지 사이즈)
spring.banner.image.height=세로사이즈
spring.banner.image.margin=여백
spring.banner.image.invert=인버터(1이면 반전)
```

- 배너를 끄고 싶다면 스프링 부트 애플리케이션에 다음과 같이 추가한다.

```
app.setBannerMode(Banner.Mode.OFF);
```

**코딩으로 배너 구현하기**

SpringApplication에서 setBanner를 사용해서 코딩으로 배너를 변경할 수 있다.

만약 banner.txt 파일이 존재한다면 printBanner()는 무시된다.

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringinitApplication.class);
        app.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
                out.println("========================");
                out.println("hyeon Spring Boot Banner");
                out.println("========================");
            }
        });
        app.run(args);
    }
}
```

![https://i.imgur.com/1M62cQa.png](https://i.imgur.com/1M62cQa.png)

**배너 변수**

Banner에서 쓸 수 있는 여러 변수들이 존재한다.

- ${spring-boot.version} : spring boot 버전 출력
- 일부 변수는 MANIFEST.MF 파일이 생성되어야만 출력이 된다.

## 4) SpringApplicationBuilder

SpringApplication을 실행하는 방법은 SpringApplication.run만 존재하는게 아니고

SpringApplication 자체를 인스턴스로 만들어서 커스터마이징 할 수 있다.

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(SpringinitApplication.class)
                .run(args);
    }
}
```

## 5) ApplicationEvent 등록

Spring Boot에서는 기본적으로 제공해주는 다양한 시점의 이벤트들이 있다.

예를 들면 Application이 시작될 때, Application context를 만들었을 때, Application이 잘 구동이 되었을 때, Application이 준비가 됐을 때, 실패했을 때 등

**ApplicationEvent 등록**

- ApplicationListener 인터페이스를 상속받는 클래스는 어떤 이벤트의 리스너를 만드는지에 대한 타입을 줘야 한다.

```java
@Component
public class SampleListener implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        System.out.println("========================");
        System.out.println("Application is Starting");
        System.out.println("========================");
    }
}
```

- ApplicationStartingEvent
    - 주의할 점은 **이벤트 발생 기점이 언제인가**(Application context가 만들어졌느냐 안만들어졌느냐)이다.
    - Application context가 만들어지기 이전에 발생한 이벤트는 Bean으로 등록한다 하더라도 리스너가 동작하지 않기 때문에 이런 경우에는 직접 등록을 해주어야 한다.

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringinitApplication.class);
        app.addListeners(new SampleListener());
        app.run(args);
    }
}
```

- app.addListeners(new SampleListener());
    - `addListners`를 통해 리스너 객체를 만들어 넘겨준다.
- 직접 등록할 때는 Bean 등록이 의미없기 때문에 `SampleListner`에서 추가했던 `@Component` 어노테이션을 제거했다.

![https://i.imgur.com/DgMcso3.png](https://i.imgur.com/DgMcso3.png)

**Application Context 만들어진 다음에 발생하는 이벤트 리스너 등록**

- ApplicationStartedEvent
    - Bean으로 등록된 Listner만 생성하면 자동으로 등록된다.

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringinitApplication.class);
        app.run(args);
    }
}
```

```java
@Component
public class SampleListener implements ApplicationListener<ApplicationStartedEvent> {
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("========================");
        System.out.println("Application is Starting");
        System.out.println("========================");
    }
}
```

![https://i.imgur.com/PH21ZI1.png](https://i.imgur.com/PH21ZI1.png)

- 결론적으로 ApplicationEvent 등록할 때 해당 이벤트가 Application Context 만들어지기 전에 발생하는지, 후에 발생하는지가 가장 중요하다.

## 6) WebApplicationType 설정

```java
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringinitApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }
}
```

**WebApplication의 세 가지 타입**

1. SERVLET
    - Spring MVC가 있다면 기본적으로 SERVLET으로 작동한다.
2. REACTIVE
    - Spring WebFlux가 있다면 기본적으로 REACTIVE으로 작동한다.
3. NONE
    - 둘 다 없으면 NONE으로 동작한다.

**Type 적용 순서**

WebApplication의 Type은 **SERVLET** → **REACTIVE** → **NONE** 순서로 적용된다

## 7) 애플리케이션 아규먼트 사용하기

Application argument란 위 Program arguments에서 `--`로 들어오는 argument이고,
VM options는 `-D`로 들어온다.

VM options는 `foo`, Program arguments는 `bar`로 지정하였다.

![https://i.imgur.com/z4QaPTd.png](https://i.imgur.com/z4QaPTd.png)

어떤 Bean에 생성자가 한 개고, 그 생성자의 Parameter가 Bean일 경우에는 그 Bean을 스프링이 알아서 주입해준다.

```java
@Component
public class SampleListener {
    public SampleListener(ApplicationArguments arguments) {
        System.out.println("foo : " + arguments.containsOption("foo"));
        System.out.println("bar : " + arguments.containsOption("bar"));
    }
}
```

코드를 작성하고 argument를 확인해보면,

![https://i.imgur.com/RJco1Fi.png](https://i.imgur.com/RJco1Fi.png)

VM options는 false, Program arguments는 true를 출력한다.

JVM option는 Application argument가 아니다. 오로지 -- 옵션을 준 것만 argument이다.

## 8) 애플리케이션 실행한 뒤 뭔가 실행하고 싶을 때

**ApplicationRunner 또는 CommandLineRunner**

```java
@Component
@Order(1)
public class SampleListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("foo : " + args.containsOption("foo"));
        System.out.println("bar : " + args.containsOption("bar"));
    }
}
```

- ApplicationRunner
    - 제공하는 유용한 메소드들을 사용할 수 있다.
    - Application argument에 대한 추상화된 api를 사용하여 코딩을 할 수 있다.
    - @Order를 사용하여 순서 지정 가능

```java
@Component
public class SampleListener implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(args).forEach(System.out::println);
    }
}
```

- CommandLineRunner
    - ApplicationRunner와 마찬가지로 Application argument만 받을 수 있고 JVM option은 무시된다.
    - CommandLineRunner보단 ApplicationRunner를 더 추천한다.

# 9. 스프링 부트 활용 - 외부 설정

# 스프링 핵심 기능

# 2. 외부 설정

외부설정 파일이란 애플리케이션에서 사용하는 여러 설정값들을 애플리케이션 안 혹은 밖에 정의할 수 있는 기능이다.

### Classpath란?

Classpath란 **자바 가상머신이 실행할 때 class파일을 찾는데 그 때 기준이 되는 경로**를 의미한다.

Spring에서는 이 classpath를 통해 필요한 resources를 가져와 사용한다.

### 사용할 수 있는 외부 설정

- properties
- YAML
- 환경 변수
- 커맨드 라인 아규먼트

## 1) **application.properties**

- 가장 흔히 볼 수 있는 설정 파일
- 스프링 부트가 애플리케이션을 구동할 때 자동으로 로딩하는 파일 이름 (규약, 컨벤션)

key - value 형태로 값을 정의해두면 application에서 참조해서 사용할 수 있다.

```
**application.properties 파일**

hyeon.name = hyeon
```

@Value("${hyeon.name}")을 사용하여 application**.**properties파일에서 선언했던 값을 사용할 수 있다.

```java
@Component
public class SampleListener implements ApplicationRunner {

    @Value("${hyeon.name}")
    private String name;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=============");
        System.out.println(name);
        System.out.println("=============");
    }
}
```

![https://i.imgur.com/MMvrkie.png](https://i.imgur.com/MMvrkie.png)

## 2) 프로퍼티 우선 순위

1. 유저 홈 디렉토리에 있는 spring-boot-dev-tools.properties

2. 테스트에 있는 @TestPropertySource

3. @SpringBootTest 애노테이션의 properties 애트리뷰트

4. 커맨드 라인 아규먼트

- `mvn package` 로 빌드한 뒤 jar 파일 생성하기
- `java -jar target/spring-boot-embedded-server-1.0-SNAPSHOT.jar --hyeon.name=hyeon`

  ![https://i.imgur.com/mvSKbqf.png](https://i.imgur.com/mvSKbqf.png)

    - properties 파일에 들어있는 값을 overriding해서 hyeon이 출력된다.
    - 커맨드 라인에서 아규먼트를 fourth로 직접적으로 줄 수 있다
    - properties 안의 값을 overriding

5. SPRING_APPLICATION_JSON (환경 변수 또는 시스템 프로티) 에 들어있는 프로퍼티

6. ServletConfig 파라미터

7. ServletContext 파라미터

8. java:comp/env JNDI 애트리뷰트

9. System.getProperties() 자바 시스템 프로퍼티

10. OS 환경 변수

11. RandomValuePropertySource

12. JAR 밖에 있는 특정 프로파일용 application properties

13. JAR 안에 있는 특정 프로파일용 application properties

14. JAR 밖에 있는 application properties

15. JAR 안에 있는 application properties

16. @PropertySource

17. 기본 프로퍼티 (SpringApplication.setDefaultProperties)

- 자동 설정

## 3) application.properties 자체의 우선 순위

application.properties는 다음과 같은 위치에 넣을 수 있는데 우선순위가 높은 파일이 낮은 파일을 덮어 쓴다.

1. file:./config/

2. file:./

3. classpath:/config/

4. classpath:/

**랜덤값 설정하기**

```
hyeon.age = ${random.int}
```

**플레이스 홀더**

- name = hyeon
- fullName = ${name} baik

## 4) 타입-세이프 프로퍼티 @ConfigurationProperties

```xml
hyeon.name = hyeon
hyeon.age = ${random.int(0,100)}
hyeon.fullName = ${hyeon.name} Kim
```

같은 key로 시작하는 외부 설정이 많을 경우에 그것을 묶어서 하나의 Bean으로 등록하는 방법이 있다.

```java
@Component
@ConfigurationProperties("hyeon")
public class HyeonProperties {
    private String name;
    private int age;
    private String fullName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
```

@ConfigurationProperties("hyeon")로 프로퍼티의 키값 hyeon으로 마크해주면 프로퍼티의 값들을 멤버 병수에 자동으로 바인딩을 해준다.(camelCase, kebab-case, snake_case 상관없이 Relaxed Binding을 함)

다른 클래스에 사용하기 위해서 @Component을 사용하여 Bean으로 등록해야 한다.

```java
@SpringBootApplication
@EnableConfigurationProperties(HyeonProperties.class)
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringinitApplication.class, args);
    }
}
```

@Configuration을 사용한 클래스에 @EnableConfigurationProperties를 추가로 설정하여 ConfigurationProperties를 달고 있는 클래스들을 Bean으로 등록해줘야 하지만, 스프링 부트를 사용할 경우에는 자동구성에 기본적으로 포함되어 있기 때문에 @EnableConfigurationProperties를 설정할 필요가 없다.

```java
@Component
public class SampleListener implements ApplicationRunner {

    @Autowired
    HyeonProperties hyeonProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("========");
        System.out.println(hyeonProperties.getName());
        System.out.println(hyeonProperties.getAge());
        System.out.println("========");
    }
}
```

SampleRunner에 HyeonProperties를 주입 받아서 사용하면 된다.

![https://i.imgur.com/DdwvjJC.png](https://i.imgur.com/DdwvjJC.png)

**spring-boot-configuration-processor**

- META 정보를 생성해주는 플러그인
- @ConfigurationProperties를 사용하기 위한 의존성

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

@ConfigurationProperties가 붙어있는 Bean들의 META 정보를 생성해줘서 나중에 application.properties에서 자동완성을 사용할 수 있다.

**@Validated**

- 프로퍼티 값 검증
- JSR-303 구현체 (@NotNull, ...)

**@Value**

SpEL 을 사용할 수 있지만 위에 있는 기능들은 전부 사용하지 못한다.

# 3. Profile

Profile이란 스프링에서 특정 Profile에서만 특정한 빈을 등록하고 싶다거나, 애플리케이션 동작을 특정 Profile에서 설정을 다르게 하고 싶을 때 사용하는 기능이다.

**BaseConfiguration.java**

```java
@Profile("prod")
@Configuration
public class BaseConfiguration {
    @Bean
    public String hello() {
        return "hello";
    }
}
```

**SampleRunner.java**

```java
@Profile("test")
@Configuration
public class TestConfiguration {
    @Bean
    public String hello() {
        return "hello test!";
    }
}
```

**application.properties**

```
spring.profiles.active=prod
```

![https://i.imgur.com/6FFQDo1.png](https://i.imgur.com/6FFQDo1.png)

- 어떤 프로파일을 활성화할 것인가?
    - `spring.profiles.active`
- 어떤 프로파일을 추가할 것인가?
    - `spring.profiles.include`

# 10. 스프링 부트 활용 - 로깅

# 스프링 핵심 기능

# 4. 로깅

## 스프링 부트 기본 로거 설정

## 1) 로깅 퍼사드 vs 로거

- 로깅 퍼사드 : **Commons Logging**, SLF4j
- 로거 : JUL, Log4J2, **Logback**

## 2) 스프링 5에 로거 관련 변경 사항

스프링부트는 기본적으로 **Commons Logging**을 사용한다.

**로깅 퍼사드**란 실제 로깅을 하는게 아니라 로거 API들을 추상화해놓은 인터페이스들이다.

프레임워크를 사용하는 애플리케이션들은 로거를 원하는 대로 쓰기 위해 밑에 있는 로거를 원하는 대로 바꿔 낄 수 있는 로깅 퍼사드를 사용하였다.

Commons Logging은 런타임시 클래스 로딩 관련 이슈 같은 여러 가지 문제들이 있었다. 그래서 Commons Logging을 기피하게 되었고 **SLF4j**라는 구조적으로 더 심플하고 안전한 새로운 라이브러리가 만들어졌다.

그렇다면 스프링은 왜 SLF4j를 사용하지 않고 Commons Logging을 사용할까?
SLF4j가 나오기 이전에 스프링 코어 모듈이 만들어졌기 때문이다.

Spring 1.0에서는 pom.xml 의존성 설정을 통해 Commons Logging을 exclusion하고 SLF4j를 추가해서 사용했지만 이것은 귀찮은 방법이다.

따라서 Spring 5부터는 pom.xml에 exclusion시키지 않고 Commons Logging 코드를 자체적으로 컴파일 시점에 SLF4j 등으로 변경할 수 있는 기능을 가진 **Spring-JCL**이라는 모듈을 만들었다.

```
**Spring-JCL**
Commons Logging → SLF4j → Logback
```

정리하자면 Commons Logging을 쓰든 SLF4j을 쓰든 SLF4j의 구현체인 **Logback**으로 변경되기 때문에 스프링 부트는 최종적으로 **Logback**을 쓰는 것이다.

Dependency를 보면 logback을 확인할 수 있다.

![https://i.imgur.com/iQVyES3.png](https://i.imgur.com/iQVyES3.png)

## 3) **스프링 부트 로깅**

- 더 많은 로그을 찍고싶다면
    - --debug (일부 핵심 라이브러리만 디버깅 모드로)

      VM options에 -Ddebug를 주거나 Program arguments에 --debug를 주면 된다.

  ![https://i.imgur.com/VWMS7Ho.png](https://i.imgur.com/VWMS7Ho.png)

    - --trace (전부 다 디버깅 모드로)

## 커스터마이징

application.properties

```
spring.output.ansi.enabled=always
logging.path=logs
logging.level.me.hyeon=debug
```

- 컬러 출력: spring.output.ansi.enabled=always
- 파일 출력: logging.file=file 또는 logging.path=directory
- 로그 레벨 조정: logging.level.패키지 = 로그 레벨

## 4) 커스텀 로그 설정 파일 사용하기

- Logback: logback-spring.xml

  resources 패키지 내에 위치한다.

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="org/springframework/boot/logging/logback/base.xml" />
        <logger name="me.hyeon" level="DEBUG"/>
    </configuration>
    ```

- Log4J2: log4j2-spring.xml
- JUL (비추): logging.properties
- Logback extension

  파일 내 Logback Extension을 사용하여 Logback을 커스텀할 수 있다.

    - 프로파일
    - Environment 프로퍼티

## 5) 로거를 Log4j2로 변경하기

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

pom.xml에서 spring-boot-starter-logging를 exclusion시키고, spring-boot-starter-log4j2를 추가한다.

최종적으로 log4j2가 로그 메세지를 찍는 것이다.

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의