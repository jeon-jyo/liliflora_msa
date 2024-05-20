# 예약구매(liliflora-eCommerce) 프로젝트

사용자가 기본적인 유저 관리 기능을 비롯해 Wishlist, 주문내역, 주문상태 조회 등을 통해 원하는 물품의 구매 및 진행 상태를 확인할 수 있는 서비스

<br>

## 🚀 프로젝트 소개

* 마이크로서비스 아키텍처를 활용한 예약 구매 플랫폼
  * 각 프로세스마다 독립적인 확장성을 높이기 위해 MSA로 구성
* 대규모 트래픽 처리 테스트를 완료한 안정적인 서비스 구축
* 빠른 재고 처리와 한정 물품 구매를 지원

<br>

## 📍 기술 스택

* Java 17
* SpringBoot 3.2.5
* JPA / Hibernate
* Redis / MySql
* SpringSecurity / JWT
* Spring Cloud eureka / api-gateway / openfeign
* Docker / Docker-compose

<br>

## 📍 주요 기능

* Api-Gateway (api-gateway-server)
  * 클라이언트 요청에 대한 각 서비스의 엔드포인트와 REST API를 관리
  * 엔드포인트 서버에서 공통으로 필요한 인증 및 인가 기능 제공

<br>

* eureka (eureka-server)
  * Service Discovery 패턴을 구현하여 모든 마이크로 서비스의 정보를 저장하고 관리
  * 각 서버의 검색, 로드 밸런싱 및 장애 조치를 지원

<br>

* User (user-service)
  * Spring Security & JWT 를 사용한 인증 및 인가 구현
  * SMTP 와 Redis 를 활용한 이메일 인증
  * 회원가입 유효성과 이메일 인증 중복 처리
  * 개인정보를 AES 암호화하여 저장

<br>

* Product & Stock (product-service)
  * 재고 모듈을 분리하여 RedisHash Cashe 저장소 사용
  * Redisson 분산락을 이용한 동시성 이슈 해결
  * 상품 및 주문과의 트랜잭션 최종성 확보

<br>

* Orders & Wishlist (order-serivce)
  * Spring scheduler 를 활용하여 주문 상태 변경 및 관리
  * 일반 주문과 예약 주문, 주문 취소 및 반품 기능 제공

<br>

## 📝 API 명세서

