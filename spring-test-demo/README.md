# 11. 스프링 부트 활용 - 테스트

# 스프링 핵심 기능

# 5. 테스트

## 1) spring-boot-starter-test 의존성 추가

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

![https://i.imgur.com/FpsUmFT.png](https://i.imgur.com/FpsUmFT.png)

많은 라이브러리들이 들어온다.

## 2) @SpringBootTest

스프링 부트에서는 @SpringBootTest을 사용하여 애플리케이션 테스트에 필요한 의존성들을 제공해 준다.

@SpringBootTest은 @SpringBootApplication가 있는 스프링 메인 애플리케이션을 찾아가서 여기서부터 시작하는 모든 Bean을 스캔해서 test용 애플리케이션 context를 만들어 등록한다.

@MockBean이 있을 경우에는 그 Bean만 mock으로 교체한다.

**SampleController**

```java
@RestController
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/hello")
    public String hello() {
        return "hello " + sampleService.getName();
    }
}
```

**SampleService**

```java
@Service
public class SampleService {
    public String getName() {
        return "hyeon";
    }
}
```

테스트 코드를 작성하려면 @RunWith(SpringRunner.class), @SpringBootTest, @AutoConfigureMockMvc  설정해야 한다.

@Runwith(SpringRunner.class) 는 스프링 부트와 jUnit 사이의 연결자 역할을 한다.

이 강의에서는 스프링 부트 2.0.3 버전을 사용하는데 최근 스프링 부트는 JUnit 5를 사용하기 때문에
더이상 JUnit 4에서 제공하던 @RunWith를 쓸 필요가 없고, @ExtendWith를 사용해야 하지만,
이미 스프링 부트가 제공하는 모든 테스트용 annotation에 메타 annotation으로 적용되어 있기 때문에 @ExtendWith(SpringExtension.class)를 생략할 수 있다.

### webEnvironment

@SpringBootTest는 webEnvironment 기본 값이 MOCK으로 잡혀있다.

- MOCK: mock servlet environment. 내장 톰캣 구동 안 함
- RANDON_PORT, DEFINED_PORT: 내장 톰캣 사용 함
- NONE: 서블릿 환경 제공 안 함

S**pringControllerTest**

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SpringControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception{
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello hyeon"))
                .andDo(print());
    }

}
```

위의 코드는 MockMvc를 사용하여 webEnvironment 환경(테스트의 웹 환경)이 MOCK으로 잡혀있을 때 테스트할 수 있는 방법이다.

MockMvc란 웹 애플리케이션을 애플리케이션 서버에 배포하지 않고도 스프링 mvc의 동작을 재현할 수 있는 클래스이다.

**MockMvc - perform() 메소드**

perform() 메소드는

- DispatcherServlet에 요청을 의뢰하는 역할
- MockMvcRequestBuilder클래스를 사용해 설정한 요청 데이터를 perform()의 인수로 전달
- get, post, put, delete, fileUpload 와 같은 메서드를 제공
- ResultActions 이라는 인터페이스를 반환

**ResultActions 인터페이스**

- andDo()
    - log() 실행결과를 디버깅 레벨에서 로그로 출력
    - print() 실행결과를 임의의 출력대상에 출력 출력대상을 지정하지 않으면 System.out 으로 출력한다.
- andExpect()
    - status : HTTP 상태 코드 검증
    - content : 응답 본문 내용 검증 jsonPath나 xpath와 같은 특정 콘텐츠를 위한 메서드도 제공

## 3) @MockBean

- ApplicationContext에 들어있는 빈을 Mock으로 만든 객체로 교체함
- 모든 @Test 마다 자동으로 리셋
- @SpringBootTest보다 슬라이스 테스트에서 더 많이 사용됨

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

RANDOM_PORT를 사용하면 내장 톰캣이 뜬다. 이때는 MockMvc를 쓰는 것이 아니라 테스트용 RestTemplate를 써야 한다.

여기서 문제는 Serviec단까지 가기 때문에 테스트가 너무 크다.

Serviec단까지 가지 않고 Controller만 test를 하고 싶다면 **@MockBean**을 사용하여 애플리케이션 컨텍스트에 들어있는 SampleService Bean을 여기서 만든 MockBean으로 교체한다.

그래서 실제적으로 SampleService는 원본이 아니라 mockSampleService를 쓰게 되는 것이다. 이때부터는 moking을 할 수 있다.

@MockBean을 사용하여 SampleController가 쓰는 SampleService를 moking해서 Bean을 교체한다.

S**pringControllerTest**

```java
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

		@MockBean
    SampleService mockSampleService;

    @Test
    public void hello() throws Exception{
				when(mockSampleService.getName()).thenReturn("hyeon");

        String result = testRestTemplate.getForObject("/", String.class);
        assertThat(result).isEqualTo("hello hyeon");
    }

}
```

## 4) **WebTestClient**

Java5 Spring MVC WebFlux에 새로 추가된 Rest Client 중 하나이다. 기존에 사용하던 Rest Client는 Synchronous였다. 요청 하나 보내면 끝날 때까지 기다렸다가 요청을 보낼 수 있었다.

WebTestClient는 Asynchronous하게 동작한다. 요청을 보내고 응답이 오면 그 때 CallBack Event가 발생해 실행할 수 있다.

**의존성 추가하기**

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

**SpringControllerTest**

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SampleService mockSampleService;

    @Test
    public void hello() throws Exception{
        when(mockSampleService.getName()).thenReturn("hyeon");

        webTestClient.get().uri("/hello").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("hello hyeon");
    }

}
```

## 5) 슬라이스 테스트

레이어 별로 잘라서 테스트하고 싶을 때

- @JsonTest

  Json으로 쓰면 어떻게 되는지 알아볼 때 사용한다.

    ```java
    @RunWith(SpringRunner.class)
    @JsonTest
    public class SpringControllerTest {

        @Autowired
        JacksonTester<Sample>

        @Test
        public void hello() throws Exception{
            
        }

    }
    ```

- @WebMvcTest

  일반적인 Component(Service, Repository)들은 Bean으로 등록되지 않고, web과 관련된 Bean만 등록된다.

- @WebFluxTest
- @DataJpaTest

## 6) 테스트유틸

- **OutputCapture**

  **SampleController**

    ```java
    @RestController
    public class SampleController {

        Logger logger = LoggerFactory.getLogger(SampleController.class);

        @Autowired
        private SampleService sampleService;

        @GetMapping("/hello")
        public String hello() {
            logger.info("holoman");
            System.out.println("skip");
            return "hello " + sampleService.getName();
        }
    }
    ```

  **SpringControllerTest**

    ```java
    @RunWith(SpringRunner.class)
    @WebMvcTest(SampleController.class)
    public class SpringControllerTest {

        @Rule
        public OutputCapture outputCapture = new OutputCapture();

        @Autowired
        MockMvc mockMvc;

        @MockBean
        SampleService mockSampleService;

        @Test
        public void hello() throws Exception{
            when(mockSampleService.getName()).thenReturn("hyeon");

            mockMvc.perform(get("/hello"))
                    .andExpect(content().string("hello hyeon"));

            assertThat(outputCapture.toString())
                    .contains("holoman")
                    .contains("skip");
        }
    }
    ```

  ![https://i.imgur.com/M7Tl1Ru.png](https://i.imgur.com/M7Tl1Ru.png)

  holoman은 로그에 skip은 System.out으로 출력된 것을 확인할 수 있다.

- TestPropertyValues
- TestRestTemplate
- ConfigFileApplicationContextInitializer

### 참고

- 백기선님의 스프링 부트 개념과 활용 강의