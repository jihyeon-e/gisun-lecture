# 3. 스프링 부트 원리 - 자동 설정

# 1. 자동 설정 이해

**웹 어플리케이션이 아닌 어플리케이션 타입으로 설정을 한 다음 실행한 어플리케이션(응용 프로그램)**

```java
@Configuration
@ComponentScan
public class Application {
    
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
```

- @EnableAutoConfiguration으로 읽어들이는 빈 없이도 동작 가능함

**@SpringBootApplication은 아래 3가지 어노테이션을 합친 어노테이션**

```
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

- 빈은 사실 두 단계로 나눠서 읽힘

    - 스프링 부트는 @ComponentScan를 통해서 내가 만든 빈을 등록한 다음 추가적으로 @EnableAutoConfiguration으로 스프링에서 만들어 놓은 빈을 등록함

- @ComponentScan

    - @Component
      @Configuration @Repository @Service @Controller @RestController

- @EnableAutoConfiguration를 통해서 웹 어플리케이션 관련된 빈들이 자동으로 설정됨

  ![https://i.imgur.com/vO4wuKp.png](https://i.imgur.com/vO4wuKp.png)

    - 모든 jar파일에서 spring-boot-autoconfigure:2.5.3의 spring.factories 찾고,
      ■ org.springframework.boot.autoconfigure.EnableAutoConfiguration이라는 키값에 해당하는 설정파일(@Configuration)에 있는 모든 클래스들이 조건에 맞으면(@ConditionalOnXxxYyyZzz) 빈 등록을 시작함

  # 2. 자동 설정 만들기 1부: Starter와 AutoConfigure

  **구현 방법**

    1. 의존성 추가

    ```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.0.3.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```

    2. @Configuration 파일 작성

    ```java
    @Configuration
    public class HolomanConfiguration {

        @Bean
        public Holoman holoman() {
            Holoman holoman = new Holoman();
            holoman.setHowLong(5);
            holoman.setName("Keesun");
            return holoman;
        }
    }
    ```

    3. src/main/resource/META-INF에 spring.factories 파일 만들기
    4. spring.factories 안에 자동 설정 파일 추가

    ```xml
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    me.whiteship.HolomanConfiguration
    ```

    5. mvn install

  ![https://i.imgur.com/R2wk5Yu.png](https://i.imgur.com/R2wk5Yu.png)

    - 이 프로젝트를 빌드를 해서 jar파일 생성된 걸 다른 maven project에서도 갖다 쓸 수 있도록 local maven 저장소에다가 설치를 함

    - 다른 프로젝트의 pom.xml에 의존성 추가

    ```xml
    <dependency>
        <groupId>me.whiteship</groupId>
        <artifactId>jihyeon-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```

  아래와 같이 라이브러리가 추가된 것을 확인할 수 있음

  ![https://i.imgur.com/6RCpm32.png](https://i.imgur.com/6RCpm32.png)

    - 자바 어플리케이션 코드 작성

    ```java
    @Component
    public class HolomanRunner implements ApplicationRunner {

        @Autowired
        Holoman holoman;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            System.out.println(holoman);
        }
    }
    ```

    - ApplicationRunner 인터페이스 : 스프링 부트 어플리케이션이 만들어지고 띄웠을 때 자동으로 실행되는 bean을 만들고 싶을 때
    - 현재 프로젝트에서는 Holoman이라는 bean을 등록하지 않음

  ![https://i.imgur.com/vDlNr4m.png](https://i.imgur.com/vDlNr4m.png)

    - 콘솔창에 holoman이 출력됐기 때문에 Holoman이라는 bean이 있다는 것이 증명됨

    - 만약 내가 명시적으로 bean을 등록한다면 그 bean은 무시가 됨

    ```java
    @SpringBootApplication
    public class Application {

        public static void main(String[] args) {
            SpringApplication application = new SpringApplication(Application.class);
            application.setWebApplicationType(WebApplicationType.NONE);
            application.run(args);
        }

        @Bean
        public Holoman holoman() {
            Holoman holoman = new Holoman();
            holoman.setName("whiteship");
            holoman.setHowLong(60);
            return holoman;
        }
    }
    ```

  스프링 부트에서 bean을 등록하는 두 개의 방법 중 @ComponentScan으로 bean을 등록하는 것이 우선이고 그 다음이 AutoConfiguration으로 bean을 등록하는 것이기 때문에 그 bean이 위의 Holoman을 덮어쓴다. 따라서 콘솔창에는 아까와 마찬가지로 Holoman{name='Keesun', howLong=5}이 출력된다.

  # 3. 자동 설정 만들기 2부: @ConfigurationProperties

  내가 등록한 bean보다  AutoConfiguration이 더 우선시 되는 문제를 해결해보자

  덮어쓰기를 방지하려면 @ComponentScan으로 읽히는 bean들이 우선 시 되어야 함 → @ConditionalOnMissingBean 사용하기

    ```java
    @Configuration
    public class HolomanConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Holoman holoman() {
            Holoman holoman = new Holoman();
            holoman.setHowLong(5);
            holoman.setName("Keesun");
            return holoman;
        }

    }
    ```

    - @ConditionalOnMissingBean : 이 타입의 bean이 없을 때만 bean을 등록해라

  다시 mvn install하고 refresh하면 콘솔창에 Holoman{name='whiteship', howLong=60}이 출력된 것을 확인할 수 있다.

  ![https://i.imgur.com/ALxPsNv.png](https://i.imgur.com/ALxPsNv.png)

  기본 설정은 일종의 컨벤션이지만 내가 원하는대로 직접 bean을 등록하면 그 bean에 있는 값대로 그 bean이 우선시 돼서 쓰이게 된다. 이는 앞으로도 스프링 부트가 제공하는 여러가지 기능을 커스터마이징하는 기본적인 방법 중에 하나이다.

  내가 정의한 bean이 우선시 되는 것은 좋지만 나는 값만 바꾸고 싶은 것인데 bean 설정을 장황하게 해야 하는 것인가? 이렇게 하고 싶지 않을 때 application properties 작성해서 변경하기

    - application properties를 작성해서 변경할 수 있게 하려면 properties에 해당하는 것을 정의해줘야 함

  ![https://i.imgur.com/VmKxFyG.png](https://i.imgur.com/VmKxFyG.png)

    - 자바 어플리케이션 코드 작성

    ```java
    @Configuration
    @EnableConfigurationProperties(HolomanProperties.class)
    public class HolomanConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Holoman holoman(HolomanProperties properties) {
            Holoman holoman = new Holoman();
            holoman.setHowLong(properties.getHowLong());
            holoman.setName(properties.getName());
            return holoman;
        }
    }
    ```

    ```java
    @ConfigurationProperties("holoman")
    public class HolomanProperties {

        private String name;

        private int howLong;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHowLong() {
            return howLong;
        }

        public void setHowLong(int howLong) {
            this.howLong = howLong;
        }
    }
    ```

    - pom.xml에 의존성 추가

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
    ```

  bean을 재정의 할 필요없이 properties만 정의하면 된다.

  ![https://i.imgur.com/IP1iGXu.png](https://i.imgur.com/IP1iGXu.png)

  bean이 없기 때문에 자동설정에 있는 bean을 사용하게될 것이고 자동설정에 있는 bean을 사용하게 될 때 HolomanProperties를 읽어오는데 HolomanProperties에 해당하는 것은 @ConfigurationProperties를 사용해서 application.properties에 제공한 값들을 사용해서 쓰게 된다.

  스프링 부트 자동 설정에 대한 원리를 알면 앞으로 스프링 부트의 여러가지 기능들을 활용할 때 자동 설정이 어디에  있으며 어떻게 찾아보는 거고 그 자동 설정 파일이 어떻게 적용되는건지 알 수 있을 것이다.

  ### 참고

    - 백기선님의 스프링 부트 개념과 활용 강의