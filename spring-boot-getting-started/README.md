# 1\. 스프링 부트 소개

-   스프링 부트는 제품 수준의 Spring 기반의 독립적인 애플리케이션을 만들 때 빠르고 쉽게 만들 수 있게 도와준다.
-   스프링 부트는 널리 쓰이는 설정을 기본적으로 제공해줌(톰캣 설정 등)

### 목적

-   모든 Spring 개발에 대해 근본적으로 더 빠르고 광범위하게 접근할 수 있는 시작 환경을 제공한다.
-   일일히 설정하지 않아도 이미 컨벤션으로 설정되어있는 것을 제공해준다. 하지만 사용자의 요구사항에 맞게 얼마든지 그러한 설정들을 쉽고 빠르게 바꿀 수 있다. 이 점이 중요한 것이고 이것 때문에 스프링부트가 널리 사용되어진다.
-   비니지스 로직을 구현하는데 필요한 기능뿐 아니라 non-functional features-비기능 기능(내장 서버, 보안, 메트릭, 상태 확인 및 외부 구성)도 제공해준다.
-   코드 생성과 xml 설정을 더이상 하지 않는다.

# 2\. 스프링 부트 시작하기

**프로젝트 생성**

![https://i.imgur.com/YBDAx6M.png](https://i.imgur.com/YBDAx6M.png)

**메이븐으로 시작하기**

![https://i.imgur.com/uXfG7Xm.png](https://i.imgur.com/uXfG7Xm.png)

**xml파일에 스프링 부트 dependency 세팅**

```
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
    </parent>

<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

**자바 어플리케이션 코드 작성**

```
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```

**스프링 부트 실행**

![https://i.imgur.com/mPWwLRA.png](https://i.imgur.com/mPWwLRA.png)

**기존 스프링 의존성 구성 방법**

-   spring-webmvc
-   spring-context
-   jackson-core
-   등등

**스프링 부트 의존성 구성 방법**

기존처럼 수많은 dependency를 추가하지 않고 아래의 dependency만 추가해도

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

spring-boot-web과 관련된 dependency가 추가된다.

**기존 스프링 빈 xml 구성**

![https://i.imgur.com/ZbRmsg7.png](https://i.imgur.com/ZbRmsg7.png)

**스프링 부트 빈 구성**

-   스프링 빈 xml은 구성하지 않아도 된다.

**mvn package 명령어를 사용하여 바로 실행 가능한 jar 파일 생성**

![https://i.imgur.com/sXe1e1F.png](https://i.imgur.com/sXe1e1F.png)

-   추후에 생성된 jar 파일을 통해 서버에 배포하여 인텔리제이, 이클립스없이 어플리케이션을 실행할 수 있음

**이미 실행 중인 포트가 있을 때 기존 포트를 죽이는 방법**

```
터미널 -> lsof -i tcp:8080 -> kill $(lsof -t -i:8080)
```

![https://i.imgur.com/U2N4wfb.png](https://i.imgur.com/U2N4wfb.png)

## 3\. 스프링 부트 프로젝트 구조

-   자바 파일은 classpath 클래스를 통해 resources 폴더를 참조 가능하다.
-   `ClassPathResource("static/employees.dat", this.getClass().getClassLoader());`
-   classpath에 root 폴더는 resources폴더이다.

![https://i.imgur.com/8860ABr.png](https://i.imgur.com/8860ABr.png)

# 2. 스프링 부트 원리 - 의존성 관리

# 1. 의존성 관리 이해

**스프링 부트 dependency 동작 원리**

- pom.xml 에 spring-boot-starter-parent 의존성을 추가함
- spring-boot-starter-parent 는 spring-boot-dependencies를 의존하고 있음
- spring-boot-dependencies에 스프링에 사용될 버전들이 명시되어 있음

![https://i.imgur.com/9Qf4x5e.png](https://i.imgur.com/9Qf4x5e.png)

- 직접 버전을 명시해주지 않아도 spring-boot-dependencies 설정되어 있는 버전들을 사용함

**Spring boot 의존성 장점**

- 기존 스프링보다 직접 관리해야 할 의존성의 수가 줄어듦
- 스프링 부트가 버전을 관리해주기 때문에 라이브러리 간의 버전 호환을 신경쓰지 않아도 됨
- yml, plugin 설정 등 스프링 부트에 최적화된 설정들이 추가되어있음

# 2. 의존성 관리 응용

**의존성 추가**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency> 
```

위와같이 의존성을 추가하게되면 아래와같이 Spring JPA와 관련된 의존성들이 추가된 모습을 볼 수 있다.

![https://i.imgur.com/4U667il.png](https://i.imgur.com/4U667il.png)

스프링 부트가 버전을 관리하는 의존성이  아닌 경우 항상 버전까지 명시해야 한다.

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.1.0</version>
</dependency>
```

스프링 부트가 관리하는 의존성의 버전을 바꾸고 싶을 때는  properties에 추가한다.

```xml
<properties>
	  <spring.version>5.0.6.RELEASE</spring.version>
</properties>
```

아래와 같이 스프링 버전이 5.0.6으로 바뀐 것을 확인할 수 있다.

![https://i.imgur.com/3OBxsRK.png](https://i.imgur.com/3OBxsRK.png)

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의