**[liliflora-eCommerce API 명세서_ MSA](https://ruby-flyingfish-f67.notion.site/API-_-MSA-2d6ae53ede284eaa82ccfe8fd04a3c96?pvs=4)**

<br>

## 📝 아키텍쳐

![commerce drawio](https://github.com/jeon-jyo/liliflora_msa/assets/96943317/b32b75f7-e7fa-4cba-94c1-22c6e4b35df5)

<br>

## 📝 ERD

![liliflora_msa](https://github.com/jeon-jyo/liliflora_msa/assets/96943317/a568a714-1519-4413-9229-8465733d801e)

<br>

## 📝 프로젝트구조

```
📦 liliflora
.gitignore
README.md
api-gateway-server
.gitignore
build.gradle
gradle
wrapper
gradle-wrapper.jar
gradle-wrapper.properties
gradlew
gradlew.bat
src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ osio
│     │  │        └─ apigatewayserver
│     │  │           ├─ ApiGatewayServerApplication.java
│     │  │           ├─ filter
│     │  │           │  ├─ AuthorizationHeaderFilter.java
│     │  │           │  └─ GlobalFilter.java
│     │  │           ├─ jwt
│     │  │           │  └─ JwtTokenProvider.java
│     │  │           └─ redis
│     │  │              ├─ RedisConfig.java
│     │  │              └─ RedisService.java
│     │  └─ resources
│     │     └─ application.yml
│     └─ test
│        └─ java
│           └─ com
│              └─ osio
│                 └─ apigatewayserver
│                    └─ ApiGatewayServerApplicationTests.java
├─ build.gradle
├─ core-module
.gitignore
build.gradle
│  ├─ gradle
│  │  └─ wrapper
│  │     ├─ gradle-wrapper.jar
│  │     └─ gradle-wrapper.properties
│  ├─ gradlew
│  ├─ gradlew.bat
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ osio
│     │  │        └─ coremodule
│     │  │           └─ CoreModuleApplication.java
│     │  └─ resources
│     │     └─ application.properties
│     └─ test
│        └─ java
│           └─ com
│              └─ osio
│                 └─ coremodule
│                    └─ CoreModuleApplicationTests.java
├─ docker-compose.yml
├─ eureka-server
│  ├─ .gitignore
│  ├─ build.gradle
│  ├─ gradle
│  │  └─ wrapper
│  │     ├─ gradle-wrapper.jar
│  │     └─ gradle-wrapper.properties
│  ├─ gradlew
│  ├─ gradlew.bat
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ osio
│     │  │        └─ eurekaserver
│     │  │           └─ EurekaServerApplication.java
│     │  └─ resources
│     │     └─ application.yml
│     └─ test
│        └─ java
│           └─ com
│              └─ osio
│                 └─ eurekaserver
│                    └─ EurekaServerApplicationTests.java
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
├─ order-service
│  ├─ .gitignore
│  ├─ build.gradle
│  ├─ gradle
│  │  └─ wrapper
│  │     ├─ gradle-wrapper.jar
│  │     └─ gradle-wrapper.properties
│  ├─ gradlew
│  ├─ gradlew.bat
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ osio
│     │  │        └─ orderservice
│     │  │           ├─ OrderServiceApplication.java
│     │  │           └─ domain
│     │  │              ├─ client
│     │  │              │  ├─ product
│     │  │              │  │  ├─ ProductClient.java
│     │  │              │  │  └─ dto
│     │  │              │  │     ├─ ProductReqDto.java
│     │  │              │  │     ├─ ProductResDto.java
│     │  │              │  │     └─ StockReqDto.java
│     │  │              │  └─ user
│     │  │              │     ├─ UserClient.java
│     │  │              │     └─ dto
│     │  │              │        └─ UserResDto.java
│     │  │              ├─ order
│     │  │              │  ├─ controller
│     │  │              │  │  └─ OrderController.java
│     │  │              │  ├─ dto
│     │  │              │  │  ├─ OrderItemRequestDto.java
│     │  │              │  │  ├─ OrderItemResponseDto.java
│     │  │              │  │  ├─ OrderRequestDto.java
│     │  │              │  │  └─ OrderResponseDto.java
│     │  │              │  ├─ entity
│     │  │              │  │  ├─ Order.java
│     │  │              │  │  ├─ OrderItem.java
│     │  │              │  │  ├─ OrderStatus.java
│     │  │              │  │  └─ OrderStatusEnum.java
│     │  │              │  ├─ repository
│     │  │              │  │  ├─ OrderItemRepository.java
│     │  │              │  │  ├─ OrderRepository.java
│     │  │              │  │  └─ OrderStatusRepository.java
│     │  │              │  └─ service
│     │  │              │     ├─ OrderService.java
│     │  │              │     └─ OrderStatusScheduler.java
│     │  │              └─ wishlist
│     │  │                 ├─ controller
│     │  │                 │  ├─ WishlistController.java
│     │  │                 │  └─ WishlistInternalController.java
│     │  │                 ├─ dto
│     │  │                 │  ├─ WishItemRequestDto.java
│     │  │                 │  └─ WishItemResponseDto.java
│     │  │                 ├─ entity
│     │  │                 │  ├─ WishItem.java
│     │  │                 │  └─ Wishlist.java
│     │  │                 ├─ repository
│     │  │                 │  ├─ WishItemRepository.java
│     │  │                 │  └─ WishlistRepository.java
│     │  │                 └─ service
│     │  │                    └─ WishlistService.java
│     │  └─ resources
│     │     └─ application.properties
│     └─ test
│        └─ java
│           └─ com
│              └─ osio
│                 └─ orderservice
│                    └─ OrderServiceApplicationTests.java
├─ product-service
│  ├─ .gitignore
│  ├─ build.gradle
│  ├─ gradle
│  │  └─ wrapper
│  │     ├─ gradle-wrapper.jar
│  │     └─ gradle-wrapper.properties
│  ├─ gradlew
│  ├─ gradlew.bat
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ osio
│     │  │        └─ productservice
│     │  │           ├─ ProductServiceApplication.java
│     │  │           ├─ domain
│     │  │           │  ├─ client
│     │  │           │  │  └─ order
│     │  │           │  │     └─ dto
│     │  │           │  │        ├─ ProductReqDto.java
│     │  │           │  │        ├─ ProductResDto.java
│     │  │           │  │        └─ StockReqDto.java
│     │  │           │  ├─ product
│     │  │           │  │  ├─ controller
│     │  │           │  │  │  ├─ ProductController.java
│     │  │           │  │  │  └─ ProductInternalController.java
│     │  │           │  │  ├─ dto
│     │  │           │  │  │  └─ ProductResponseDto.java
│     │  │           │  │  ├─ entity
│     │  │           │  │  │  └─ Product.java
│     │  │           │  │  ├─ repository
│     │  │           │  │  │  └─ ProductRepository.java
│     │  │           │  │  └─ service
│     │  │           │  │     └─ ProductService.java
│     │  │           │  └─ stock
│     │  │           │     ├─ controller
│     │  │           │     │  └─ StockInternalController.java
│     │  │           │     ├─ entity
│     │  │           │     │  └─ Stock.java
│     │  │           │     ├─ repository
│     │  │           │     │  └─ StockRepository.java
│     │  │           │     └─ service
│     │  │           │        ├─ StockScheduler.java
│     │  │           │        └─ StockService.java
│     │  │           └─ global
│     │  │              └─ config
│     │  │                 ├─ RedisConfig.java
│     │  │                 └─ RedissonConfig.java
│     │  └─ resources
│     │     └─ application.properties
│     └─ test
│        └─ java
│           └─ com
│              └─ osio
│                 └─ productservice
│                    ├─ ProductServiceApplicationTests.java
│                    └─ domain
│                       └─ stock
│                          └─ service
│                             └─ StockServiceTest.java
├─ settings.gradle
└─ user-service
   ├─ .gitignore
   ├─ build.gradle
   ├─ gradle
   │  └─ wrapper
   │     ├─ gradle-wrapper.jar
   │     └─ gradle-wrapper.properties
   ├─ gradlew
   ├─ gradlew.bat
   └─ src
      ├─ main
      │  ├─ java
      │  │  └─ com
      │  │     └─ osio
      │  │        └─ userservice
      │  │           ├─ UserServiceApplication.java
      │  │           ├─ domain
      │  │           │  ├─ client
      │  │           │  │  └─ order
      │  │           │  │     └─ OrderClient.java
      │  │           │  └─ user
      │  │           │     ├─ controller
      │  │           │     │  ├─ GlobalExceptionHandler.java
      │  │           │     │  ├─ UserController.java
      │  │           │     │  └─ UserInternalController.java
      │  │           │     ├─ dto
      │  │           │     │  ├─ ResponseDto.java
      │  │           │     │  ├─ UserRequestDto.java
      │  │           │     │  └─ UserResponseDto.java
      │  │           │     ├─ entity
      │  │           │     │  ├─ User.java
      │  │           │     │  └─ UserRoleEnum.java
      │  │           │     ├─ repository
      │  │           │     │  └─ UserRepository.java
      │  │           │     └─ service
      │  │           │        ├─ MailSendService.java
      │  │           │        ├─ RedisService.java
      │  │           │        └─ UserService.java
      │  │           └─ global
      │  │              ├─ config
      │  │              │  ├─ EmailConfig.java
      │  │              │  └─ RedisConfig.java
      │  │              ├─ jwt
      │  │              │  ├─ JwtAuthenticationFilter.java
      │  │              │  ├─ JwtToken.java
      │  │              │  └─ JwtTokenProvider.java
      │  │              ├─ security
      │  │              │  ├─ CustomUserDetailsService.java
      │  │              │  ├─ SecurityConfig.java
      │  │              │  └─ UserDetailsImpl.java
      │  │              └─ util
      │  │                 └─ EncryptUtil.java
      │  └─ resources
      │     └─ application.properties
      └─ test
         └─ java
            └─ com
               └─ osio
                  └─ userservice
                     └─ UserServiceApplicationTests.java
```

<br>

## 📌 트러블슈팅

* **[OrderItems()가 null 로 받아오는 문제](https://liliflora.tistory.com/373)**
* **[OrderStatus Enum이 안 바뀌는 문제](https://liliflora.tistory.com/397)**
* **[동시성 제어를 위한 락이 잘 안되는 문제](https://liliflora.tistory.com/399)**

<br>

## ✨ 기술적 의사결정

* **[결제진입 API에서 결제완료까지의 관리](https://liliflora.tistory.com/392)**
* **[ApiGateway와 기존의 인증인가 방식](https://liliflora.tistory.com/384)**
* **[프론트 없이 jwt 로그아웃 구현](https://liliflora.tistory.com/385)**

<br>

## 📑 부하테스트

* **[재고감소의 동시성제어, 분산락 테스트](https://liliflora.tistory.com/396)**

<br>

## 📑 기타 기록

* **[일일 기록 보드](https://ruby-flyingfish-f67.notion.site/e43d92155170485693c45853df4819d8?v=b886208c7384456294d6020df46eaac3&pvs=4)**
